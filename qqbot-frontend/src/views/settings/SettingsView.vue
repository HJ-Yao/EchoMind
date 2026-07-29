<script setup lang="ts">
/**
 * 系统设置页面
 *
 * 管理 QQBot 机器人核心配置，包括：
 * - AI 模型参数（Temperature、MaxTokens）
 * - OneBot 连接参数（WebSocket URL、Token）
 * - 系统基本信息
 */
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'

const loading = ref(false)

/** 设置表单数据 */
const formData = ref({
  aiModel: 'qwen3.7-max',
  temperature: 0.7,
  maxTokens: 2000,
  wsUrl: 'ws://127.0.0.1:3001',
  wsToken: '',
  httpUrl: 'http://127.0.0.1:3000',
})

/** 加载设置 */
async function loadSettings() {
  loading.value = true
  try {
    // TODO: 从后端加载当前配置
    // const result = await getSettings()
  } catch (error) {
    console.error('加载设置失败:', error)
  } finally {
    loading.value = false
  }
}

/** 保存设置 */
async function saveSettings() {
  loading.value = true
  try {
    // TODO: 保存配置到后端
    // await saveSettings(formData.value)
    ElMessage.success('设置保存成功')
  } catch (error) {
    ElMessage.error('设置保存失败')
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadSettings()
})
</script>

<template>
  <div class="settings-view">
    <h2 class="page-title">系统设置</h2>

    <el-card shadow="hover" v-loading="loading">
      <template #header>
        <span>AI 模型配置</span>
      </template>
      <el-form :model="formData" label-width="140px">
        <el-form-item label="AI 模型">
          <el-select v-model="formData.aiModel" style="width: 240px">
            <el-option label="qwen3.7-max (推荐)" value="qwen3.7-max" />
            <el-option label="qwen-plus" value="qwen-plus" />
            <el-option label="qwen-max" value="qwen-max" />
            <el-option label="qwen-turbo" value="qwen-turbo" />
          </el-select>
        </el-form-item>
        <el-form-item label="Temperature">
          <el-slider
            v-model="formData.temperature"
            :min="0"
            :max="2"
            :step="0.1"
            :marks="{ 0: '0', 0.7: '0.7', 1.0: '1.0', 2: '2' }"
            style="width: 300px"
          />
          <span style="margin-left: 12px; color: #909399">{{ formData.temperature }}</span>
        </el-form-item>
        <el-form-item label="Max Tokens">
          <el-input-number
            v-model="formData.maxTokens"
            :min="100"
            :max="8000"
            :step="100"
          />
        </el-form-item>
      </el-form>
    </el-card>

    <el-card shadow="hover" style="margin-top: 20px">
      <template #header>
        <span>OneBot 连接配置</span>
      </template>
      <el-form :model="formData" label-width="140px">
        <el-form-item label="WebSocket URL">
          <el-input v-model="formData.wsUrl" style="width: 400px" />
        </el-form-item>
        <el-form-item label="WebSocket Token">
          <el-input v-model="formData.wsToken" type="password" show-password style="width: 400px" />
        </el-form-item>
        <el-form-item label="HTTP API URL">
          <el-input v-model="formData.httpUrl" style="width: 400px" />
        </el-form-item>
      </el-form>
    </el-card>

    <div class="settings-actions">
      <el-button type="primary" :loading="loading" @click="saveSettings">
        保存设置
      </el-button>
      <el-button @click="loadSettings">重置</el-button>
    </div>
  </div>
</template>

<style scoped>
.settings-view {
  max-width: 800px;
}

.page-title {
  margin-bottom: 24px;
  font-size: 20px;
  font-weight: 600;
}

.settings-actions {
  margin-top: 24px;
  display: flex;
  gap: 12px;
}
</style>
