import { MessagePlugin } from 'tdesign-vue-next';
import { getVersion } from '@tauri-apps/api/app'; 

// 获取当前版本号
let currentVersion: string = '1.0.0'; // 默认值，实际版本将通过getVersion获取

// 初始化版本号
const initializeVersion = async () => {
  try {
    currentVersion = await getVersion();
    console.log('当前应用版本:', currentVersion);
  } catch (error) {
    console.error('获取应用版本失败:', error);
    // 保持使用默认版本号
  }
};

// 立即初始化版本号
initializeVersion();
const updateCheckInterval = 24 * 60 * 60 * 1000; // 24小时检查一次
const API_URL = 'https://mod.ehre.top/api/public/getPublishVersion';

// 版本信息接口
interface VersionInfo {
  id: string;
  versionNumber: string;
  versionDescription: string;
  updateContent: string;
  downloadUrl: string | null;
  isReleased: boolean;
  createdTime: string;
  updatedTime: string;
}

// API响应接口
interface ApiResponse {
  code: number;
  msg: string | null;
  data: VersionInfo;
}

// 检查版本号是否比当前版本新
const isNewerVersion = (latestVersion: string, currentVersion: string): boolean => {
  const latestParts = latestVersion.split('.').map(Number);
  const currentParts = currentVersion.split('.').map(Number);
  
  for (let i = 0; i < Math.max(latestParts.length, currentParts.length); i++) {
    const latestPart = latestParts[i] || 0;
    const currentPart = currentParts[i] || 0;
    
    if (latestPart > currentPart) return true;
    if (latestPart < currentPart) return false;
  }
  
  return false;
};

// 调用API检查是否有新版本
export const checkForUpdates = async (): Promise<{
  hasUpdate: boolean;
  currentVersion: string;
  newVersion?: string;
  updateUrl?: string;
  updateContent?: string;
  message?: string;
  createdTime?: string;
}> => {
  try {
    // 确保版本号已初始化
    if (currentVersion === '1.0.0') {
      await initializeVersion();
    }
    
    const response = await fetch(API_URL);
    
    if (!response.ok) {
      throw new Error(`HTTP error! status: ${response.status}`);
    }
    
    const apiResponse: ApiResponse = await response.json();
    
    if (apiResponse.code !== 0) {
      throw new Error(apiResponse.msg || 'API返回错误');
    }
    
    const versionData = apiResponse.data;
    
    // 检查是否已发布
    if (!versionData.isReleased) {
      return {
        hasUpdate: false,
        currentVersion,
        message: '当前已是最新版本'
      };
    }
    
    // 检查版本号
    if (isNewerVersion(versionData.versionNumber, currentVersion)) {
      return {
        hasUpdate: true,
        currentVersion,
        newVersion: versionData.versionNumber,
        updateUrl: versionData.downloadUrl || undefined,
        updateContent: versionData.updateContent || '',
        createdTime: versionData.createdTime || '未知'
      };
    } else {
      return {
        hasUpdate: false,
        currentVersion,
        message: '当前已是最新版本'
      };
    }
  } catch (error) {
    console.error('检查更新失败:', error);
    throw error;
  }
};

// 检查是否需要提醒更新（基于时间间隔）
export const shouldCheckForUpdates = () => {
  const lastCheckTime = localStorage.getItem('lastUpdateCheck');
  if (!lastCheckTime) return true;
  
  const now = new Date().getTime();
  const lastCheck = new Date(lastCheckTime).getTime();
  
  return (now - lastCheck) > updateCheckInterval;
};

// 保存最后检查时间
export const saveLastCheckTime = () => {
  localStorage.setItem('lastUpdateCheck', new Date().toISOString());
};

// 显示更新通知
export const showUpdateNotification = (updateInfo: any) => {
  if (updateInfo.hasUpdate) {
    MessagePlugin.info(`发现新版本 ${updateInfo.newVersion}，当前版本 ${updateInfo.currentVersion}`);
  } else {
    MessagePlugin.success(updateInfo.message);
  }
};

// 应用启动时检查更新
export const checkUpdatesOnStartup = async () => {
  if (shouldCheckForUpdates()) {
    try {
      const updateInfo = await checkForUpdates();
      saveLastCheckTime();
      
      if (updateInfo.hasUpdate) {
        showUpdateNotification(updateInfo);
        return updateInfo;
      }
    } catch (error) {
      console.error('检查更新失败:', error);
    }
  }
  return null;
};