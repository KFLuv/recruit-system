<template>
  <div class="wechat-test">
    <h2>飞书消息测试</h2>
    
    <el-card class="test-card">
      <template #header>
        <div class="card-header">
          <span>🚀 测试审批通知</span>
        </div>
      </template>
      
      <el-form :model="approvalForm" label-width="100px">
        <el-form-item label="接收人">
          <el-input v-model="approvalForm.userId" placeholder="admin" />
        </el-form-item>
        <el-form-item label="计划名称">
          <el-input v-model="approvalForm.planName" placeholder="2026 年度招聘计划" />
        </el-form-item>
        <el-form-item label="申请人">
          <el-input v-model="approvalForm.applicant" placeholder="张三" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" v-perm="'write'" @click="sendApprovalNotice">发送审批通知</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card class="test-card" style="margin-top: 20px;">
      <template #header>
        <div class="card-header">
          <span>🎯 测试拟录用通知</span>
        </div>
      </template>
      
      <el-form :model="offerForm" label-width="100px">
        <el-form-item label="接收人">
          <el-input v-model="offerForm.userId" placeholder="admin" />
        </el-form-item>
        <el-form-item label="候选人">
          <el-input v-model="offerForm.candidateName" placeholder="张三" />
        </el-form-item>
        <el-form-item label="应聘岗位">
          <el-input v-model="offerForm.position" placeholder="Java 开发工程师" />
        </el-form-item>
        <el-form-item>
          <el-button type="success" v-perm="'write'" @click="sendOfferNotice">发送拟录用通知</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card class="test-card" style="margin-top: 20px;">
      <template #header>
        <div class="card-header">
          <span>📊 测试结果</span>
        </div>
      </template>
      
      <div class="test-result">
        <el-timeline>
          <el-timeline-item 
            v-for="(log, index) in testLogs" 
            :key="index" 
            :timestamp="log.time"
            placement="top"
          >
            <el-card>
              <p :style="{ color: log.success ? 'green' : 'red' }">
                {{ log.message }}
              </p>
              <p style="color: #999; font-size: 12px;">{{ log.content }}</p>
            </el-card>
          </el-timeline-item>
          <el-timeline-item v-if="testLogs.length === 0" timestamp="暂无测试记录" placement="top">
            <el-empty description="点击上面的按钮进行测试" :image-size="80" />
          </el-timeline-item>
        </el-timeline>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import request from '@/api/request'

const approvalForm = ref({
  userId: 'admin',
  planName: '2026 年度招聘计划',
  applicant: '张三'
})

const offerForm = ref({
  userId: 'admin',
  candidateName: '张三',
  position: 'Java 开发工程师'
})

const testLogs = ref([])

async function sendApprovalNotice() {
  try {
    const res = await request.post('/test/send-approval', approvalForm.value)
    console.log('审批通知返回:', res)
    addLog('审批通知', res.success ? '✅ 成功' : '❌ 失败', res.success)
    ElMessage.success(res.success ? '✅ 成功' : '❌ 失败')
  } catch (e) {
    console.error('审批通知错误:', e)
    addLog('审批通知', '❌ 发送失败', false)
    ElMessage.error('发送失败：' + (e.message || e))
  }
}

async function sendOfferNotice() {
  try {
    const res = await request.post('/test/send-offer', offerForm.value)
    console.log('拟录用通知返回:', res)
    addLog('拟录用通知', res.success ? '✅ 成功' : '❌ 失败', res.success)
    ElMessage.success(res.success ? '✅ 成功' : '❌ 失败')
  } catch (e) {
    console.error('拟录用通知错误:', e)
    addLog('拟录用通知', '❌ 发送失败', false)
    ElMessage.error('发送失败：' + (e.message || e))
  }
}

function addLog(type, message, success) {
  const now = new Date()
  const time = now.getHours().toString().padStart(2, '0') + ':' + 
               now.getMinutes().toString().padStart(2, '0') + ':' + 
               now.getSeconds().toString().padStart(2, '0')
  
  testLogs.value.unshift({
    time: time,
    type: type,
    message: message,
    success: success,
    content: JSON.stringify({
      userId: type === '审批通知' ? approvalForm.value.userId : offerForm.value.userId,
      planName: approvalForm.value.planName,
      applicant: approvalForm.value.applicant,
      candidateName: offerForm.value.candidateName,
      position: offerForm.value.position
    }, null, 2)
  })
}
</script>

<style scoped>
.wechat-test {
  padding: 20px;
  max-width: 800px;
  margin: 0 auto;
}

.test-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 16px;
  font-weight: 600;
}

.test-result {
  max-height: 400px;
  overflow-y: auto;
}
</style>
