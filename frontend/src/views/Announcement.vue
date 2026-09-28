<template>
  <div class="announcement-page">
    <div class="page-header">
      <h2>系统公告</h2>
      <div class="header-actions">
        <el-button @click="handleRefresh">
          <el-icon><Refresh /></el-icon>
          <span> 刷新 </span>
        </el-button>
        <el-button type="primary" v-perm="'write'" @click="openCreate">
          <el-icon><Plus /></el-icon>
          <span> 发布公告 </span>
        </el-button>
      </div>
    </div>

    <el-table :data="announcements" border stripe>
      <el-table-column prop="title" label="标题" min-width="200" />
      <el-table-column prop="author" label="发布人" width="120" />
      <el-table-column prop="published" label="状态" width="80">
        <template #default="{ row }">
          <el-tag :type="row.published ? 'success' : 'info'" size="small">
            {{ row.published ? '已发布' : '草稿' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createdAt" label="创建时间" width="165" />
      <el-table-column label="操作" width="160" fixed="right">
        <template #default="{ row }">
          <el-button text size="small" @click="openDetail(row)">查看</el-button>
          <el-button text size="small" type="primary" v-perm="'write'" @click="openEdit(row)">编辑</el-button>
          <el-button text size="small" type="danger" v-perm="'write'" @click="handleDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 发布/编辑弹窗 -->
    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑公告' : '发布公告'"
      width="700px"
    >
      <el-form :model="form" label-width="80px">
        <el-form-item label="标题">
          <el-input v-model="form.title" />
        </el-form-item>
        <el-form-item label="内容">
          <el-input v-model="form.content" type="textarea" :rows="8" />
        </el-form-item>
        <el-form-item label="发布">
          <el-switch
            v-model="form.published"
            active-text="立即发布"
            inactive-text="保存为草稿"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSave">{{ isEdit ? '保存' : '发布' }}</el-button>
      </template>
    </el-dialog>

    <!-- 详情弹窗 -->
    <el-dialog v-model="detailVisible" title="公告详情" width="700px">
      <template v-if="current">
        <h3 style="margin-bottom: 12px;">{{ current.title }}</h3>
        <div style="color: #909399; font-size: 13px; margin-bottom: 16px;">
          发布人：{{ current.author }} &nbsp;|&nbsp; {{ current.createdAt }}
        </div>
        <div style="white-space: pre-wrap; line-height: 1.8;">{{ current.content }}</div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { Refresh, Plus } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getAnnouncements, createAnnouncement, updateAnnouncement, deleteAnnouncement } from '@/api/announcement'

const announcements = ref([])
const dialogVisible = ref(false)
const detailVisible = ref(false)
const isEdit = ref(false)
const current = ref(null)
const form = ref({ title: '', content: '', published: false })

function handleRefresh() {
  location.reload()
}

function openCreate() {
  isEdit.value = false
  form.value = { title: '', content: '', published: false }
  dialogVisible.value = true
}

function openEdit(row) {
  isEdit.value = true
  form.value = { title: row.title, content: row.content, published: row.published, _id: row.id }
  dialogVisible.value = true
}

function openDetail(row) {
  current.value = row
  detailVisible.value = true
}

async function handleSave() {
  if (!form.value.title || !form.value.content) {
    ElMessage.warning('请填写完整信息')
    return
  }
  try {
    if (isEdit.value && form.value._id) {
      await updateAnnouncement(form.value._id, {
        title: form.value.title,
        content: form.value.content,
        published: form.value.published
      })
      ElMessage.success('更新成功')
    } else {
      await createAnnouncement({
        title: form.value.title,
        content: form.value.content,
        published: form.value.published
      })
      ElMessage.success('发布成功')
    }
    dialogVisible.value = false
    loadAnnouncements()
  } catch {}
}

async function handleDelete(row) {
  try {
    await ElMessageBox.confirm('确定删除该公告？', '提示')
    await deleteAnnouncement(row.id)
    ElMessage.success('删除成功')
    loadAnnouncements()
  } catch {}
}

async function loadAnnouncements() {
  try {
    const res = await getAnnouncements()
    if (res.success) {
      announcements.value = res.data || []
    }
  } catch {}
}

onMounted(() => {
  loadAnnouncements()
})
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
