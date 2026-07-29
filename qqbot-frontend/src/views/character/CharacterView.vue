<script setup lang="ts">
/**
 * AI 角色管理页面
 *
 * 管理 AI 角色（人格模拟），支持：
 * - 查看已有角色
 * - 创建新的 AI 角色
 * - 编辑角色 Prompt
 * - 删除角色
 */
import { ref, onMounted } from 'vue'
import { getCharacters, createCharacter, deleteCharacter } from '@/api/bot'
import type { AiCharacter } from '@/types'
import { ElMessage, ElMessageBox } from 'element-plus'

const loading = ref(false)
const characters = ref<AiCharacter[]>([])
const dialogVisible = ref(false)
const dialogTitle = ref('创建 AI 角色')

/** 表单数据 */
const formData = ref({
  name: '',
  description: '',
  systemPrompt: '',
})

/** 表单 ref */
const formRef = ref()

/** 表单校验规则 */
const rules = {
  name: [{ required: true, message: '请输入角色名称', trigger: 'blur' }],
  description: [{ required: true, message: '请输入角色描述', trigger: 'blur' }],
  systemPrompt: [{ required: true, message: '请输入系统 Prompt', trigger: 'blur' }],
}

/** 加载角色列表 */
async function loadCharacters() {
  loading.value = true
  try {
    const result = await getCharacters()
    if (result) {
      characters.value = result
    }
  } catch (error) {
    console.error('加载角色列表失败:', error)
  } finally {
    loading.value = false
  }
}

/** 打开创建对话框 */
function openCreateDialog() {
  dialogTitle.value = '创建 AI 角色'
  formData.value = { name: '', description: '', systemPrompt: '' }
  dialogVisible.value = true
}

/** 提交表单 */
async function handleSubmit() {
  try {
    await formRef.value?.validate()
    await createCharacter(formData.value as unknown as Record<string, unknown>)
    ElMessage.success('创建成功')
    dialogVisible.value = false
    await loadCharacters()
  } catch (error) {
    if (error !== false) {
      ElMessage.error('创建失败')
    }
  }
}

/** 删除角色 */
async function handleDelete(id: number, name: string) {
  try {
    await ElMessageBox.confirm(`确定要删除角色「${name}」吗？`, '确认删除', {
      type: 'warning',
    })
    await deleteCharacter(id)
    ElMessage.success('删除成功')
    await loadCharacters()
  } catch (error) {
    // 取消操作
  }
}

onMounted(() => {
  loadCharacters()
})
</script>

<template>
  <div class="character-view">
    <div class="page-header">
      <h2 class="page-title">AI 角色管理</h2>
      <el-button type="primary" @click="openCreateDialog">
        <el-icon style="margin-right: 4px"><Plus /></el-icon>
        创建角色
      </el-button>
    </div>

    <!-- 角色卡片列表 -->
    <el-row :gutter="20">
      <el-col
        v-for="character in characters"
        :key="character.id"
        :xs="24"
        :sm="12"
        :md="8"
        :lg="6"
      >
        <el-card class="character-card" shadow="hover" v-loading="loading">
          <template #header>
            <div class="card-header">
              <span>{{ character.name }}</span>
              <el-button
                type="danger"
                size="small"
                text
                @click="handleDelete(character.id, character.name)"
              >
                <el-icon><Delete /></el-icon>
              </el-button>
            </div>
          </template>
          <p class="character-desc">{{ character.description }}</p>
          <div class="character-prompt">
            <el-text type="info" size="small">System Prompt:</el-text>
            <p class="prompt-preview">{{ character.systemPrompt }}</p>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 空状态 -->
    <el-empty v-if="!loading && characters.length === 0" description="暂无 AI 角色，点击上方按钮创建" />

    <!-- 创建/编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="600px"
      :close-on-click-modal="false"
    >
      <el-form
        ref="formRef"
        :model="formData"
        :rules="rules"
        label-width="100px"
      >
        <el-form-item label="角色名称" prop="name">
          <el-input v-model="formData.name" placeholder="请输入角色名称" />
        </el-form-item>
        <el-form-item label="角色描述" prop="description">
          <el-input
            v-model="formData.description"
            type="textarea"
            :rows="2"
            placeholder="简要描述该角色的特点"
          />
        </el-form-item>
        <el-form-item label="System Prompt" prop="systemPrompt">
          <el-input
            v-model="formData.systemPrompt"
            type="textarea"
            :rows="6"
            placeholder="输入 System Prompt，定义 AI 的行为风格"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.character-view {
  max-width: 1200px;
}

.page-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 24px;
}

.page-title {
  font-size: 20px;
  font-weight: 600;
  margin-bottom: 0;
}

.character-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.character-desc {
  color: #606266;
  font-size: 14px;
  margin-bottom: 12px;
}

.character-prompt {
  border-top: 1px solid #ebeef5;
  padding-top: 12px;
}

.prompt-preview {
  color: #909399;
  font-size: 12px;
  margin: 8px 0 0;
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
</style>
