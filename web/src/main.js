import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import store from './store'
// 全局挂载 axios
import axios from 'axios'

const app = createApp(App)
axios.defaults.baseURL = 'http://localhost:3000'
app.config.globalProperties.$http = axios
app.use(store).use(router).mount('#app')
