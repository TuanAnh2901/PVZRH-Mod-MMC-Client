<template>
  <div class="markdown-container" v-html="renderedContent"></div>
</template>

<script setup lang="ts">
import { computed } from 'vue';
import { marked } from 'marked';

interface Props {
  content: string;
}

const props = withDefaults(defineProps<Props>(), {
  content: ''
});

// 配置marked选项
marked.setOptions({
  breaks: true, // 支持换行
  gfm: true, // 启用GitHub风格的Markdown
});

const renderedContent = computed(() => {
  if (!props.content) return '';
  return marked(props.content);
});
</script>

<style scoped>
.markdown-container {
  line-height: 1.6;
}

/* Markdown样式 */
.markdown-container :deep(h1),
.markdown-container :deep(h2),
.markdown-container :deep(h3),
.markdown-container :deep(h4),
.markdown-container :deep(h5),
.markdown-container :deep(h6) {
  margin-top: 0.5em;
  margin-bottom: 0.5em;
  font-weight: 600;
}

.markdown-container :deep(h3) {
  font-size: 16px;
  color: #333;
}

.markdown-container :deep(ul) {
  padding-left: 20px;
  margin-bottom: 0;
}

.markdown-container :deep(li) {
  margin-bottom: 4px;
}

.markdown-container :deep(p) {
  margin: 0.5em 0;
}

.markdown-container :deep(code) {
  background-color: #f5f5f5;
  padding: 2px 4px;
  border-radius: 3px;
  font-family: monospace;
}

.markdown-container :deep(pre) {
  background-color: #f5f5f5;
  padding: 10px;
  border-radius: 5px;
  overflow-x: auto;
}

.markdown-container :deep(blockquote) {
  border-left: 4px solid #ddd;
  padding-left: 16px;
  margin: 0;
  color: #666;
}
</style>