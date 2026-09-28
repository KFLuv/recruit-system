<template>
  <div class="login-container">
    <div class="login-card">
      <!-- 顶部图标 -->
      <div class="login-logo">
        <span class="login-logo-icon">📋</span>
      </div>
      <h2 class="login-title">人力招聘全流程数字化归档管理系统</h2>
      <el-form
        ref="formRef"
        :model="loginForm"
        :rules="rules"
        size="large"
        @keyup.enter="handleLogin"
      >
        <el-form-item prop="username">
          <el-input
            v-model="loginForm.username"
            placeholder="用户名"
            :prefix-icon="User"
          />
        </el-form-item>
        <el-form-item prop="password">
          <el-input
            v-model="loginForm.password"
            type="password"
            placeholder="密码"
            show-password
            :prefix-icon="Lock"
          />
        </el-form-item>
        <el-form-item>
          <el-button
            type="primary"
            size="large"
            style="width: 100%"
            :loading="loading"
            @click="handleLogin"
          >
            登 录
          </el-button>
        </el-form-item>
        <div class="login-footer">
          <span>还没有账号？</span>
          <el-link type="primary" :underline="false" @click="openRegister">注册账号</el-link>
        </div>
      </el-form>
      <p v-if="errorMsg" style="color: #f56c6c; text-align: center; margin-top: 10px;">
        {{ errorMsg }}
      </p>
    </div>

    <!-- 注册弹窗 -->
    <el-dialog v-model="showRegister" title="注册账号" width="420px" :close-on-click-modal="false">
      <el-form ref="registerFormRef" :model="registerForm" :rules="registerRules" label-width="80px">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="registerForm.username" placeholder="请输入登录用户名" :prefix-icon="User" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="registerForm.password" type="password" placeholder="请输入密码" show-password :prefix-icon="Lock" />
        </el-form-item>
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input v-model="registerForm.confirmPassword" type="password" placeholder="请再次输入密码" show-password :prefix-icon="Lock" />
        </el-form-item>
        <el-form-item label="姓名" prop="realName">
          <el-input v-model="registerForm.realName" placeholder="选填，默认同用户名" />
        </el-form-item>
        <el-form-item label="账号等级" prop="role">
          <el-radio-group v-model="registerForm.role">
            <el-radio value="readonly">只读（仅可查看）</el-radio>
            <el-radio value="admin">管理员（可操作）</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showRegister = false">取 消</el-button>
        <el-button type="primary" :loading="registerLoading" @click="handleRegister">注 册</el-button>
      </template>
    </el-dialog>

    <!-- AI 聊天助手 -->
    <AiChatAssistant />
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, Lock } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'
import { login, register } from '@/api/auth'
import AiChatAssistant from '@/components/AiChatAssistant.vue'

const router = useRouter()
const userStore = useUserStore()
const formRef = ref(null)
const loading = ref(false)
const errorMsg = ref('')

const loginForm = reactive({
  username: '',
  password: ''
})

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

async function handleLogin() {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  loading.value = true
  errorMsg.value = ''
  try {
    const res = await login(loginForm.username, loginForm.password)
    if (res.success) {
      userStore.setToken(res.data?.token || 'logged')
      if (res.data) userStore.setUserInfo(res.data)
      router.push('/dashboard')
    } else {
      errorMsg.value = res.message || '登录失败'
    }
  } catch (e) {
    errorMsg.value = e.message || '网络错误'
  } finally {
    loading.value = false
  }
}

/* ---------- 注册 ---------- */
const showRegister = ref(false)
const registerLoading = ref(false)
const registerFormRef = ref(null)
const registerForm = reactive({
  username: '',
  password: '',
  confirmPassword: '',
  realName: '',
  role: 'readonly'
})

const validateConfirm = (rule, value, callback) => {
  if (value !== registerForm.password) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

const registerRules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 4, message: '密码至少 4 位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请再次输入密码', trigger: 'blur' },
    { validator: validateConfirm, trigger: 'blur' }
  ],
  role: [{ required: true, message: '请选择账号等级', trigger: 'change' }]
}

function openRegister() {
  registerForm.username = ''
  registerForm.password = ''
  registerForm.confirmPassword = ''
  registerForm.realName = ''
  registerForm.role = 'readonly'
  showRegister.value = true
}

async function handleRegister() {
  const valid = await registerFormRef.value.validate().catch(() => false)
  if (!valid) return

  registerLoading.value = true
  try {
    const res = await register({
      username: registerForm.username,
      password: registerForm.password,
      realName: registerForm.realName,
      role: registerForm.role
    })
    if (res.success) {
      ElMessage.success('注册成功，请登录')
      loginForm.username = registerForm.username
      loginForm.password = ''
      showRegister.value = false
    }
  } catch (e) {
    // 错误已由拦截器提示
  } finally {
    registerLoading.value = false
  }
}
</script>

<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}
.login-card {
  width: 420px;
  padding: 40px;
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 20px 60px rgba(0,0,0,0.15);
  position: relative;
}
.login-logo {
  text-align: center;
  margin-bottom: 8px;
}
.login-logo-icon {
  display: inline-flex;
  width: 64px;
  height: 64px;
  border-radius: 16px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  align-items: center;
  justify-content: center;
  font-size: 32px;
  box-shadow: 0 4px 16px rgba(102,126,234,0.4);
}
.login-title {
  text-align: center;
  margin-bottom: 30px;
  font-size: 20px;
  color: #303133;
}
.login-footer {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 4px;
  margin-top: 4px;
  color: #909399;
  font-size: 14px;
}
</style>
