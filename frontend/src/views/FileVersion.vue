<template>
  <div class="file-version-page">
    <div class="page-header">
      <h2>文件版本管理</h2>
      <div class="header-actions">
        <el-select
          v-model="bizType"
          placeholder="文件类型"
          style="width: 140px"
          @change="handleQuery"
        >
          <el-option label="简历" value="resume" />
          <el-option label="附件" value="attachment" />
        </el-select>
        <el-input
          v-model="bizId"
          placeholder="关联ID"
          style="width: 120px"
          @change="handleQuery"
        />
        <el-button @click="handleQuery">查询</el-button>
        <el-button @click="handleRefresh">
          <el-icon><Refresh /></el-icon>
          <span> 刷新 </span>
        </el-button>
      </div>
    </div>

    <el-table v-if="versions.length > 0" :data="versions" border stripe>
      <el-table-column prop="bizType" label="文件类型" width="100">
        <template #default="{ row }">{{ row.bizType === 'resume' ? '简历' : '附件' }}</template>
      </el-table-column>
      <el-table-column prop="bizId" label="关联ID" width="80" />
      <el-table-column prop="version" label="版本号" width="80" />
      <el-table-column prop="fileName" label="文件名" min-width="200" />
      <el-table-column prop="fileSize" label="大小" width="100">
        <template #default="{ row }">{{ row.fileSize ? (row.fileSize / 1024).toFixed(1) + ' KB' : '-' }}</template>
      </el-table-column>
      <el-table-column prop="uploader" label="上传人" width="100" />
      <el-table-column prop="createdAt" label="上传时间" width="165" />
    </el-table>
    <div v-else style="text-align: center; color: #909399; padding: 30px 0;">
      请选择文件类型并输入关联ID后查询
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRoute } from 'vue-router'
import { Refresh } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { getFileVersions } from '@/api/fileVersion'

const bizType = ref('resume')
const bizId = ref('')
const versions = ref([])
const route = useRoute()

if (route.query.candidateId) {
  bizId.value = route.query.candidateId
}

function handleRefresh() {
  location.reload()
}

async function handleQuery() {
  if (!bizId.value) {
    ElMessage.warning('请输入关联 ID')
    return
  }
  if (isNaN(Number(bizId.value))) {
    ElMessage.error('关联 ID 必须是数字')
    return
  }
  try {
    const res = await getFileVersions(bizType.value, Number(bizId.value))
    if (res.success) {
      versions.value = res.data || []
      if (versions.value.length === 0) {
        ElMessage.info('暂无版本记录')
      }
    }
  } catch (err) {
    ElMessage.error('查询失败：' + (err.message || '未知错误'))
  }
}
</script>

<style scoped>
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}
.header-actions {
  display: flex;
  gap: 8px;
}
</style>
