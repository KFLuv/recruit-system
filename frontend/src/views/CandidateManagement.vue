<template>
  <div class="candidate-management">
    <div style="display:flex;justify-content:space-between;align-items:center;margin-bottom:16px;">
      <h2>候选人管理</h2>
      <div>
        <el-input v-model="keyword" placeholder="姓名/电话" clearable style="width:180px;margin-right:8px;" />
        <el-select v-model="statusFilter" placeholder="状态" clearable style="width:120px;margin-right:8px;" @change="loadCandidates">
          <el-option label="新申请" value="new" /><el-option label="初筛" value="screening" />
          <el-option label="面试" value="interview" /><el-option label="拟录用" value="offered" />
          <el-option label="已入职" value="onboarded" /><el-option label="淘汰" value="rejected" />
        </el-select>
        <el-button @click="loadCandidates">搜索</el-button>
        <el-button @click="handleRefresh"><el-icon><Refresh /></el-icon><span> 刷新 </span></el-button>
        <el-button type="primary" v-perm="'write'" @click="openAddDialog">+ 新增</el-button>
      </div>
    </div>

    <el-table :data="candidates" border stripe>
      <el-table-column prop="candidateNo" label="编号" width="150" />
      <el-table-column prop="realName" label="姓名" width="90" />
      <el-table-column prop="gender" label="性别" width="55" />
      <el-table-column prop="age" label="年龄" width="55" />
      <el-table-column prop="education" label="学历" width="70" />
      <el-table-column prop="phone" label="电话" width="120" />
      <el-table-column prop="appliedPosition" label="应聘岗位" min-width="120" />
      <el-table-column prop="planName" label="招聘计划" min-width="120" />
      <el-table-column prop="status" label="状态" width="80">
        <template #default="{row}">
          <el-tag :type="statusType(row.status)" size="small">{{ statusLabel(row.status) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createdAt" label="创建时间" width="165" />
      <el-table-column label="操作" width="250" fixed="right">
        <template #default="{row}">
          <el-button text size="small" @click="viewDetail(row)">详情</el-button>
          <el-button text size="small" type="primary" v-perm="'write'" @click="editCandidate(row)">编辑</el-button>
          <el-button text size="small" type="warning" v-perm="'write'" @click="openStatusDialog(row)">状态</el-button>
          <el-button text size="small" type="danger" v-perm="'write'" @click="handleDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 新增/编辑弹窗 -->
    <el-dialog v-model="dialogVisible" :title="isEdit?'编辑候选人':'新增候选人'" width="900px" :close-on-click-modal="false">
      <el-form :model="form" label-width="120px" size="small">
        <el-tabs>
          <el-tab-pane label="基础信息">
            <el-row :gutter="20">
              <el-col :span="8"><el-form-item label="姓名"><el-input v-model="form.realName" /></el-form-item></el-col>
              <el-col :span="8"><el-form-item label="性别"><el-select v-model="form.gender" style="width:100%;"><el-option label="男" value="男" /><el-option label="女" value="女" /></el-select></el-form-item></el-col>
              <el-col :span="8"><el-form-item label="年龄"><el-input-number v-model="form.age" :min="0" :max="100" style="width:100%;" /></el-form-item></el-col>
            </el-row>
            <el-row :gutter="20">
              <el-col :span="8"><el-form-item label="手机号"><el-input v-model="form.phone" /></el-form-item></el-col>
              <el-col :span="8"><el-form-item label="邮箱"><el-input v-model="form.email" /></el-form-item></el-col>
              <el-col :span="8"><el-form-item label="身份证号"><el-input v-model="form.idNumber" /></el-form-item></el-col>
            </el-row>
            <el-row :gutter="20">
              <el-col :span="8"><el-form-item label="学历"><el-select v-model="form.education" style="width:100%;"><el-option label="大专" value="大专" /><el-option label="本科" value="本科" /><el-option label="硕士" value="硕士" /><el-option label="博士" value="博士" /></el-select></el-form-item></el-col>
              <el-col :span="8"><el-form-item label="毕业院校"><el-input v-model="form.university" /></el-form-item></el-col>
              <el-col :span="8"><el-form-item label="专业"><el-input v-model="form.major" /></el-form-item></el-col>
            </el-row>
            <el-row :gutter="20">
              <el-col :span="8"><el-form-item label="户籍所在地"><el-input v-model="form.domicile" /></el-form-item></el-col>
              <el-col :span="8"><el-form-item label="现居住地址"><el-input v-model="form.currentAddress" /></el-form-item></el-col>
              <el-col :span="8"><el-form-item label="工作年限"><el-input-number v-model="form.workYears" :min="0" :step="0.5" style="width:100%;" /></el-form-item></el-col>
            </el-row>
            <el-row :gutter="20">
              <el-col :span="8"><el-form-item label="期望薪资"><el-input v-model="form.expectedSalary" /></el-form-item></el-col>
              <el-col :span="8"><el-form-item label="到岗时间"><el-input v-model="form.availableDate" /></el-form-item></el-col>
              <el-col :span="8"><el-form-item label="紧急联系人"><el-input v-model="form.emergencyContact" /></el-form-item></el-col>
            </el-row>
            <el-row :gutter="20">
              <el-col :span="8"><el-form-item label="紧急联系人电话"><el-input v-model="form.emergencyPhone" /></el-form-item></el-col>
            </el-row>
          </el-tab-pane>

          <el-tab-pane label="应聘信息">
            <el-row :gutter="20">
              <el-col :span="12"><el-form-item label="应聘岗位"><el-input v-model="form.appliedPosition" /></el-form-item></el-col>
              <el-col :span="12"><el-form-item label="所属单位">
                <el-select v-model="form.orgUnitName" style="width:100%;" filterable clearable>
                  <el-option v-for="u in orgUnits" :key="u" :label="u" :value="u" />
                </el-select>
              </el-form-item></el-col>
            </el-row>
            <el-row :gutter="20">
              <el-col :span="12"><el-form-item label="招聘计划">
                <el-select v-model="form.planId" style="width:100%;" clearable>
                  <el-option v-for="p in planOptions" :key="p.id" :label="p.planName" :value="p.id" />
                </el-select>
              </el-form-item></el-col>
              <el-col :span="12"><el-form-item label="简历来源"><el-select v-model="form.resumeSource" style="width:100%;"><el-option label="校招" value="校招" /><el-option label="内推" value="内推" /><el-option label="招聘网站" value="招聘网站" /><el-option label="猎头" value="猎头" /><el-option label="线下宣讲" value="线下宣讲" /><el-option label="其他" value="其他" /></el-select></el-form-item></el-col>
            </el-row>
            <el-row :gutter="20">
              <el-col :span="12"><el-form-item label="状态"><el-select v-model="form.status" style="width:100%;"><el-option label="新申请" value="new" /><el-option label="初筛" value="screening" /><el-option label="面试" value="interview" /><el-option label="拟录用" value="offered" /><el-option label="已入职" value="onboarded" /><el-option label="淘汰" value="rejected" /></el-select></el-form-item></el-col>
            </el-row>
            <el-form-item label="简历附件">
              <input type="file" accept=".pdf,.doc,.docx" @change="uploadResume" />
              <span v-if="form.resumeFilePath" style="margin-left:10px;color:#409eff;">已上传</span>
            </el-form-item>
          </el-tab-pane>

          <el-tab-pane label="工作经历">
            <div v-for="(w, i) in form.workExperiences" :key="i" style="border:1px solid #ebeef5;border-radius:6px;padding:12px;margin-bottom:8px;">
              <el-row :gutter="12">
                <el-col :span="8"><el-form-item label="公司"><el-input v-model="w.companyName" size="small" /></el-form-item></el-col>
                <el-col :span="8"><el-form-item label="岗位"><el-input v-model="w.position" size="small" /></el-form-item></el-col>
                <el-col :span="8"><el-form-item label="时间"><el-input v-model="w.startDate" placeholder="开始" size="small" style="width:48%;" /> ~ <el-input v-model="w.endDate" placeholder="结束" size="small" style="width:48%;" /></el-form-item></el-col>
              </el-row>
              <el-form-item label="工作描述"><el-input v-model="w.description" type="textarea" :rows="2" size="small" /></el-form-item>
              <el-button text size="small" type="danger" @click="form.workExperiences.splice(i,1)">删除</el-button>
            </div>
            <el-button size="small" @click="form.workExperiences.push({})">+ 添加工作经历</el-button>
          </el-tab-pane>

          <el-tab-pane label="项目经历">
            <div v-for="(p, i) in form.projectExperiences" :key="i" style="border:1px solid #ebeef5;border-radius:6px;padding:12px;margin-bottom:8px;">
              <el-row :gutter="12">
                <el-col :span="8"><el-form-item label="项目名称"><el-input v-model="p.projectName" size="small" /></el-form-item></el-col>
                <el-col :span="8"><el-form-item label="本人角色"><el-input v-model="p.role" size="small" /></el-form-item></el-col>
                <el-col :span="8"><el-form-item label="时间"><el-input v-model="p.projectStart" placeholder="开始" size="small" style="width:48%;" /> ~ <el-input v-model="p.projectEnd" placeholder="结束" size="small" style="width:48%;" /></el-form-item></el-col>
              </el-row>
              <el-form-item label="项目详情"><el-input v-model="p.description" type="textarea" :rows="2" size="small" /></el-form-item>
              <el-form-item label="业绩成果"><el-input v-model="p.achievement" type="textarea" :rows="2" size="small" /></el-form-item>
              <el-button text size="small" type="danger" @click="form.projectExperiences.splice(i,1)">删除</el-button>
            </div>
            <el-button size="small" @click="form.projectExperiences.push({})">+ 添加项目经历</el-button>
          </el-tab-pane>

          <el-tab-pane label="成绩评定">
            <el-row :gutter="20">
              <el-col :span="8"><el-form-item label="笔试分数"><el-input-number v-model="form.scoreWritten" :min="0" :max="100" :step="0.5" style="width:100%;" /></el-form-item></el-col>
              <el-col :span="16"><el-form-item label="笔试评语"><el-input v-model="form.commentWritten" /></el-form-item></el-col>
            </el-row>
            <el-row :gutter="20">
              <el-col :span="8"><el-form-item label="初试分数"><el-input-number v-model="form.scoreFirstInterview" :min="0" :max="100" :step="0.5" style="width:100%;" /></el-form-item></el-col>
              <el-col :span="8"><el-form-item label="初试面试官"><el-input v-model="form.interviewerFirst" /></el-form-item></el-col>
              <el-col :span="8"><el-form-item label="初试评语"><el-input v-model="form.commentFirstInterview" /></el-form-item></el-col>
            </el-row>
            <el-row :gutter="20">
              <el-col :span="8"><el-form-item label="复试分数"><el-input-number v-model="form.scoreSecondInterview" :min="0" :max="100" :step="0.5" style="width:100%;" /></el-form-item></el-col>
              <el-col :span="8"><el-form-item label="复试面试官"><el-input v-model="form.interviewerSecond" /></el-form-item></el-col>
              <el-col :span="8"><el-form-item label="复试评语"><el-input v-model="form.commentSecondInterview" /></el-form-item></el-col>
            </el-row>
            <el-row :gutter="20">
              <el-col :span="8"><el-form-item label="综合评级"><el-select v-model="form.rating" style="width:100%;"><el-option label="A-优秀" value="A" /><el-option label="B-良好" value="B" /><el-option label="C-一般" value="C" /><el-option label="D-较差" value="D" /></el-select></el-form-item></el-col>
              <el-col :span="8"><el-form-item label="总分（自动）"><el-input :model-value="autoTotalScore" readonly /></el-form-item></el-col>
            </el-row>
          </el-tab-pane>

          <el-tab-pane label="流程跟进">
            <el-form-item label="初筛结果"><el-input v-model="form.screeningResult" type="textarea" :rows="2" /></el-form-item>
            <el-form-item label="面试排期"><el-input v-model="form.interviewSchedule" /></el-form-item>
            <el-row :gutter="20">
              <el-col :span="12"><el-form-item label="体检结果"><el-input v-model="form.medicalResult" /></el-form-item></el-col>
              <el-col :span="12"><el-form-item label="背调结果"><el-input v-model="form.backgroundCheckResult" /></el-form-item></el-col>
            </el-row>
            <el-form-item label="录用结论"><el-input v-model="form.hireConclusion" /></el-form-item>
          </el-tab-pane>

          <el-tab-pane label="其他">
            <el-form-item label="证书资质"><el-input v-model="form.certificates" type="textarea" :rows="3" /></el-form-item>
            <el-form-item label="获奖经历"><el-input v-model="form.awards" type="textarea" :rows="3" /></el-form-item>
            <el-form-item label="自我评价"><el-input v-model="form.selfEvaluation" type="textarea" :rows="3" /></el-form-item>
            <el-form-item label="备注"><el-input v-model="form.remark" type="textarea" :rows="3" /></el-form-item>
          </el-tab-pane>
        </el-tabs>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible=false">取消</el-button>
        <el-button type="primary" @click="handleSave">保存</el-button>
      </template>
    </el-dialog>

    <!-- 状态修改弹窗 -->
    <el-dialog v-model="statusDialogVisible" title="修改候选人状态" width="400px">
      <el-select v-model="statusForm.status" style="width:100%;">
        <el-option label="新申请" value="new" /><el-option label="初筛" value="screening" />
        <el-option label="面试" value="interview" /><el-option label="拟录用" value="offered" />
        <el-option label="已入职" value="onboarded" /><el-option label="淘汰" value="rejected" />
      </el-select>
      <el-input v-if="statusForm.status==='rejected'" v-model="statusForm.rejectionReason" type="textarea" :rows="3" placeholder="请输入淘汰原因（必填）" style="margin-top:10px;" />
      <template #footer>
        <el-button @click="statusDialogVisible=false">取消</el-button>
        <el-button type="primary" @click="confirmStatus">确认</el-button>
      </template>
    </el-dialog>

    <!-- 详情弹窗 -->
    <el-dialog v-model="detailVisible" title="候选人详情" width="850px">
      <div v-if="detail">
        <el-descriptions :column="3" border size="small">
          <el-descriptions-item label="编号">{{ detail.candidateNo }}</el-descriptions-item>
          <el-descriptions-item label="姓名">{{ detail.realName }}</el-descriptions-item>
          <el-descriptions-item label="性别">{{ detail.gender }}</el-descriptions-item>
          <el-descriptions-item label="年龄">{{ detail.age }}</el-descriptions-item>
          <el-descriptions-item label="学历">{{ detail.education }}</el-descriptions-item>
          <el-descriptions-item label="毕业院校">{{ detail.university || '—' }}</el-descriptions-item>
          <el-descriptions-item label="专业">{{ detail.major || '—' }}</el-descriptions-item>
          <el-descriptions-item label="电话">{{ detail.phone }}</el-descriptions-item>
          <el-descriptions-item label="邮箱">{{ detail.email || '—' }}</el-descriptions-item>
          <el-descriptions-item label="应聘岗位">{{ detail.appliedPosition }}</el-descriptions-item>
          <el-descriptions-item label="所属计划">{{ detail.planName || '—' }}</el-descriptions-item>
          <el-descriptions-item label="状态">
            <el-tag :type="statusType(detail.status)" size="small">{{ statusLabel(detail.status) }}</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="创建时间">{{ detail.createdAt || '—' }}</el-descriptions-item>
          <el-descriptions-item label="简历来源">{{ detail.resumeSource || '—' }}</el-descriptions-item>
          <el-descriptions-item label="简历附件">{{ detail.resumeFilePath ? '可下载' : '—' }}</el-descriptions-item>
        </el-descriptions>
        <div v-if="detail.status==='rejected'&&detail.rejectionReason" style="margin-top:12px;">
          <el-alert :title="'淘汰原因：'+detail.rejectionReason" type="warning" :closable="false" />
        </div>
        <div v-if="detail.scoreWritten!=null||detail.scoreFirstInterview!=null" style="margin-top:16px;">
          <h4>成绩评定</h4>
          <el-descriptions :column="3" border size="small">
            <el-descriptions-item label="笔试分数">{{ detail.scoreWritten??'—' }}</el-descriptions-item>
            <el-descriptions-item label="初试分数">{{ detail.scoreFirstInterview??'—' }}</el-descriptions-item>
            <el-descriptions-item label="复试分数">{{ detail.scoreSecondInterview??'—' }}</el-descriptions-item>
            <el-descriptions-item label="评级">{{ detail.rating||'—' }}</el-descriptions-item>
          </el-descriptions>
        </div>
        <div v-if="detail.workExperiences && detail.workExperiences.length" style="margin-top:16px;">
          <h4>工作经历</h4>
          <el-table :data="detail.workExperiences" border size="small">
            <el-table-column prop="companyName" label="公司" />
            <el-table-column prop="position" label="岗位" />
            <el-table-column label="时间" width="200">
              <template #default="{row}">{{ row.startDate || '' }} ~ {{ row.endDate || '' }}</template>
            </el-table-column>
            <el-table-column prop="description" label="描述" min-width="150" />
          </el-table>
        </div>
        <div v-if="detail.projectExperiences && detail.projectExperiences.length" style="margin-top:16px;">
          <h4>项目经历</h4>
          <el-table :data="detail.projectExperiences" border size="small">
            <el-table-column prop="projectName" label="项目" />
            <el-table-column prop="role" label="角色" width="90" />
            <el-table-column label="时间" width="200">
              <template #default="{row}">{{ row.projectStart || '' }} ~ {{ row.projectEnd || '' }}</template>
            </el-table-column>
            <el-table-column prop="description" label="详情" min-width="150" />
            <el-table-column prop="achievement" label="业绩" min-width="150" />
          </el-table>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { Refresh } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getCandidateList, createCandidate, updateCandidate, deleteCandidate, updateCandidateStatus } from '@/api/candidate'
import { getPlanList } from '@/api/plan'

const candidates = ref([])
const planOptions = ref([])
const keyword = ref('')
const statusFilter = ref('')
const dialogVisible = ref(false)
const statusDialogVisible = ref(false)
const detailVisible = ref(false)
const isEdit = ref(false)
const editId = ref(null)
const statusEditId = ref(null)
const detail = ref(null)

const orgUnits = ['总经办','人事部','人力资源部','财务部','企划部','技术研发部','市场部','销售部','行政部','后勤部','生产部','品质部']

const form = ref({
  realName:'',gender:'男',age:null,phone:'',email:'',idNumber:'',education:'本科',
  university:'',major:'',domicile:'',currentAddress:'',workYears:null,
  expectedSalary:'',availableDate:'',emergencyContact:'',emergencyPhone:'',
  appliedPosition:'',orgUnitName:'',planId:null,resumeSource:'校招',status:'new',resumeFilePath:'',
  workExperiences:[],projectExperiences:[],
  scoreWritten:null,commentWritten:'',scoreFirstInterview:null,interviewerFirst:'',commentFirstInterview:'',
  scoreSecondInterview:null,interviewerSecond:'',commentSecondInterview:'',rating:'',
  screeningResult:'',interviewSchedule:'',medicalResult:'',backgroundCheckResult:'',hireConclusion:'',
  certificates:'',awards:'',selfEvaluation:'',remark:''
})
const statusForm = ref({ status:'new', rejectionReason:'' })

const autoTotalScore = computed(() => {
  const scores = [form.value.scoreWritten, form.value.scoreFirstInterview, form.value.scoreSecondInterview].filter(s => s != null)
  if (scores.length === 0) return ''
  const total = scores.reduce((a, b) => a + b, 0) / scores.length
  return total.toFixed(1)
})

function statusType(s) {
  const map = { new:'info', screening:'', interview:'warning', offered:'success', onboarded:'success', rejected:'danger' }
  return map[s] || 'info'
}
function statusLabel(s) {
  const map = { new:'新申请', screening:'初筛', interview:'面试', offered:'拟录用', onboarded:'已入职', rejected:'淘汰' }
  return map[s] || s
}

function handleRefresh() {
  location.reload()
}

async function loadCandidates() {
  try {
    const params = {}
    if (statusFilter.value) params.status = statusFilter.value
    if (keyword.value) params.keyword = keyword.value
    const res = await getCandidateList(params)
    if (res.success) candidates.value = res.data || []
  } catch {}
}

async function loadPlanOptions() {
  try {
    const res = await getPlanList()
    if (res.success) planOptions.value = res.data || []
  } catch {}
}

function openAddDialog() {
  isEdit.value = false; editId.value = null
  form.value = { realName:'',gender:'男',age:null,phone:'',email:'',idNumber:'',education:'本科',
    university:'',major:'',domicile:'',currentAddress:'',workYears:null,
    expectedSalary:'',availableDate:'',emergencyContact:'',emergencyPhone:'',
    appliedPosition:'',orgUnitName:'',planId:null,resumeSource:'校招',status:'new',resumeFilePath:'',
    workExperiences:[],projectExperiences:[],
    scoreWritten:null,commentWritten:'',scoreFirstInterview:null,interviewerFirst:'',commentFirstInterview:'',
    scoreSecondInterview:null,interviewerSecond:'',commentSecondInterview:'',rating:'',
    screeningResult:'',interviewSchedule:'',medicalResult:'',backgroundCheckResult:'',hireConclusion:'',
    certificates:'',awards:'',selfEvaluation:'',remark:'' }
  dialogVisible.value = true
}

function editCandidate(row) {
  isEdit.value = true; editId.value = row.id
  form.value = { ...row,
    workExperiences: row.workExperiences?.map(w=>({...w})) || [],
    projectExperiences: row.projectExperiences?.map(p=>({...p})) || []
  }
  dialogVisible.value = true
}

function viewDetail(row) {
  detail.value = row
  detailVisible.value = true
}

function openStatusDialog(row) {
  statusEditId.value = row.id
  statusForm.value = { status: row.status, rejectionReason: row.rejectionReason || '' }
  statusDialogVisible.value = true
}

async function handleSave() {
  try {
    if (isEdit.value) {
      await updateCandidate(editId.value, form.value)
      ElMessage.success('更新成功')
    } else {
      await createCandidate(form.value)
      ElMessage.success('创建成功')
    }
    dialogVisible.value = false
    loadCandidates()
  } catch {}
}

async function confirmStatus() {
  if (statusForm.value.status === 'rejected' && !statusForm.value.rejectionReason) {
    ElMessage.warning('请填写淘汰原因')
    return
  }
  try {
    await updateCandidateStatus(statusEditId.value, statusForm.value.status, statusForm.value.rejectionReason)
    ElMessage.success('状态已更新')
    statusDialogVisible.value = false
    loadCandidates()
  } catch {}
}

async function handleDelete(row) {
  try {
    await ElMessageBox.confirm('确认删除？')
    await deleteCandidate(row.id)
    ElMessage.success('删除成功')
    loadCandidates()
  } catch {}
}

async function uploadResume(e) {
  const file = e.target.files[0]
  if (!file) return
  const fd = new FormData()
  fd.append('file', file)
  try {
    const res = await fetch('/api/upload/resume', { method:'POST', body:fd, credentials:'include' })
    const json = await res.json()
    if (json.success) {
      form.value.resumeFilePath = json.data?.filePath || json.data
      ElMessage.success('上传成功')
    } else {
      ElMessage.error(json.message || '上传失败')
    }
  } catch {
    ElMessage.error('上传失败')
  }
}

onMounted(() => { loadCandidates(); loadPlanOptions() })
</script>

<style scoped>
/* 候选人管理表单 - 加大输入框和字体 */
.candidate-management :deep(.el-form-item__label) {
  font-size: 14px;
  font-weight: 500;
}
.candidate-management :deep(.el-input__inner),
.candidate-management :deep(.el-textarea__inner),
.candidate-management :deep(.el-input-number__input) {
  font-size: 14px;
}
.candidate-management :deep(.el-input),
.candidate-management :deep(.el-select) {
  width: 100% !important;
}
.candidate-management :deep(.el-form-item) {
  margin-bottom: 18px;
}
.candidate-management :deep(.el-tabs__content) {
  padding-top: 12px;
}
</style>
