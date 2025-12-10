import { load } from '@tauri-apps/plugin-store';
import { readDir,exists, readTextFile } from '@tauri-apps/plugin-fs';
import { join, dirname } from '@tauri-apps/api/path';
import { appDataDir } from '@tauri-apps/api/path';
import JSZip from 'jszip';
import { writeFile, mkdir } from '@tauri-apps/plugin-fs';

export interface LocalModInfo {
  name: string;      // 显示名称
  fileName: string;  // 文件名
  fullPath: string;  // 绝对路径
  isEnabled: boolean; // 是否启用
}

/**
 * 自动识别游戏路径
 * @returns 识别到的游戏路径，如果识别失败返回null
 */
export const autoDetectGamePath = async (): Promise<string | null> => {
  try {
    // 动态获取用户LocalLow路径
    // 注意：Tauri没有直接获取LocalLow路径的API，我们需要从用户目录构建
    const homeDir = await appDataDir();
    // 从appDataDir路径获取用户目录，然后构建LocalLow路径
    // appDataDir通常是 C:\Users\用户名\AppData\Roaming
    const userDir = homeDir.split('\\AppData\\')[0];
    const logPath = `${userDir}\\AppData\\LocalLow\\LanPiaoPiao\\PlantsVsZombiesRH\\Player.log`;
    
    try {
      const logContent = await readTextFile(logPath);
      
      // 使用正则表达式匹配游戏目录
      // 匹配模式：Loading player data from F:/Game/融合版整合包/PlantsVsZombiesRH_Data/data.unity3d
      const regex = /Loading player data from (.+?)\/PlantsVsZombiesRH_Data\/data\.unity3d/;
      const match = logContent.match(regex);
      
      if (match && match[1]) {
        const detectedPath = match[1];
        
        // 保存识别到的路径
        const store = await load('store.json');
        await store.set('gamePath', detectedPath);
        await store.save();
        
        return detectedPath;
      } else {
        console.error('未能从日志文件中识别游戏路径');
        return null;
      }
    } catch (fileError) {
      console.error('读取日志文件失败:', fileError);
      return null;
    }
  } catch (error) {
    console.error('自动识别游戏路径失败:', error);
    return null;
  }
};

/**
 * 获取配置中的游戏路径
 */
export const getGamePath = async (): Promise<string | null> => {
  try {
    const store = await load('store.json');
    return await store.get<string>('gamePath') ?? null;
  } catch (err) {
    console.error('无法读取配置文件:', err);
    return null;
  }
};

/**
 * 获取并保存游戏使用的mod框架
 * 优先检测 BepInEx，其次检测 MelonLoader
 */
export const getModFramework = async (): Promise<string | null> => {
  try {
    const gamePath = await getGamePath();
    if (!gamePath) return null;

    let frameworkName: string | null = null;

    // 1. 拼接可能的路径
    const bepInExPath = await join(gamePath, 'BepInEx');
    const melonLoaderPath = await join(gamePath, 'MelonLoader');

    // 2. 检测是否存在
    // 注意：exists 需要在 import 中引入
    const hasBepInEx = await exists(bepInExPath);
    
    if (hasBepInEx) {
      frameworkName = 'BepInEx';
    } else {
      // 只有 BepInEx 不存在时才去检查 MelonLoader
      const hasMelon = await exists(melonLoaderPath);
      if (hasMelon) {
        frameworkName = 'MelonLoader';
      }
    }

    // 3. 如果找到了框架，保存到 Store
    if (frameworkName) {
      const store = await load('store.json');
      await store.set('modFramework', frameworkName);
      await store.save(); // 必须调用 save 才能持久化
      // console.log('框架已更新为:', frameworkName);
    }

    return frameworkName;

  } catch (err) {
    console.error('检测 Mod 框架失败:', err);
    return null;
  }
};



/**
 * 获取 BepInEx/plugins 完整路径
 */
export const getPluginsDirectory = async (): Promise<string | null> => {
  const gamePath = await getGamePath();
  if (!gamePath) return null;
  // 根据不同的mod框架返回不同的路径
  const modFramework = await getModFramework();
  if (modFramework === 'MelonLoader') {
    return await join(gamePath, 'Mods');
  }
  // 确保跨平台路径正确拼接
  return await join(gamePath, 'BepInEx', 'plugins');
};

/**
 * 获取MelonLoader/plugins 完整路径
 */
export const getMelonLoaderPluginsDirectory = async (): Promise<string | null> => {
  const gamePath = await getGamePath();
  if (!gamePath) return null;
  // 确保跨平台路径正确拼接
  return await join(gamePath, 'Mods');
};

/**
 * 扫描本地模组列表
 * @returns 模组列表数组，如果扫描失败返回空数组
 */
export const scanLocalMods = async (): Promise<LocalModInfo[]> => {
  const pluginsDir = await getPluginsDirectory();
  if (!pluginsDir) return [];

  try {
    const entries = await readDir(pluginsDir);
    const mods: LocalModInfo[] = [];

    for (const entry of entries) {
      if (entry.isDirectory) continue;

      const name = entry.name;
      const isDll = name.endsWith('.dll');
      const isDisabled = name.endsWith('.dll-close');

      if (isDll || isDisabled) {
        mods.push({
          name: name,
          fileName: name,
          fullPath: await join(pluginsDir, name),
          isEnabled: isDll,
        });
      }
    }
    return mods;
  } catch (err) {
    console.error('扫描插件目录失败:', err);
    return [];
  }
};

/**
 * 将ZIP文件解压到游戏目录
 * @param zipData ZIP文件的数据
 * @param gamePath 游戏目录
 */
export const extractZipToGameDir = async (zipData: Uint8Array, gamePath: string) => {
  const zip = new JSZip();
  const loadedZip = await zip.loadAsync(zipData.buffer);
  
  for (const [relativePath, fileEntry] of Object.entries(loadedZip.files)) {
    const targetPath = await join(gamePath, relativePath);

    if (fileEntry.dir) {
      await mkdir(targetPath, { recursive: true });
    } else {
      const parentDir = await dirname(targetPath);
      await mkdir(parentDir, { recursive: true });
      const content = await fileEntry.async('uint8array');
      await writeFile(targetPath, content);
    }
  }
};