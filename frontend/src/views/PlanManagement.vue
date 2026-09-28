<template>
  <div class="plan-management">
    <div style="display:flex;justify-content:space-between;align-items:center;margin-bottom:16px;">
      <h2>招聘计划</h2>
      <div>
        <el-input v-model="keyword" placeholder="名称/负责人" clearable style="width:180px;margin-right:8px;" />
        <el-select v-model="yearFilter" placeholder="年度" clearable style="width:100px;margin-right:8px;" @change="loadPlans">
          <el-option label="2026" value="2026" />
          <el-option label="2025" value="2025" />
          <el-option label="2027" value="2027" />
        </el-select>
        <el-select v-model="statusFilter" placeholder="状态" clearable style="width:120px;margin-right:8px;" @change="loadPlans">
          <el-option label="草稿" value="draft" />
          <el-option label="审批中" value="submitting" />
          <el-option label="已通过" value="approved" />
          <el-option label="已退回" value="rejected" />
          <el-option label="已归档" value="archived" />
          <el-option label="已作废" value="revoked" />
          <el-option label="已删除" value="deleted" />
        </el-select>
        <el-button @click="loadPlans">搜索</el-button>
        <el-button type="primary" v-perm="'write'" @click="openAddDialog">+ 新建计划</el-button>
      </div>
    </div>

    <el-alert v-if="statusFilter==='deleted'" type="warning" :closable="false" show-icon
      title="已删除计划保留在此处，可恢复为草稿；只有点击「确认移除」才会彻底删除" style="margin-bottom:12px;" />

    <el-table :data="plans" border stripe @selection-change="selChange">
      <el-table-column type="selection" width="45" />
      <el-table-column prop="planYear" label="年度" width="70" />
      <el-table-column prop="planNo" label="计划编号" width="120" />
      <el-table-column prop="planName" label="计划名称" min-width="150">
        <template #default="{row}">
          <el-link type="primary" @click="viewDetail(row)">{{ row.planName }}</el-link>
        </template>
      </el-table-column>
      <el-table-column prop="orgUnitName" label="所属单位" width="120" />
      <el-table-column prop="headcount" label="人数" width="60" />
      <el-table-column prop="totalBudget" label="预算(万)" width="90">
        <template #default="{row}">{{ row.totalBudget || '-' }}</template>
      </el-table-column>
      <el-table-column prop="status" label="状态" width="90">
        <template #default="{row}">
          <el-tag :type="statusType(row.status)" size="small">{{ statusLabel(row.status) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createdAt" label="创建时间" width="165" />
      <el-table-column label="操作" width="260" fixed="right">
        <template #default="{row}">
          <el-button text size="small" @click="viewDetail(row)">详情</el-button>
          <el-button v-if="row.status==='draft'" v-perm="'write'" text size="small" type="primary" @click="editPlan(row)">编辑</el-button>
          <el-button v-if="row.status==='draft'||row.status==='rejected'" v-perm="'write'" text size="small" type="success" @click="handleSubmit(row)">提交审批</el-button>
          <el-button v-if="row.status==='draft'" v-perm="'write'" text size="small" type="danger" @click="handleDelete(row)">删除</el-button>
          <el-button v-if="row.status==='deleted'" v-perm="'write'" text size="small" type="success" @click="handleRestore(row)">恢复</el-button>
          <el-button v-if="row.status==='deleted'" v-perm="'write'" text size="small" type="danger" @click="handlePermanentDelete(row)">确认移除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <div style="margin-top:10px;">
      <el-button v-perm="'write'" :disabled="selectedIds.length===0" size="small" @click="batchSubmit">批量提交</el-button>
      <el-button v-perm="'write'" :disabled="selectedIds.length===0" size="small" type="warning" @click="batchRevoke">批量作废</el-button>
    </div>

    <!-- 新建/编辑弹窗 -->
    <el-dialog v-model="dialogVisible" :title="isEdit?'编辑计划':'新建计划'" width="1200px" :close-on-click-modal="false">
      <el-form :model="form" label-width="110px" size="small">
        <el-row :gutter="20">
          <el-col :span="8">
            <el-form-item label="招聘年度">
              <el-input-number v-model="form.planYear" :min="2024" :max="2030" style="width:100%;" />
            </el-form-item>
          </el-col>
          <el-col :span="16">
            <el-form-item label="计划名称">
              <el-input v-model="form.planName" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="8">
            <el-form-item label="所属单位">
              <el-input v-model="form.orgUnitName" list="orgUnitList" />
              <datalist id="orgUnitList">
                <option v-for="u in orgUnits" :key="u" :value="u" />
              </datalist>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="负责人">
              <el-input v-model="form.planOwner" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="总预算(万)">
              <el-input-number v-model="form.totalBudget" :min="0" :precision="2" :step="0.1" style="width:100%;" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="8">
            <el-form-item label="编制总数">
              <el-input-number v-model="form.totalPositions" :min="0" style="width:100%;" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="招聘开始">
              <el-date-picker v-model="form.startDate" type="date" value-format="YYYY-MM-DD" style="width:100%;" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="招聘结束">
              <el-date-picker v-model="form.endDate" type="date" value-format="YYYY-MM-DD" style="width:100%;" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="8">
            <el-form-item label="简历收集开始">
              <el-date-picker v-model="form.resumeCollectStart" type="date" value-format="YYYY-MM-DD" style="width:100%;" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="简历收集结束">
              <el-date-picker v-model="form.resumeCollectEnd" type="date" value-format="YYYY-MM-DD" style="width:100%;" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="集中面试月份">
              <el-input v-model="form.interviewMonths" placeholder="如：3月、4月" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-divider>岗位编制明细</el-divider>
        <div v-for="(pos, i) in form.positions" :key="i" style="border:1px solid #dcdfe6;border-radius:6px;padding:16px;margin-bottom:12px;">
          <div style="display:flex;justify-content:space-between;align-items:center;margin-bottom:12px;">
            <strong>岗位 #{{ i+1 }}</strong>
            <el-button text size="small" type="danger" @click="form.positions.splice(i,1)">× 删除本岗位</el-button>
          </div>
          <el-row :gutter="16">
            <el-col :span="6">
              <el-form-item label="用人部门" label-width="80px">
                <el-select v-model="pos.deptName" style="width:100%;" filterable>
                  <el-option v-for="u in orgUnits" :key="u" :label="u" :value="u" />
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="6">
              <el-form-item label="岗位名称" label-width="80px">
                <el-input v-model="pos.positionName" />
              </el-form-item>
            </el-col>
            <el-col :span="4">
              <el-form-item label="人数" label-width="50px">
                <el-input-number v-model="pos.recruitmentCount" :min="1" style="width:100%;" />
              </el-form-item>
            </el-col>
            <el-col :span="4">
              <el-form-item label="职级" label-width="50px">
                <el-select v-model="pos.rank" style="width:100%;">
                  <el-option label="初级" value="初级" /><el-option label="中级" value="中级" />
                  <el-option label="高级" value="高级" /><el-option label="资深" value="资深" /><el-option label="专家" value="专家" />
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="4">
              <el-form-item label="学历" label-width="50px">
                <el-select v-model="pos.educationLevel" style="width:100%;">
                  <el-option label="不限" value="" /><el-option label="大专" value="大专" />
                  <el-option label="本科" value="本科" /><el-option label="硕士" value="硕士" /><el-option label="博士" value="博士" />
                </el-select>
              </el-form-item>
            </el-col>
          </el-row>
          <el-row :gutter="16">
            <el-col :span="6">
              <el-form-item label="工作地点" label-width="80px">
                <el-input v-model="pos.workLocation" />
              </el-form-item>
            </el-col>
            <el-col :span="6">
              <el-form-item label="薪资范围" label-width="80px">
                <el-input v-model="pos.salaryRange" />
              </el-form-item>
            </el-col>
            <el-col :span="4">
              <el-form-item label="用工类型" label-width="70px">
                <el-select v-model="pos.employmentType" style="width:100%;">
                  <el-option label="正式工" value="正式工" /><el-option label="外包" value="外包" />
                  <el-option label="实习" value="实习" /><el-option label="劳务派遣" value="劳务派遣" />
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="4">
              <el-form-item label="经验年限" label-width="70px">
                <el-select v-model="pos.experienceReq" style="width:100%;">
                  <el-option label="不限" value="不限" /><el-option label="无" value="无" />
                  <el-option label="1-3年" value="1-3年" /><el-option label="3-5年" value="3-5年" />
                  <el-option label="5-10年" value="5-10年" /><el-option label="10-20年" value="10-20年" />
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="4">
              <el-form-item label="证书/资质" label-width="70px">
                <el-input v-model="pos.qualificationReq" />
              </el-form-item>
            </el-col>
          </el-row>
          <el-row :gutter="16">
            <el-col :span="6">
              <el-form-item label="专业要求" label-width="80px">
                <el-input v-model="pos.majorRequired" />
              </el-form-item>
            </el-col>
            <el-col :span="4">
              <el-form-item label="年龄下限" label-width="70px">
                <el-input-number v-model="pos.ageMin" :min="18" :max="55" style="width:100%;" />
              </el-form-item>
            </el-col>
            <el-col :span="4">
              <el-form-item label="年龄上限" label-width="70px">
                <el-input-number v-model="pos.ageMax" :min="18" :max="55" style="width:100%;" />
              </el-form-item>
            </el-col>
            <el-col :span="5">
              <el-form-item label="政治面貌" label-width="70px">
                <el-input v-model="pos.politicalReq" />
              </el-form-item>
            </el-col>
            <el-col :span="5">
              <el-form-item label="特殊限制" label-width="70px">
                <el-input v-model="pos.specialReq" />
              </el-form-item>
            </el-col>
          </el-row>
          <el-form-item label="岗位职责" label-width="80px">
            <el-input v-model="pos.jobResponsibility" type="textarea" :rows="3" placeholder="请输入本岗位的主要工作职责" />
          </el-form-item>
          <el-form-item label="岗位需求" label-width="80px">
            <el-input v-model="pos.jobRequirement" type="textarea" :rows="3" placeholder="请输入本岗位所需的技术能力、技能要求" />
          </el-form-item>
        </div>
        <el-button size="small" @click="form.positions.push({deptName:'',positionName:'',recruitmentCount:1,rank:'',workLocation:'',salaryRange:'',employmentType:'',educationLevel:'',majorRequired:'',jobResponsibility:'',jobRequirement:'',experienceReq:'',qualificationReq:'',ageMin:null,ageMax:null,politicalReq:'',specialReq:''})">+ 添加岗位</el-button>

        <el-divider>招聘渠道</el-divider>
        <el-checkbox-group v-model="form.channels">
          <el-checkbox label="内部推荐" />
          <el-checkbox label="校招" />
          <el-checkbox label="主流招聘网站" />
          <el-checkbox label="猎头" />
          <el-checkbox label="线下招聘会" />
          <el-checkbox label="政企人才引进" />
        </el-checkbox-group>

        <el-divider>录用考核规则</el-divider>
        <el-row :gutter="20">
          <el-col :span="8">
            <el-form-item label="笔试占比(%)">
              <el-input-number v-model="form.scoreWeightWritten" :min="0" :max="100" style="width:100%;" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="面试占比(%)">
              <el-input-number v-model="form.scoreWeightInterview" :min="0" :max="100" style="width:100%;" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="招聘流程">
              <el-input v-model="form.recruitmentProcess" placeholder="如：简历筛选→笔试→面试→背调" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="背调核查维度">
          <el-input v-model="form.backgroundCheckDimensions" type="textarea" :rows="2" placeholder="如：学历真实性、工作履历、犯罪记录" />
        </el-form-item>
        <el-form-item label="体检标准">
          <el-input v-model="form.medicalStandards" placeholder="如：公务员体检标准" />
        </el-form-item>
        <el-form-item label="其他说明">
          <el-input v-model="form.otherRequirements" type="textarea" :rows="2" />
        </el-form-item>
        <el-form-item label="内部备注">
          <el-input v-model="form.internalNotes" type="textarea" :rows="2" placeholder="不对外公开" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible=false">取消</el-button>
        <el-button @click="handleSave(false)">保存</el-button>
        <el-button type="primary" @click="handleSave(true)">保存并提交</el-button>
      </template>
    </el-dialog>

    <!-- 详情弹窗 -->
    <el-dialog v-model="detailVisible" title="计划详情" width="750px">
      <div v-if="detail">
        <el-descriptions :column="2" border size="small">
          <el-descriptions-item label="计划名称">{{ detail.planName }}</el-descriptions-item>
          <el-descriptions-item label="年度">{{ detail.planYear }}</el-descriptions-item>
          <el-descriptions-item label="所属单位">{{ detail.orgUnitName || '—' }}</el-descriptions-item>
          <el-descriptions-item label="负责人">{{ detail.leader || '—' }}</el-descriptions-item>
          <el-descriptions-item label="人数">{{ detail.headcount }}</el-descriptions-item>
          <el-descriptions-item label="预算(万)">{{ detail.totalBudget }}</el-descriptions-item>
          <el-descriptions-item label="状态"><el-tag :type="statusType(detail.status)" size="small">{{ statusLabel(detail.status) }}</el-tag></el-descriptions-item>
          <el-descriptions-item label="创建时间">{{ detail.createdAt || '—' }}</el-descriptions-item>
          <el-descriptions-item label="招聘周期">{{ detail.startDate || '—' }} ~ {{ detail.endDate || '—' }}</el-descriptions-item>
        </el-descriptions>
        <div v-if="detail.positions && detail.positions.length" style="margin-top:16px;">
          <h4>岗位编制明细</h4>
          <el-table :data="detail.positions" border size="small">
            <el-table-column prop="deptName" label="部门" />
            <el-table-column prop="positionName" label="岗位" />
            <el-table-column prop="recruitmentCount" label="人数" width="60" />
            <el-table-column prop="rank" label="职级" width="60" />
            <el-table-column prop="workLocation" label="地点" />
          </el-table>
        </div>
        <div v-if="detail.rejectionReason" style="margin-top:12px;">
          <el-alert :title="'驳回原因：'+detail.rejectionReason" type="warning" :closable="false" />
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getPlanList, createPlan, updatePlan, deletePlan, restorePlan, permanentDeletePlan, submitForApproval, batchRevoke as batchRevokeApi } from '@/api/plan'

const plans = ref([])
const keyword = ref('')
const yearFilter = ref('2026')
const statusFilter = ref('')
const selectedIds = ref([])
const dialogVisible = ref(false)
const detailVisible = ref(false)
const isEdit = ref(false)
const editId = ref(null)
const detail = ref(null)

const orgUnits = ['总经办','人事部','人力资源部','财务部','企划部','技术研发部','市场部','销售部','行政部','后勤部','生产部','品质部']

const form = ref({
  planYear: 2026, planName: '', orgUnitName: '', planOwner: '', totalBudget: 0, totalPositions: 0,
  startDate: '', endDate: '', positions: [], channels: [],
  scoreWeightWritten: 50, scoreWeightInterview: 50, recruitmentProcess: '', otherRequirements: '',
  resumeCollectStart: '', resumeCollectEnd: '', interviewMonths: '',
  backgroundCheckDimensions: '', medicalStandards: '', internalNotes: ''
})

function statusType(s) {
  const map = { draft:'info', submitting:'warning', approved:'success', rejected:'danger', archived:'', revoked:'info', deleted:'danger' }
  return map[s] || 'info'
}
function statusLabel(s) {
  const map = { draft:'草稿', submitting:'审批中', approved:'已通过', rejected:'已退回', archived:'已归档', revoked:'已作废', deleted:'已删除' }
  return map[s] || s
}

function selChange(rows) { selectedIds.value = rows.map(r => r.id) }

async function loadPlans() {
  try {
    const params = {}
    if (yearFilter.value) params.year = yearFilter.value
    if (statusFilter.value) params.status = statusFilter.value
    if (keyword.value) params.keyword = keyword.value
    const res = await getPlanList(params)
    if (res.success) plans.value = res.data || []
  } catch {}
}

function openAddDialog() {
  isEdit.value = false; editId.value = null
  form.value = { planYear:2026, planName:'', orgUnitName:'', planOwner:'', totalBudget:0, totalPositions:0,
    startDate:'', endDate:'', positions:[], channels:[],
    scoreWeightWritten:50, scoreWeightInterview:50, recruitmentProcess:'', otherRequirements:'',
    resumeCollectStart:'', resumeCollectEnd:'', interviewMonths:'',
    backgroundCheckDimensions:'', medicalStandards:'', internalNotes:'' }
  dialogVisible.value = true
}

function editPlan(row) {
  isEdit.value = true; editId.value = row.id
  // 反显时 channels 转回数组
  const ch = typeof row.channels === 'string' ? row.channels.split(',').filter(Boolean) : (row.channels || [])
  form.value = { ...row,
    positions: row.positions ? row.positions.map(p => ({...p})) : [],
    channels: ch
  }
  dialogVisible.value = true
}

function viewDetail(row) {
  detail.value = row
  detailVisible.value = true
}

async function handleSave(submitAfter) {
  try {
    const payload = { ...form.value }
    // 将数组转为逗号分隔字符串
    if (Array.isArray(payload.channels)) payload.channels = payload.channels.join(',')
    let savedId = editId.value
    if (isEdit.value) {
      await updatePlan(editId.value, payload)
      ElMessage.success('更新成功')
    } else {
      const res = await createPlan(payload)
      ElMessage.success('创建成功')
      savedId = res?.data?.id || savedId
    }
    // 保存并提交：保存成功后立即提交审批
    if (submitAfter && savedId) {
      try {
        await submitForApproval(savedId)
        ElMessage.success('已提交审批')
      } catch (e) {
        ElMessage.error('提交审批失败，请稍后在列表操作')
      }
    }
    dialogVisible.value = false
    loadPlans()
  } catch {}
}

async function handleSubmit(row) {
  try {
    await ElMessageBox.confirm(`确认提交「${row.planName}」审批？`)
    await submitForApproval(row.id)
    ElMessage.success('已提交审批')
    loadPlans()
  } catch {}
}

async function handleDelete(row) {
  try {
    await ElMessageBox.confirm(`确认将「${row.planName}」移入已删除？可在「已删除」中恢复或彻底移除。`, '删除计划', { type: 'warning' })
    await deletePlan(row.id)
    ElMessage.success('已移入已删除')
    loadPlans()
  } catch {}
}

async function handleRestore(row) {
  try {
    await ElMessageBox.confirm(`确认恢复「${row.planName}」为草稿？`)
    await restorePlan(row.id)
    ElMessage.success('已恢复为草稿')
    loadPlans()
  } catch {}
}

async function handlePermanentDelete(row) {
  try {
    await ElMessageBox.confirm(
      `确认彻底删除「${row.planName}」？此操作不可恢复，数据将被永久清除！`,
      '彻底删除',
      { type: 'error', confirmButtonText: '确认移除', confirmButtonClass: 'el-button--danger' }
    )
    await permanentDeletePlan(row.id)
    ElMessage.success('已彻底删除')
    loadPlans()
  } catch {}
}

async function batchSubmit() {
  if (!selectedIds.value.length) return
  try {
    await ElMessageBox.confirm(`确认批量提交 ${selectedIds.value.length} 个计划？`)
    await Promise.all(selectedIds.value.map(id => submitForApproval(id)))
    ElMessage.success('批量提交成功')
    loadPlans()
  } catch {}
}

async function batchRevoke() {
  if (!selectedIds.value.length) return
  try {
    await ElMessageBox.confirm(`确认批量作废 ${selectedIds.value.length} 个计划？`)
    await batchRevokeApi(selectedIds.value)
    ElMessage.success('批量作废成功')
    loadPlans()
  } catch {}
}

onMounted(loadPlans)
</script>
