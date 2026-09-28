<template>
  <div class="dashboard">
    <div class="page-header">
      <h2>工作台</h2>
      <el-button @click="handleRefresh">
        <el-icon><Refresh /></el-icon>
        <span> 刷新 </span>
      </el-button>
    </div>

    <!-- 统计卡片 -->
    <el-row :gutter="16" style="margin-bottom: 16px;">
      <el-col :span="6" v-for="item in statCards" :key="item.label">
        <el-card shadow="hover" body-style="padding: 16px">
          <div class="stat-inner">
            <div class="stat-icon" :style="{ background: item.color }">
              <el-icon :size="22" style="color: #fff;">
                <component :is="item.icon" />
              </el-icon>
            </div>
            <div>
              <div class="stat-value">{{ item.value }}</div>
              <div class="stat-label">{{ item.label }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 计划状态分布 + 汇总信息 -->
    <el-row :gutter="16" style="margin-bottom: 16px;">
      <el-col :span="16">
        <el-card>
          <template #header><span>计划状态分布</span></template>
          <el-table :data="statusDistribution" border stripe size="small">
            <el-table-column prop="label" label="状态" width="120" />
            <el-table-column prop="count" label="数量" width="80" />
            <el-table-column label="占比" min-width="200">
              <template #default="{ row }">
                <el-progress :percentage="row.percent" :color="row.color" :stroke-width="18" text-inside />
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card>
          <template #header><span>汇总信息</span></template>
          <div class="summary-item">
            <span>总预算</span>
            <strong>{{ stats.totalBudget || 0 }} 万元</strong>
          </div>
          <div class="summary-item">
            <span>总编制</span>
            <strong>{{ stats.totalHeadcount || 0 }} 人</strong>
          </div>
          <div class="summary-item">
            <span>候选人总数</span>
            <strong>{{ stats.candidateCount || 0 }} 人</strong>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 近期动态 -->
    <el-card>
      <template #header><span>近期动态</span></template>
      <div v-if="activities.length === 0" style="text-align:center;color:#909399;padding:30px 0;">暂无动态</div>
      <el-timeline v-else>
        <el-timeline-item
          v-for="act in activities"
          :key="act.id"
          :timestamp="act.createTime || ''"
          :color="act.type === '审批' ? '#67c23a' : '#409eff'"
        >
          {{ act.content }}
        </el-timeline-item>
      </el-timeline>
    </el-card>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { Refresh, Document, CircleCheck, Clock, UserFilled } from '@element-plus/icons-vue'
import { getDashboardStats, getRecentActivities } from '@/api/dashboard'

const stats = ref({
  planCount: 0,
  approvedCount: 0,
  draftCount: 0,
  submittingCount: 0,
  rejectedCount: 0,
  archivedCount: 0,
  totalBudget: 0,
  totalHeadcount: 0,
  candidateCount: 0
})
const activities = ref([])

const statCards = computed(() => [
  { label: '招聘计划', value: stats.value.planCount, icon: Document, color: '#409eff' },
  { label: '审批通过', value: stats.value.approvedCount, icon: CircleCheck, color: '#67c23a' },
  { label: '审批中', value: stats.value.submittingCount, icon: Clock, color: '#e6a23c' },
  { label: '候选人', value: stats.value.candidateCount, icon: UserFilled, color: '#909399' }
])

const statusDistribution = computed(() => {
  const total = stats.value.planCount || 1
  return [
    { label: '草稿', count: stats.value.draftCount, percent: +(stats.value.draftCount / total * 100).toFixed(1), color: '#909399' },
    { label: '审批中', count: stats.value.submittingCount, percent: +(stats.value.submittingCount / total * 100).toFixed(1), color: '#e6a23c' },
    { label: '已通过', count: stats.value.approvedCount, percent: +(stats.value.approvedCount / total * 100).toFixed(1), color: '#67c23a' },
    { label: '已退回', count: stats.value.rejectedCount, percent: +(stats.value.rejectedCount / total * 100).toFixed(1), color: '#f56c6c' },
    { label: '已归档', count: stats.value.archivedCount, percent: +(stats.value.archivedCount / total * 100).toFixed(1), color: '#409eff' }
  ].filter(item => item.count > 0)
})

function handleRefresh() {
  location.reload()
}

onMounted(async () => {
  try {
    const res = await getDashboardStats()
    if (res.success && res.data) {
      Object.assign(stats.value, res.data)
    }
    const actRes = await getRecentActivities()
    if (actRes.success) activities.value = actRes.data || []
  } catch {}
})
</script>

<style scoped>
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}
.stat-inner {
  display: flex;
  align-items: center;
  gap: 12px;
}
.stat-icon {
  width: 44px;
  height: 44px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
}
.stat-value {
  font-size: 22px;
  font-weight: 700;
}
.stat-label {
  font-size: 13px;
  color: #909399;
}
.summary-item {
  display: flex;
  justify-content: space-between;
  padding: 8px 0;
  border-bottom: 1px dashed #ebeef5;
}
.summary-item:last-child {
  border-bottom: none;
}
.summary-item span {
  color: #909399;
}
</style>
