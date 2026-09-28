<template>
  <div>
    <!-- 浮动按钮 -->
    <div class="chat-fab-wrapper">
      <button class="chat-fab" @click="toggleChat">
        <span v-if="!visible" style="font-size:24px;">💬</span>
        <span v-else style="font-size:20px;">✕</span>
      </button>
      <span class="chat-fab-badge" v-if="!visible">AI</span>
    </div>

    <!-- 聊天框 -->
    <div class="chat-box" :class="{ hidden: !visible }">
      <div class="chat-header">
        <span class="chat-header-icon">🤖</span>
        <span class="chat-header-title">AI 助手</span>
        <button class="chat-close" @click="visible = false">✕</button>
      </div>
      <div class="chat-messages" ref="msgRef">
        <div v-for="(msg, i) in messages" :key="i" :class="'chat-msg ' + msg.role">
          <div class="chat-avatar">{{ msg.role === 'ai' ? '🤖' : '👤' }}</div>
          <div class="chat-bubble" v-html="msg.content">
          </div>
          <div class="chat-time">刚刚</div>
        </div>
      </div>
      <div class="chat-footer">
        <div class="chat-quick-keys">
          <span v-for="key in quickKeys" :key="key.word" class="quick-key" @click="quickSend(key.word)">{{ key.icon }} {{ key.label }}</span>
        </div>
        <div class="chat-input-row">
          <input v-model="inputText" class="chat-input" placeholder="输入消息..." @keyup.enter="sendMessage" />
          <button class="chat-send-btn" @click="sendMessage">➤</button>
        </div>
      </div>
    </div>

    <!-- Bug 报告弹窗 -->
    <div class="bug-overlay" :class="{ hidden: !showBug }" @click.self="showBug = false">
      <div class="bug-modal">
        <div class="bug-header">
          <span>🐛 提交 Bug 报告</span>
          <button class="bug-close" @click="showBug = false">✕</button>
        </div>
        <div class="bug-body">
          <label>标题 *</label>
          <input v-model="bugTitle" placeholder="简短的描述问题" />
          <label>严重程度 *</label>
          <select v-model="bugSeverity">
            <option value="low">低 - 轻微体验问题</option>
            <option value="medium">中 - 功能受影响</option>
            <option value="high">高 - 核心功能不可用</option>
            <option value="critical">严重 - 系统崩溃</option>
          </select>
          <label>详细描述 *</label>
          <textarea v-model="bugDesc" :rows="4" placeholder="请描述复现步骤、期望结果、实际结果..."></textarea>
        </div>
        <div class="bug-footer">
          <button class="bug-btn cancel" @click="showBug = false">取消</button>
          <button class="bug-btn submit" @click="submitBug">提交报告</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, nextTick } from 'vue'

const visible = ref(false)
const inputText = ref('')
const msgRef = ref(null)
const showBug = ref(false)
const bugTitle = ref('')
const bugSeverity = ref('medium')
const bugDesc = ref('')

const messages = ref([
  {
    role: 'ai',
    content:
      '您好！我是招聘系统 AI 助手 👋<br><br>' +
      '输入关键词获取帮助：<br>' +
      '🔹 <b>帮助</b> — 功能总览<br>' +
      '🔹 <b>计划</b> — 招聘计划操作<br>' +
      '🔹 <b>候选人</b> — 档案管理<br>' +
      '🔹 <b>审批</b> — 审批流程<br>' +
      '🔹 <b>预算</b> — 预算编制<br>' +
      '🔹 <b>bug</b> — 提交 Bug 报告<br>' +
      '🔹 <b>联系</b> — 联系平台负责人'
  }
])

const quickKeys = [
  { icon: '📖', label: '帮助', word: '帮助' },
  { icon: '📋', label: '计划', word: '计划' },
  { icon: '👤', label: '候选人', word: '候选人' },
  { icon: '✅', label: '审批', word: '审批' },
  { icon: '💰', label: '预算', word: '预算' },
  { icon: '🐛', label: 'Bug', word: 'bug' },
  { icon: '📞', label: '联系', word: '联系' }
]

const replies = {
  '帮助|功能|使用':
    '📖 <b>常用功能指引：</b><br>' +
    '1️⃣ <b>新建招聘计划</b> — 左侧"招聘计划" → 点"新建计划"填写信息<br>' +
    '2️⃣ <b>添加候选人</b> — "候选人管理" → "新增" 填写档案<br>' +
    '3️⃣ <b>提交审批</b> — 计划编辑完成后点"保存并提交"<br>' +
    '4️⃣ <b>审批处理</b> — "审批管理" → 审批 → 通过/驳回<br>' +
    '5️⃣ <b>上传简历</b> — 新增/编辑候选人时上传PDF简历',
  '计划|招聘计划':
    '📋 <b>招聘计划操作说明：</b><br>' +
    '• <b>新建</b>：填写年度、名称、单位、预算、岗位明细等<br>' +
    '• <b>编辑/删除</b>：仅草稿状态可编辑和删除；已退回可编辑但不可删除<br>' +
    '• <b>提交审批</b>：草稿状态下点击"提交审批"，系统校验编制预算<br>' +
    '• <b>批量操作</b>：勾选多个草稿可批量提交/作废<br>' +
    '• <b>状态含义</b>：草稿→审批中→已通过/已退回→归档',
  '候选人|档案':
    '👤 <b>候选人管理说明：</b><br>' +
    '• <b>新增</b>：填写基础信息、应聘信息、工作/项目经历<br>' +
    '• <b>上传简历</b>：支持PDF格式，自动关联到候选人档案<br>' +
    '• <b>成绩评定</b>：填写笔试/面试分数，系统自动计算总分<br>' +
    '• <b>状态流转</b>：新申请→初筛→面试→拟录用→已入职/淘汰',
  '审批|审核':
    '✅ <b>审批操作说明：</b><br>' +
    '• 待审批事项在"审批管理"页查看，可用 <b>⟳ 刷新</b> 按钮重新加载<br>' +
    '• 点击"审批"按钮，填写意见后选择通过或驳回<br>' +
    '• 通过后计划状态变为"已通过"，可继续执行招聘<br>' +
    '• 驳回后计划变为"已退回"，可编辑修改后提交再次审批',
  '预算|编制':
    '💰 <b>预算与编制说明：</b><br>' +
    '• 提交审批时会自动校验年度预算总额是否超限<br>' +
    '• 预算在"年度预算配置"中管理<br>' +
    '• 岗位编制数会累加校验，超过预算上限会提示',
  '你好|您好':
    '您好！我是 AI 助手，输入关键词获取帮助：<br>' +
    '<b>帮助</b> — 功能总览<br>' +
    '<b>计划</b> — 招聘计划操作<br>' +
    '<b>候选人</b> — 档案管理<br>' +
    '<b>审批</b> — 审批流程<br>' +
    '<b>预算</b> — 预算编制<br>' +
    '<b>bug</b> — 提交 Bug 报告<br>' +
    '<b>联系</b> — 联系平台负责人',
  'bug|上报|报告':
    '🐛 如需提交 Bug 报告，请点击下方按钮：<br>' +
    '<button onclick="document.querySelector(\'.bug-overlay\')?.classList.remove(\'hidden\')" style="margin-top:8px;padding:6px 16px;background:#409eff;color:#fff;border:none;border-radius:6px;cursor:pointer;">📝 去提交 Bug 报告</button>',
  '联系|微信|电话|负责人':
    '📞 <b>平台负责人联系方式：</b><br>' +
    '微信：<b>YOUR_WECHAT</b><br>' +
    '手机：<b>YOUR_PHONE</b>'
}

function toggleChat() {
  visible.value = !visible.value
  if (visible.value) {
    nextTick(() => scrollToBottom())
  }
}

function quickSend(word) {
  inputText.value = word
  sendMessage()
}

function sendMessage() {
  const text = inputText.value.trim()
  if (!text) return
  messages.value.push({ role: 'user', content: text })
  inputText.value = ''

  let reply = '您好！我是 AI 助手。您可以输入：<b>帮助</b>、<b>计划</b>、<b>候选人</b>、<b>审批</b>、<b>预算</b>、<b>bug</b>、<b>联系</b> 获取对应帮助。'
  for (const [key, value] of Object.entries(replies)) {
    const kws = key.split('|')
    if (kws.some(k => text.includes(k))) {
      reply = value
      break
    }
  }

  if (text.includes('bug') || text.includes('上报') || text.includes('报告')) {
    setTimeout(() => { showBug.value = true }, 500)
  }

  setTimeout(() => {
    messages.value.push({ role: 'ai', content: reply })
    nextTick(() => scrollToBottom())
  }, 300)
}

function scrollToBottom() {
  if (msgRef.value) {
    msgRef.value.scrollTop = msgRef.value.scrollHeight
  }
}

function submitBug() {
  if (!bugTitle.value || !bugDesc.value) {
    alert('请填写完整信息')
    return
  }
  alert('Bug 报告已提交，感谢反馈！')
  showBug.value = false
  bugTitle.value = ''
  bugSeverity.value = 'medium'
  bugDesc.value = ''
  if (visible.value) {
    messages.value.push({ role: 'ai', content: '✅ Bug 报告已提交成功！我们会尽快处理。' })
    nextTick(() => scrollToBottom())
  }
}
</script>

<style scoped>
/* === 浮动按钮 === */
.chat-fab-wrapper {
  position: fixed; bottom: 24px; right: 24px;
  z-index: 1000;
}
.chat-fab {
  width: 52px; height: 52px; border-radius: 50%;
  background: linear-gradient(135deg, #1a73e8, #4a90d9);
  color: white; border: none; font-size: 24px; cursor: pointer;
  box-shadow: 0 4px 16px rgba(26,115,232,0.3);
  transition: all 0.2s;
  display: flex; align-items: center; justify-content: center;
}
.chat-fab:hover { transform: scale(1.08); box-shadow: 0 6px 24px rgba(26,115,232,0.4); }
.chat-fab-badge {
  position: absolute; top: -6px; right: -6px;
  background: #dc2626; color: #fff;
  font-size: 10px; font-weight: 700;
  padding: 1px 5px; border-radius: 8px;
  line-height: 16px;
  box-shadow: 0 2px 6px rgba(220,38,38,0.4);
  animation: aiPulse 2s infinite;
}
@keyframes aiPulse {
  0%, 100% { box-shadow: 0 2px 6px rgba(220,38,38,0.4); }
  50% { box-shadow: 0 2px 12px rgba(220,38,38,0.7); }
}

/* === 聊天框 === */
.chat-box {
  position: fixed; bottom: 88px; right: 24px;
  width: 400px; height: 520px; background: white;
  border-radius: 16px; box-shadow: 0 8px 40px rgba(0,0,0,0.12);
  border: 1px solid #e2e8f0; display: flex; flex-direction: column;
  z-index: 1001; overflow: hidden; animation: modalIn 0.2s ease;
}
.chat-box.hidden { display: none; }
@keyframes modalIn {
  from { opacity: 0; transform: translateY(12px) scale(0.96); }
  to { opacity: 1; transform: translateY(0) scale(1); }
}

.chat-header {
  padding: 14px 20px; border-bottom: 1px solid #e2e8f0;
  display: flex; align-items: center; gap: 8px;
  background: linear-gradient(135deg, #1a73e8, #4a90d9);
  color: white; flex-shrink: 0;
}
.chat-header-icon { font-size: 22px; }
.chat-header-title { font-size: 15px; font-weight: 600; flex: 1; }
.chat-close {
  width: 28px; height: 28px; border-radius: 50%; border: none;
  background: rgba(255,255,255,0.2); color: white; font-size: 14px;
  cursor: pointer; display: flex; align-items: center; justify-content: center;
}
.chat-close:hover { background: rgba(255,255,255,0.35); }

/* === 消息区 === */
.chat-messages {
  flex: 1; overflow-y: auto; padding: 12px 16px;
  display: flex; flex-direction: column; gap: 12px;
}
.chat-msg {
  display: flex; gap: 8px; align-items: flex-start;
}
.chat-msg.user { flex-direction: row-reverse; }
.chat-avatar {
  width: 32px; height: 32px; border-radius: 50%;
  display: flex; align-items: center; justify-content: center;
  font-size: 16px; flex-shrink: 0;
  background: #f0f2f5;
}
.chat-bubble {
  max-width: 80%; padding: 10px 14px;
  border-radius: 12px; font-size: 13px; line-height: 1.7;
  color: #333; background: #f0f2f5;
}
.chat-msg.user .chat-avatar { background: #409eff; }
.chat-msg.user .chat-bubble { background: #409eff; color: white; }
.chat-time { font-size: 10px; color: #999; align-self: flex-end; margin-top: 4px; }

/* === 底部 === */
.chat-footer { border-top: 1px solid #e2e8f0; flex-shrink: 0; }
.chat-quick-keys {
  display: flex; flex-wrap: wrap; gap: 4px;
  padding: 8px 12px; border-bottom: 1px solid #f0f2f5;
}
.quick-key {
  padding: 3px 10px; border-radius: 14px; background: #f0f2f5;
  font-size: 12px; cursor: pointer; transition: all 0.15s;
  white-space: nowrap; color: #555;
}
.quick-key:hover { background: #409eff; color: white; }

.chat-input-row {
  display: flex; align-items: center; gap: 8px;
  padding: 8px 12px;
}
.chat-input {
  flex: 1; height: 36px; border: 1px solid #dcdfe6;
  border-radius: 18px; padding: 0 16px; font-size: 13px;
  outline: none; transition: border-color 0.2s;
}
.chat-input:focus { border-color: #409eff; }
.chat-send-btn {
  width: 36px; height: 36px; border-radius: 50%;
  background: #409eff; color: white; border: none;
  font-size: 16px; cursor: pointer; display: flex;
  align-items: center; justify-content: center;
  transition: background 0.2s;
}
.chat-send-btn:hover { background: #337ecc; }

/* === Bug 报告弹窗 === */
.bug-overlay {
  position: fixed; inset: 0; background: rgba(0,0,0,0.4);
  display: flex; align-items: center; justify-content: center;
  z-index: 2000;
}
.bug-overlay.hidden { display: none; }
.bug-modal {
  width: 480px; background: white; border-radius: 16px;
  box-shadow: 0 20px 60px rgba(0,0,0,0.15); overflow: hidden;
}
.bug-header {
  padding: 16px 20px; border-bottom: 1px solid #e2e8f0;
  display: flex; align-items: center; justify-content: space-between;
  font-weight: 600; font-size: 16px;
}
.bug-close {
  width: 28px; height: 28px; border-radius: 50%; border: none;
  background: #f0f2f5; cursor: pointer; font-size: 14px;
}
.bug-body { padding: 20px; }
.bug-body label { display: block; font-size: 13px; font-weight: 600; margin-bottom: 4px; margin-top: 12px; }
.bug-body label:first-child { margin-top: 0; }
.bug-body input, .bug-body select, .bug-body textarea {
  width: 100%; padding: 8px 12px; border: 1px solid #dcdfe6;
  border-radius: 8px; font-size: 13px; outline: none; box-sizing: border-box;
}
.bug-body textarea { resize: vertical; }
.bug-footer { padding: 12px 20px; border-top: 1px solid #e2e8f0; display: flex; gap: 8px; justify-content: flex-end; }
.bug-btn { padding: 8px 20px; border-radius: 8px; border: none; cursor: pointer; font-size: 13px; }
.bug-btn.cancel { background: #f0f2f5; color: #666; }
.bug-btn.submit { background: #409eff; color: white; }
.bug-btn.submit:hover { background: #337ecc; }
</style>
