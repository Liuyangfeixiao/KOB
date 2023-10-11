import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import store from './store'
// 全局挂载 axios
import axios from 'axios'
import VueAxios from 'vue-axios'

const app = createApp(App)
axios.defaults.baseURL = 'http://localhost:3000'

app.use(VueAxios, axios)
app.use(store).use(router)
app.provide('axios', app.config.globalProperties.axios) // provide 'axios'
app.mount('#app')