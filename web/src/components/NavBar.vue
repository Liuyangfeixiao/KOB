<template>
<nav class="navbar navbar-expand-lg bg-body-tertiary" data-bs-theme="dark">
  <div class="container">
    <router-link class="navbar-brand" :to="{name: 'home'}">
        <img src="..\assets\images\kob.png" alt="Logo" width="30" height="30" class="d-inline-block align-text-top">
        King of Bots
    </router-link>
    <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarSupportedContent" aria-controls="navbarSupportedContent" aria-expanded="false" aria-label="Toggle navigation">
      <span class="navbar-toggler-icon"></span>
    </button>
    <div class="collapse navbar-collapse" id="navbarSupportedContent">
      <ul class="navbar-nav me-auto mb-2 mb-lg-0">
        <li class="nav-item">
          <router-link class="nav-link" active-class="active" :to="{name: 'pk_index'}">对战</router-link>
        </li>
        <li class="nav-item">
          <router-link class="nav-link" active-class="active" :to="{name: 'record_index'}">对局列表</router-link>
        </li>
        <li class="nav-item">
          <router-link class="nav-link" active-class="active" :to="{name: 'ranklist_index'}">排行榜</router-link>
        </li>
      </ul>
      <ul class="navbar-nav" v-if="$store.state.user.is_login">
        <li class="nav-item dropdown">
          <a class="nav-link dropdown-toggle" href="#" role="button" data-bs-toggle="dropdown" aria-expanded="false">
            {{ $store.state.user.username }}
          </a>
          <ul class="dropdown-menu">
            <li>
                <router-link class="dropdown-item" :to="{name: 'user_bot_index'}">我的Bot</router-link>
            </li>
            
            <li><hr class="dropdown-divider"></li>
            <li><a class="dropdown-item" href="#" @click="logout">退出</a></li>
          </ul>
        </li>
      </ul>
      <ul class="navbar-nav" v-else>
        <li class="nav-item">
          <router-link class="nav-link" :to="{name: 'user_account_login' }" role="button">
            登录
          </router-link>
        </li>
        <li class="nav-item">
          <router-link class="nav-link" :to="{name: 'user_account_register'}" role="button">
            注册
          </router-link>
        </li>
      </ul>

    </div>
  </div>
</nav>
</template>

<script setup>
import { useStore } from 'vuex';
const store = useStore();
const logout = () => {
  store.dispatch("logout");
}
</script>


<style scoped>

</style>