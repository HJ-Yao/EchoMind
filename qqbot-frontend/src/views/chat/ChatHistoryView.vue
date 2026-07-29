<script setup lang="ts">
/**
 * 聊天记录页面
 *
 * 查看和管理 QQ 机器人聊天记录，支持：
 * - 按用户/时间筛选
 * - 分页查看
 * - 查看消息详情
 */
import { ref, onMounted } from 'vue'
import { getChatRecords } from '@/api/bot'
import type { ChatRecord } from '@/types'

const loading = ref(false)
const records = ref<ChatRecord[]>([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(20)

/** 加载聊天记录 */
async function loadRecords() {
  loading.value = true
  try {
    const result = await getChatRecords({
      page: currentPage.value,
      size: pageSize.value,
    })
    if (result) {
      records.value = result.records
      total.value = result.total
    }
  } catch (error) {
    console.error('加载聊天记录失败:', error)
  } finally {
    loading.value = false
  }
}

/** 页码变化 */
function handlePageChange(page: number) {
  currentPage.value = page
  loadRecords()
}

onMounted(() => {
  loadRecords()
})
</script>

<template>
  <div class="chat-history-view">
    <h2 class="page-title">聊天记录</h2>

    <el-card shadow="hover">
      <el-table
        :data="records"
        v-loading="loading"
        stripe
        style="width: 100%"
        empty-text="暂无聊天记录"
      >
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="userId" label="用户 QQ" width="140" />
        <el-table-column prop="nickname" label="用户昵称" width="140" />
        <el-table-column prop="role" label="角色" width="100">
          <template #default="{ row }">
            <el-tag :type="row.role === 'user' ? 'info' : 'success'" size="small">
              {{ row.role === 'user' ? '用户' : '机器人' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="content" label="消息内容" min-width="300" show-overflow-tooltip />
        <el-table-column prop="createTime" label="时间" width="180" />
      </el-table>

      <!-- 分页 -->
      <div class="pagination-wrapper">
        <el-pagination
          v-model:current-page="currentPage"
          :page-size="pageSize"
          :total="total"
          layout="total, prev, pager, next"
          @current-change="handlePageChange"
        />
      </div>
    </el-card>
  </div>
</template>

<style scoped>
.chat-history-view {
  max-width: 1200px;
}

.page-title {
  margin-bottom: 24px;
  font-size: 20px;
  font-weight: 600;
}

.pagination-wrapper {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>
