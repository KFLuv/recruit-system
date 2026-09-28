<template>
  <div class="approval-management">
    <h2 style="margin-bottom:16px;">审批管理</h2>
    <el-tabs v-model="activeTab">
      <el-tab-pane label="待审批" name="pending">
        <el-button size="small" @click="loadPending" style="margin-bottom:10px;">⟳ 刷新</el-button>
        <el-table :data="pendingList" border stripe>
          <el-table-column prop="planNo" label="计划编号" width="120" />
          <el-table-column prop="planName" label="计划名称" min-width="160" />
          <el-table-column prop="orgUnitName" label="所属单位" width="120" />
          <el-table-column prop="headcount" label="人数" width="60" />
          <el-table-column prop="totalBudget" label="预算(万)" width="90" />
          <el-table-column prop="createdAt" label="创建时间" width="165" />
          <el-table-column label="操作" width="160" fixed="right">
            <template #default="{row}">
              <el-button type="primary" size="small" v-perm="'write'" @click="openApprovalDialog(row)">审批</el-button>
            </template>
          </el-table-column>
        </el-table>
        <div v-if="pendingList.length===0" style="text-align:center;color:#909399;padding:30px 0;">暂无待审批事项</div>
      </el-tab-pane>
      <el-tab-pane label="审批记录" name="history">
        <el-table :data="historyList" border stripe>
          <el-table-column prop="planNo" label="计划编号" width="120" />
          <el-table-column prop="planName" label="计划名称" min-width="160" />
          <el-table-column prop="orgUnitName" label="单位" width="120" />
          <el-table-column prop="status" label="结果" width="80">
            <template #default="{row}">
              <el-tag :type="row.status==='approved'?'success':'danger'" size="small">
                {{ row.status==='approved'?'已通过':'已退回' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="approver" label="审批人" width="100" />
          <el-table-column prop="approvalComment" label="审批意见" min-width="160" />
          <el-table-column prop="approvalTime" label="审批时间" width="165" />
        </el-table>
        <div v-if="historyList.length===0" style="text-align:center;color:#909399;padding:30px 0;">暂无审批记录</div>
      </el-tab-pane>
    </el-tabs>

    <!-- 审批弹窗 -->
    <el-dialog v-model="approvalVisible" title="审批" width="500px">
      <div v-if="approvalTarget">
        <el-descriptions :column="2" border size="small">
          <el-descriptions-item label="计划名称">{{ approvalTarget.planName }}</el-descriptions-item>
          <el-descriptions-item label="单位">{{ approvalTarget.orgUnitName }}</el-descriptions-item>
          <el-descriptions-item label="人数">{{ approvalTarget.headcount }}</el-descriptions-item>
          <el-descriptions-item label="预算(万)">{{ approvalTarget.totalBudget }}</el-descriptions-item>
        </el-descriptions>
        <el-input v-model="approvalComment" type="textarea" :rows="3" placeholder="审批意见（驳回时必填）" style="margin-top:12px;" />
      </div>
      <template #footer>
        <el-button @click="approvalVisible=false">取消</el-button>
        <el-button type="danger" @click="handleReject">驳回</el-button>
        <el-button type="success" @click="handleApprove">通过</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getPlanList, approvePlan, rejectPlan } from '@/api/plan'

const activeTab = ref('pending')
const pendingList = ref([])
const historyList = ref([])
const approvalVisible = ref(false)
const approvalTarget = ref(null)
const approvalComment = ref('')

async function loadPending() {
  try {
    const res = await getPlanList({ status: 'submitting' })
    if (res.success) pendingList.value = res.data || []
  } catch {}
}

async function loadHistory() {
  try {
    const [approved, rejected] = await Promise.all([
      getPlanList({ status: 'approved' }),
      getPlanList({ status: 'rejected' })
    ])
    const list = [
      ...((approved.success ? approved.data : []) || []),
      ...((rejected.success ? rejected.data : []) || [])
    ]
    historyList.value = list
  } catch {}
}

function openApprovalDialog(row) {
  approvalTarget.value = row
  approvalComment.value = ''
  approvalVisible.value = true
}

async function handleApprove() {
  if (!approvalTarget.value) return
  try {
    await approvePlan(approvalTarget.value.id, approvalComment.value)
    ElMessage.success('审批通过')
    approvalVisible.value = false
    loadPending()
    loadHistory()
  } catch {}
}

async function handleReject() {
  if (!approvalTarget.value) return
  if (!approvalComment.value.trim()) {
    ElMessage.warning('驳回时请填写审批意见')
    return
  }
  try {
    await rejectPlan(approvalTarget.value.id, approvalComment.value)
    ElMessage.success('已驳回')
    approvalVisible.value = false
    loadPending()
    loadHistory()
  } catch {}
}

onMounted(() => { loadPending(); loadHistory() })
</script>
