import { createRouter, createWebHistory } from 'vue-router'
import PKIndexView from '../views/pk/PKIndexView'
import RanklistIndexView from '../views/ranklist/RanklistIndexView'
import RecordIndexView from '../views/record/RecordIndexView'
import UserBotIndexView from '../views/user/bot/UserBotIndexView'
import NotFound from '../views/error/NotFound'
import UserAccountLoginView from '@/views/user/account/UserAccountLoginView'
import UserAccountRegisterView from '@/views/user/account/UserAccountRegisterView'

import store from '@/store/index'

const routes = [
  {
    path: "/",
    name: "home",
    redirect: "/pk/",
    meta: {
      requstAuth: true,  // 表示是否需要授权
    }
  },
  {
    path: "/pk/",
    name: "pk_index",
    component: PKIndexView,
    meta: {
      requstAuth: true,  // 表示是否需要授权
    }
  },
  {
    path: "/record/",
    name: "record_index",
    component: RecordIndexView,
    meta: {
      requstAuth: true,  // 表示是否需要授权
    }
  },
  {
    path: "/ranklist/",
    name: "ranklist_index",
    component: RanklistIndexView,
    meta: {
      requstAuth: true,  // 表示是否需要授权
    }
  },
  {
    path: "/user/bot/",
    name: "user_bot_index",
    component: UserBotIndexView,
    meta: {
      requstAuth: true,  // 表示是否需要授权
    }
  },
  {
    path: "/user/account/login/",
    name: "user_account_login",
    component: UserAccountLoginView,
    meta: {
      requstAuth: false,  // 表示是否需要授权
    }
  },
  {
    path: "/user/account/register/",
    name: "user_account_register",
    component: UserAccountRegisterView,
    meta: {
      requstAuth: false,  // 表示是否需要授权
    }
  },
  {
    path: "/404/",
    name: "404",
    component: NotFound,
    meta: {
      requstAuth: false,  // 表示是否需要授权
    }
  },
  {
    path: "/:catchAll(.*)",
    redirect: "/404/"
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  const jwt_token = localStorage.getItem('jwt_token');
  if (jwt_token) {
    store.commit("updateToken", jwt_token);
    store.dispatch("getinfo", {
      success() {
        next();
      },
      error(err) {
        console.log(err);
        store.dispatch("logout");
        alert("token无效, 请重新登录！");
        next({name: "use_account_login"});
      }
    })
  } else {
    if (to.meta.requstAuth && !store.state.user.is_login) {
      next({name: "user_account_login"});
    } else {
      next();
    }
  }
})
export default router
