<template>
  <div class="mod-store-container">
    <!-- 页面顶部 -->
    <div class="page-header">
      <div class="header-content">
        <h1>在线模组库</h1>
        <p>浏览并下载最新的游戏模组，发现更多乐趣</p>
      </div>
      <div class="header-actions">
        <t-space>
          <!-- 手动安装按钮 -->
          <t-button 
            theme="warning" 
            variant="outline" 
            @click="handleImport" 
            :loading="importing"
            :disabled="downloadingId !== ''"
          >
            <template #icon>
              <FileImportIcon />
            </template>
            手动安装
          </t-button>
          
          <t-button theme="primary" variant="outline" @click="fetchMods" :loading="loading">
            <template #icon>
              <RefreshIcon />
            </template>
            刷新列表
          </t-button>
        </t-space>
      </div>
    </div>

    <!-- 加载状态 -->
    <div v-if="loading" class="loading-state">
      <t-loading text="正在获取模组列表..." size="small"></t-loading>
    </div>

    <!-- 模组列表网格 -->
    <div v-else class="mod-grid">
      <t-card v-for="mod in modList" :key="mod.id" class="mod-card" :bordered="false" hover-shadow>
        
        <!-- 1. 卡片头部：标题与标签 -->
        <div class="mod-card-header">
          <div class="mod-info-left">
            <div class="mod-title" :title="mod.modName">{{ mod.modName }}</div>
            <div class="mod-version">v{{ mod.version }}</div>
          </div>
          <t-tag 
            :theme="mod.frameworkName === '1' ? 'primary' : 'warning'" 
            variant="light" 
            size="small"
            v-if="mod.frameworkName"
          >
            {{ mod.frameworkName === '1' ? 'BepInEx' : 'MelonLoader' }}
          </t-tag>
        </div>

        <!-- 2. 视频预览区域 (16:9 自适应容器) -->
        <div v-if="mod.videoUrl" class="video-wrapper">
          <iframe 
            :src="mod.videoUrl" 
            scrolling="no" 
            border="0" 
            frameborder="no" 
            framespacing="0" 
            allowfullscreen="true" 
            sandbox="allow-top-navigation allow-same-origin allow-forms allow-scripts"
          ></iframe>
        </div>

        <!-- 3. 内容区域 -->
        <div class="mod-card-body">
          <p class="description" :title="mod.modDescription">
            {{ mod.modDescription || '暂无描述' }}
          </p>
          
          <div class="info-footer">
            <div class="meta-row">
              <div class="meta-item author">
                <UserIcon size="14px" />
                <span>{{ mod.authorName }}</span>
              </div>
              <div class="meta-item date">
                <TimeIcon size="14px" />
                <span>{{ formatDate(mod.updatedAt) }}</span>
              </div>
            </div>
            
            <div class="support-info">
              <span class="label">游戏版本:</span>
              <span class="value">{{ mod.supportedVersions }}</span>
            </div>
          </div>
        </div>

        <!-- 4. 底部操作栏 -->
        <template #footer>
          <div class="mod-card-actions">
            
            <!-- 状态 A: 下载/安装进度条 -->
            <div v-if="downloadingId === mod.id" class="download-progress-area">
              <div class="progress-header">
                <span class="status-text">
                  <t-loading size="small" v-if="installStatus === '下载中'" style="margin-right:4px; transform: scale(0.8);" />
                  {{ installStatus }}
                </span>
                <span class="percentage">{{ downloadProgress }}%</span>
              </div>
              <t-progress 
                theme="plump" 
                :percentage="downloadProgress" 
                :status="downloadProgress === 100 ? 'success' : 'active'" 
                size="small" 
              />
              <t-button theme="danger" variant="text" size="small" block style="margin-top: 8px" @click="cancelDownload">
                取消
              </t-button>
            </div>

            <!-- 状态 B: 操作按钮组 -->
            <template v-else>
              <t-button 
                v-if="mod.showDirectUrl" 
                theme="primary" 
                block 
                :disabled="downloadingId !== '' || importing" 
                @click="handleDownload(mod)"
              >
                <template #icon><DownloadIcon /></template>
                安装
              </t-button>

              <t-button 
                v-if="mod.downloadCloudUrl" 
                theme="default" 
                variant="outline" 
                block
                :disabled="downloadingId !== '' || importing" 
                @click="handleCloudLink(mod)"
              >
                <template #icon><CloudDownloadIcon /></template>
                网盘
              </t-button>
            </template>
          </div>
        </template>
      </t-card>
    </div>

    <!-- 空状态 -->
    <div v-if="!loading && modList.length === 0" class="empty-state">
      <t-empty description="暂无在线模组" />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue';
import { MessagePlugin, NotifyPlugin } from 'tdesign-vue-next';
import { RefreshIcon, DownloadIcon, CloudDownloadIcon, UserIcon, TimeIcon, FileImportIcon } from 'tdesign-icons-vue-next';
import { openUrl } from '@tauri-apps/plugin-opener';
import { getModFramework,extractZipToGameDir } from '../utils/modUtils'; 
import JSZip from 'jszip';
import { load } from '@tauri-apps/plugin-store';
import { writeFile, mkdir, readFile } from '@tauri-apps/plugin-fs';
import { join, dirname } from '@tauri-apps/api/path';
import { fetch } from '@tauri-apps/plugin-http';
import { open } from '@tauri-apps/plugin-dialog';

// 接口定义
interface ModItem {
  id: string;
  modName: string;
  authorName: string;
  modDescription: string;
  videoUrl: string;
  supportedVersions: string;
  showDirectUrl: boolean;
  downloadDirectUrl: string;
  downloadCloudUrl: string;
  version: string;
  updatedAt: string;
  frameworkName: string;
}

interface ApiResponse {
  code: number;
  msg: string | null;
  data: ModItem[];
}

const loading = ref(false);
const importing = ref(false);
const modList = ref<ModItem[]>([]);

// 下载相关状态
const downloadingId = ref('');
const downloadProgress = ref(0);
const installStatus = ref('下载中');
const abortController = ref<AbortController | null>(null);

// -----------------------------------------------------------
// 手动导入逻辑
// -----------------------------------------------------------
const handleImport = async () => {
  try {
    const store = await load('store.json');
    const gamePath = await store.get<string>('gamePath');
    
    if (!gamePath) {
      MessagePlugin.error('请先在设置中配置游戏根目录');
      return;
    }

    const selectedPath = await open({
      multiple: false,
      directory: false,
      filters: [{ name: 'Mod压缩包', extensions: ['zip'] }]
    });

    if (!selectedPath) return;

    importing.value = true;
    const loadingMsg = MessagePlugin.loading('正在读取并安装模组...', 0);

    try {
      const fileData = await readFile(selectedPath as string);
      await extractZipToGameDir(fileData, gamePath);
      MessagePlugin.close(loadingMsg);
      NotifyPlugin.success({ title: '手动安装成功', content: '模组已解压至游戏目录', duration: 3000 });
    } catch (err: any) {
      MessagePlugin.close(loadingMsg);
      console.error(err);
      NotifyPlugin.error({ title: '安装失败', content: err.message || '解压文件出错' });
    } finally {
      importing.value = false;
    }
  } catch (err) {
    console.error(err);
    importing.value = false;
  }
};

// -----------------------------------------------------------
// 获取列表逻辑
// -----------------------------------------------------------
const fetchMods = async () => {
  loading.value = true;
  try {
    const response = await fetch('https://mod.ehre.top/api/public/mod', {
      method: 'GET',
      headers: { 'User-Agent': 'Tauri-App' }
    });

    if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`);
    const res: ApiResponse = await response.json();

    if (res.code === 0 && res.data) {
      modList.value = res.data;
    } else {
      MessagePlugin.error(res.msg || '获取模组列表失败');
    }
  } catch (error) {
    console.error(error);
    MessagePlugin.error('网络请求失败，请检查网络');
  } finally {
    loading.value = false;
  }
};

const formatDate = (dateStr: string) => dateStr ? dateStr.split(' ')[0] : '';

// -----------------------------------------------------------
// 在线下载逻辑
// -----------------------------------------------------------
const cancelDownload = () => {
  if (abortController.value) {
    abortController.value.abort();
    abortController.value = null;
  }
  downloadingId.value = '';
  downloadProgress.value = 0;
  installStatus.value = '';
  MessagePlugin.info('已取消安装');
};

const handleDownload = async (mod: ModItem) => {
  if (!mod.downloadDirectUrl) {
    MessagePlugin.warning('该模组没有直链下载地址');
    return;
  }

  const modFramework = await getModFramework();
  const fcode = modFramework === 'BepInEx' ? '1' : '2';
  
  if (mod.frameworkName !== fcode) {
    const currentEnv = modFramework;
    const targetEnv = mod.frameworkName === '1' ? 'BepInEx' : 'MelonLoader';
    MessagePlugin.warning(`环境不匹配：当前是 ${currentEnv}，该模组需要 ${targetEnv}`);
    return;
  }

  downloadingId.value = mod.id;
  downloadProgress.value = 0;
  installStatus.value = '下载中';
  abortController.value = new AbortController();

  try {
    const store = await load('store.json');
    const gamePath = await store.get<string>('gamePath');
    if (!gamePath) throw new Error('未设置游戏路径');

    const response = await fetch(mod.downloadDirectUrl, {
      method: 'GET',
      signal: abortController.value.signal,
      connectTimeout: 30000,
    });

    if (!response.ok) throw new Error(`HTTP ${response.status}`);

    const contentLength = response.headers.get('content-length');
    const totalLength = contentLength ? parseInt(contentLength, 10) : 0;

    if (!response.body) throw new Error('无法读取响应流');

    const reader = response.body.getReader();
    const chunks: Uint8Array[] = [];
    let receivedLength = 0;

    while (true) {
      const { done, value } = await reader.read();
      if (done) break;
      chunks.push(value);
      receivedLength += value.length;
      if (totalLength > 0) {
        const progress = Math.round((receivedLength / totalLength) * 95);
        downloadProgress.value = progress;
      }
    }

    installStatus.value = '解压中';
    downloadProgress.value = 95;

    const allChunks = new Uint8Array(receivedLength);
    let position = 0;
    for (const chunk of chunks) {
      allChunks.set(chunk, position);
      position += chunk.length;
    }

    await extractZipToGameDir(allChunks, gamePath);

    downloadProgress.value = 100;
    NotifyPlugin.success({ title: '安装成功', content: `${mod.modName} 已就绪`, duration: 3000 });

  } catch (err: any) {
    if (err.name === 'AbortError' || err.toString().includes('aborted') || err.message.includes('canceled')) {
      console.log('用户取消下载');
    } else {
      console.error(err);
      NotifyPlugin.error({ title: '安装失败', content: err.message || '未知错误' });
    }
  } finally {
    if (!abortController.value?.signal.aborted) {
      downloadingId.value = '';
    }
    abortController.value = null;
  }
};

const handleCloudLink = (mod: ModItem) => {
  if (mod.downloadCloudUrl) openUrl(mod.downloadCloudUrl);
};

// -----------------------------------------------------------
// 生命周期
// -----------------------------------------------------------
onMounted(() => {
  fetchMods();
});

onUnmounted(() => {
  if (abortController.value) {
    abortController.value.abort();
  }
});
</script>

<style scoped>
/* 容器布局 */
.mod-store-container {
  height: 100%;
  display: flex;
  flex-direction: column;
  background-color: var(--td-bg-color-container); /* 适配深色/浅色模式 */
}

/* 页面头部 */
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 4px 24px 4px;
}

.page-header h1 {
  font-size: 24px;
  font-weight: 600;
  color: var(--td-text-color-primary);
  margin: 0 0 4px 0;
}

.page-header p {
  font-size: 14px;
  color: var(--td-text-color-secondary);
  margin: 0;
}

/* 核心网格布局 */
.mod-grid {
  display: grid;
  /* 响应式网格，最小宽度280px */
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 20px;
  padding-bottom: 24px;
  /* 关键属性：让同一行的卡片等高 */
  align-items: stretch;
}

/* 卡片容器 */
.mod-card {
  display: flex;
  flex-direction: column;
  height: 100%;
  border-radius: 8px;
  border: 1px solid var(--td-component-stroke);
  transition: all 0.25s cubic-bezier(0.25, 0.8, 0.5, 1);
  overflow: hidden;
}

.mod-card:hover {
  transform: translateY(-4px);
  box-shadow: var(--td-shadow-2);
  border-color: var(--td-brand-color-focus);
}

/* 卡片头部 */
.mod-card-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  padding: 0 0 12px 0;
  gap: 8px;
}

.mod-info-left {
  flex: 1;
  min-width: 0;
}

.mod-title {
  font-size: 16px;
  font-weight: 700;
  color: var(--td-text-color-primary);
  margin-bottom: 2px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.mod-version {
  font-size: 12px;
  color: var(--td-text-color-placeholder);
  font-family: monospace;
}

/* 视频容器：保持 16:9 比例 */
.video-wrapper {
  position: relative;
  width: 100%;
  padding-bottom: 56.25%; /* 16:9 */
  background: #000;
  border-radius: 6px;
  overflow: hidden;
  margin-bottom: 12px;
  border: 1px solid var(--td-component-stroke);
}

.video-wrapper iframe {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
}

/* 卡片内容主体 */
.mod-card-body {
  flex: 1; /* 撑满剩余空间，推到底部 */
  display: flex;
  flex-direction: column;
}

.description {
  font-size: 13px;
  color: var(--td-text-color-secondary);
  line-height: 1.6;
  margin-bottom: 16px;
  /* 2行截断 */
  display: -webkit-box;
  -webkit-box-orient: vertical;
  -webkit-line-clamp: 2;
  overflow: hidden;
  height: 42px; /* 固定高度防止跳动 */
}

/* 元信息与支持版本区域 */
.info-footer {
  margin-top: auto; /* 关键：推到底部 */
  padding-top: 12px;
  border-top: 1px dashed var(--td-component-stroke);
}

.meta-row {
  display: flex;
  justify-content: space-between;
  margin-bottom: 8px;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 12px;
  color: var(--td-text-color-placeholder);
}

.support-info {
  background: var(--td-bg-color-secondarycontainer);
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 12px;
  display: flex;
  justify-content: space-between;
}

.support-info .label {
  color: var(--td-text-color-secondary);
}

.support-info .value {
  color: var(--td-text-color-primary);
  font-weight: 500;
}

/* 卡片底部 Actions (Grid) */
.mod-card-actions {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 12px;
}

/* 覆盖 TDesign Card 默认样式 */
:deep(.t-card__footer) {
  padding: 12px 16px;
  background: var(--td-bg-color-secondarycontainer);
}

:deep(.t-card__body) {
  padding: 16px;
  display: flex;
  flex-direction: column;
  height: 100%;
}

/* 下载进度条样式 */
.download-progress-area {
  grid-column: span 2;
  background: var(--td-brand-color-light);
  border-radius: 6px;
  padding: 10px;
  border: 1px solid var(--td-brand-color-focus);
}

.progress-header {
  display: flex;
  justify-content: space-between;
  font-size: 12px;
  margin-bottom: 6px;
  color: var(--td-brand-color);
}

.percentage {
  font-weight: 700;
  font-family: monospace;
}

/* 加载与空状态 */
.loading-state,
.empty-state {
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 80px 0;
  color: var(--td-text-color-secondary);
}
</style>