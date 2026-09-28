<template>
  <div class="system-log">
    <div class="page-header">
      <h2>系统日志</h2>
      <el-button @click="handleRefresh">
        <el-icon><Refresh /></el-icon>
        <span> 刷新 </span>
      </el-button>
    </div>

    <el-table
      v-loading="loading"
      :data="logs"
      border
      stripe
    >
      <el-table-column prop="createdAt" label="操作时间" width="175" :formatter="formatTime" />
      <el-table-column prop="username" label="操作用户" width="110" />
      <el-table-column prop="userRole" label="用户角色" width="90" />
      <el-table-column prop="module" label="操作模块" width="110" />
      <el-table-column prop="bizNo" label="编号" width="140" show-overflow-tooltip>
        <template #default="{ row }">{{ row.bizNo || '-' }}</template>
      </el-table-column>
      <el-table-column prop="description" label="操作描述" width="200" show-overflow-tooltip />
      <el-table-column prop="method" label="请求方法" width="100" />
      <el-table-column prop="ip" label="请求IP" width="140" />
      <el-table-column prop="duration" label="执行耗时(ms)" width="110" align="right" />
      <el-table-column label="操作结果" width="100" align="center">
        <template #default="{ row }">
          <el-tag :type="row.result === 'success' ? 'success' : 'danger'" size="small" effect="plain">
            {{ row.result === 'success' ? '成功' : '失败' }}
          </el-tag>
        </template>
      </el-table-column>
    </el-table>

    <div style="display: flex; justify-content: flex-end; margin-top: 16px;">
      <el-pagination
        :current-page="page"
        :page-size="pageSize"
        :total="total"
        layout="total, prev, pager, next"
        @current-change="handlePageChange"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { Refresh } from '@element-plus/icons-vue'
import { getSystemLogs } from '@/api/systemLog'

const loading = ref(false)
const logs = ref([])
const page = ref(1)
const pageSize = ref(15)
const total = ref(0)

function handleRefresh() {
  location.reload()
}

// 将 ISO 时间格式化为 yyyy-MM-dd HH:mm:ss
function formatTime(row, column, cellValue) {
  if (!cellValue) return '-'
  const d = new Date(cellValue)
  if (isNaN(d.getTime())) return cellValue
  const pad = n => String(n).padStart(2, '0')
  return `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())} ${pad(d.getHours())}:${pad(d.getMinutes())}:${pad(d.getSeconds())}`
}

async function loadLogs() {
  loading.value = true
  try {
    const res = await getSystemLogs({ page: page.value, size: pageSize.value })
    if (res.success) {
      logs.value = res.data || []
      total.value = res.total || 0
    }
  } catch {
    logs.value = []
    total.value = 0
  } finally {
    loading.value = false
  }
}

function handlePageChange(p) {
  page.value = p
  loadLogs()
}

onMounted(() => {
  loadLogs()
})
</script>

<style scoped>
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}
</style>
