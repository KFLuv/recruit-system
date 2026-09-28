<template>
  <div class="layout">
    <!-- 顶部导航 -->
    <el-header class="topbar">
      <div class="topbar-left">
        <span class="topbar-title">人力招聘管理系统</span>
      </div>
      <div class="topbar-right">
        <span class="user-info">
          <el-icon><User /></el-icon>
          {{ userStore.userInfo?.username || '用户' }}
          <el-tag size="small" :type="roleTagType" effect="plain">{{ roleLabel }}</el-tag>
        </span>
        <el-button text type="primary" @click="handleLogout">退出登录</el-button>
      </div>
    </el-header>

    <!-- 只读账号提示条 -->
    <div v-if="isReadonly" class="readonly-banner">
      <el-icon><InfoFilled /></el-icon>
      当前为只读账号，仅可查看数据，编辑/删除/审批等操作将被系统拒绝
    </div>

    <div class="main-container">
      <!-- 侧边栏 -->
      <el-aside width="220px" class="sidebar">
        <el-menu
          :default-active="activeMenu"
          router
          style="border-right: none;"
        >
          <el-menu-item index="/dashboard">
            <el-icon><Odometer /></el-icon>
            <span>工作台</span>
          </el-menu-item>
          <el-menu-item index="/plans">
            <el-icon><Document /></el-icon>
            <span>招聘计划</span>
          </el-menu-item>
          <el-menu-item index="/candidates">
            <el-icon><UserFilled /></el-icon>
            <span>候选人管理</span>
          </el-menu-item>
          <el-menu-item index="/approvals">
            <el-icon><CircleCheck /></el-icon>
            <span>审批管理</span>
          </el-menu-item>
          <el-menu-item index="/announcements">
            <el-icon><Bell /></el-icon>
            <span>系统公告</span>
          </el-menu-item>
          <el-menu-item index="/file-versions">
            <el-icon><FolderOpened /></el-icon>
            <span>文件版本</span>
          </el-menu-item>
          <el-menu-item index="/system-logs">
            <el-icon><Memo /></el-icon>
            <span>系统日志</span>
          </el-menu-item>
          <el-menu-item index="/feishu-test">
            <el-icon><ChatDotRound /></el-icon>
            <span>飞书消息测试</span>
          </el-menu-item>
        </el-menu>
      </el-aside>

      <!-- 内容区 -->
      <el-main class="content">
        <router-view />
      </el-main>
    </div>

    <!-- AI 聊天助手 -->
    <AiChatAssistant />
  </div>
</template>

<script setup>
import { computed, ref, nextTick } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { User, Odometer, Document, UserFilled, CircleCheck, Bell, FolderOpened, Memo, ChatDotRound, InfoFilled } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'
import { logout as logoutApi } from '@/api/auth'
import AiChatAssistant from '@/components/AiChatAssistant.vue'
import { ElMessage } from 'element-plus'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const activeMenu = computed(() => route.path)

/* ---------- 账号信息 ---------- */
const isReadonly = computed(() => userStore.userInfo?.role === 'readonly')

const roleLabel = computed(() => {
  const role = userStore.userInfo?.role
  const map = { admin: '管理员', manager: '经理', readonly: '只读', judge: '评委' }
  return map[role] || role || '普通用户'
})

const roleTagType = computed(() => {
  const role = userStore.userInfo?.role
  if (role === 'admin') return 'danger'
  if (role === 'readonly') return 'info'
  return 'warning'
})

async function handleLogout() {
  try {
    await logoutApi()
  } catch {}
  userStore.logout()
  router.push('/login')
}

/* ---------- AI 聊天助手 ---------- */
const showChat = ref(false)
const chatInput = ref('')
const chatRef = ref(null)
const chatMessages = ref([
  { role: 'ai', content: '您好！我是招聘系统 AI 助手。输入关键词获取帮助：**帮助**（功能总览）、**计划**（招聘计划操作）、**候选人**（档案管理）、**审批**（审批流程）、**预算**（预算编制）、**bug**（提交 Bug 报告）、**联系**（联系平台负责人）' }
])

async function sendChat() {
  if (!chatInput.value.trim()) return
  const text = chatInput.value.trim()
  chatMessages.value.push({ role: 'user', content: text })
  chatInput.value = ''

  const replies = {
    '帮助|功能|使用': '**常用功能指引：**\n1. 新建招聘计划：填写计划信息、岗位明细后保存\n2. 添加候选人：在候选人管理中新增，可上传简历\n3. 提交审批：草稿计划可提交审批\n4. 审批处理：在审批管理中通过/驳回\n5. 支持批量操作和状态追踪',
    '计划|招聘计划': '**招聘计划操作说明：**\n• 新建：填写年度/名称/单位/预算/岗位明细等\n• 编辑/删除：仅草稿状态可操作\n• 提交审批：草稿和已退回状态可提交\n• 状态：草稿→审批中→已通过/已退回→已归档/已作废\n• 支持批量提交和批量作废',
    '候选人|档案': '**候选人管理说明：**\n• 新增：填写基础信息、应聘信息、工作/项目经历\n• 上传简历：支持 pdf/doc/docx 格式\n• 成绩评定：笔试/初试/复试分数+评语+评级\n• 状态流转：新申请→初筛→面试→拟录用→已入职/淘汰\n• 淘汰时需填写淘汰原因',
    '审批|审核': '**审批操作说明：**\n• 待审批列表展示所有审批中的计划\n• 点击"审批"弹出审批窗口\n• 通过：填写意见后点击"通过"\n• 驳回：必须填写意见后点击"驳回"\n• 已通过的计划不可再修改',
    '预算|编制': '**预算与编制说明：**\n• 提交审批时系统会校验预算和编制\n• 总预算支持小数精确到分\n• 各岗位人数之和不能超过编制总数\n• 已审批的计划占用当年预算额度',
    '你好|您好': '您好！我是 AI 助手，可以帮您：\n• **帮助** - 查看全部功能\n• **计划** - 招聘计划操作\n• **候选人** - 档案管理\n• **审批** - 审批流程\n• **预算** - 预算编制\n• **bug** - 提交 Bug 报告\n• **联系** - 联系平台负责人',
    'bug|上报|报告': '即将打开 Bug 报告弹窗...',
    '联系|微信|电话|负责人': '**平台负责人联系方式：**\n微信：YOUR_WECHAT\n手机：YOUR_PHONE'
  }

  let reply = '您好！我是 AI 助手。您可以输入：**帮助**、**计划**、**候选人**、**审批**、**预算**、**bug**、**联系** 获取对应帮助。'
  for (const [key, value] of Object.entries(replies)) {
    const keywords = key.split('|')
    if (keywords.some(k => text.includes(k))) {
      reply = value
      break
    }
  }

  if (text.includes('bug') || text.includes('上报') || text.includes('报告')) {
    setTimeout(() => { showBugReport.value = true }, 500)
  }

  setTimeout(() => {
    chatMessages.value.push({ role: 'ai', content: reply })
    nextTick(() => { if (chatRef.value) chatRef.value.scrollTop = chatRef.value.scrollHeight })
  }, 300)
}

/* ---------- Bug 报告 ---------- */
const showBugReport = ref(false)
const bugForm = ref({ title: '', severity: 'medium', description: '' })

function submitBugReport() {
  if (!bugForm.value.title || !bugForm.value.description) {
    ElMessage.warning('请填写完整信息')
    return
  }
  // 生产环境可调用 /api/bug-reports
  ElMessage.success('Bug 报告已提交，感谢反馈！')
  showBugReport.value = false
  bugForm.value = { title: '', severity: 'medium', description: '' }
  if (showChat.value) {
    chatMessages.value.push({ role: 'ai', content: 'Bug 报告已提交成功！我们会尽快处理。' })
  }
}
</script>

<style scoped>
.layout { height: 100vh; display: flex; flex-direction: column; }
.topbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  background: #fff;
  border-bottom: 1px solid #e2e8f0;
  box-shadow: 0 1px 3px rgba(0,0,0,0.04);
  height: 60px !important;
}
.topbar-left { display: flex; align-items: center; gap: 12px; }
.topbar-title { font-size: 18px; font-weight: 600; color: #1a202c; }
.topbar-right { display: flex; align-items: center; gap: 16px; }
.user-info { display: flex; align-items: center; gap: 6px; color: #606266; font-size: 14px; }
.readonly-banner {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  background: #fef0f0;
  color: #f56c6c;
  font-size: 13px;
  padding: 6px 0;
  border-bottom: 1px solid #fde2e2;
}
.main-container { display: flex; flex: 1; overflow: hidden; }
.sidebar {
  background: #fff;
  border-right: 1px solid #e2e8f0;
  overflow-y: auto;
}
.content {
  flex: 1;
  background: #f0f4f8;
  padding: 20px;
  overflow-y: auto;
}

/* AI 聊天助手样式 */
.ai-chat-toggle {
  position: fixed; bottom: 24px; right: 24px;
  width: 48px; height: 48px; border-radius: 50%;
  background: #409eff; color: #fff; display: flex;
  align-items: center; justify-content: center;
  font-size: 22px; cursor: pointer; box-shadow: 0 2px 12px rgba(64,158,255,.4);
  z-index: 1000; transition: transform .2s;
}
.ai-chat-toggle:hover { transform: scale(1.1); }
.chat-messages { height: 350px; overflow-y: auto; padding: 8px; }
.chat-msg { margin-bottom: 12px; }
.chat-msg.user { text-align: right; }
.chat-bubble {
  display: inline-block; max-width: 80%; padding: 8px 14px;
  border-radius: 8px; font-size: 13px; line-height: 1.6;
  text-align: left; white-space: pre-wrap;
}
.chat-msg.ai .chat-bubble { background: #f0f2f5; color: #333; }
.chat-msg.user .chat-bubble { background: #409eff; color: #fff; }
.chat-input { display: flex; padding: 8px; align-items: center; }
.ai-chat-dialog .el-dialog__body { padding: 12px; }
</style>
