import { createApp } from 'vue'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'
import zhCn from 'element-plus/dist/locale/zh-cn.mjs'
import { createPinia } from 'pinia'
import App from './App.vue'
import router from './router'
import { useUserStore } from '@/stores/user'

const app = createApp(App)

// 注册所有 Element Plus 图标
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component)
}

const pinia = createPinia()
app.use(pinia)

// 权限指令：只读账号隐藏写操作元素
// 用法：v-perm="'write'"
app.directive('perm', {
  mounted(el, binding) {
    const userStore = useUserStore(pinia)
    if (userStore.userInfo?.role === 'readonly' && binding.value === 'write') {
      el.style.display = 'none'
    }
  }
})

app.use(ElementPlus, { locale: zhCn })
app.use(router)
app.mount('#app')
