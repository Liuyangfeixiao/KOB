// import { inject } from "vue";
import axios from "axios";
export default {
    state: {
        id: "",
        username: "",
        photo: "",
        token: "",
        is_login: false,
    },
    getters: {
    },
    mutations: { // 同步函数
        updateUser(state, user) {
            state.id = user.id;
            state.username = user.username;
            state.photo = user.photo;
            state.is_login = user.is_login;
        },
        updateToken(state, token) {
            state.token = token;
        },
        logout(state) {
            state.id = "";
            state.username = "";
            state.photo = "";
            state.token = "";
            state.is_login = false;
        }
    },
    actions: { // 异步函数, 存放修改state值的函数
        login(context, data) {
            // const axios = inject("axios");
            // console.log(axios);
            axios.post("http://localhost:3000/user/account/token/", {
                username: data.username,
                password: data.password
            })
            .then(function (resp) {
                if (resp.data.error_message === "success") {
                    localStorage.setItem("jwt_token", resp.data.token);
                    context.commit("updateToken", resp.data.token);
                    data.success();  // 获得用户信息并存储
                } else {
                    data.error(resp);
                }
            })
            .catch(function (err) {
                data.error(err);
            })
        },
        getinfo(context, data) {
            axios.get("http://localhost:3000/user/account/info/", {
                headers: {
                    Authorization: "Bearer " + context.state.token,
                }
            }).then(function (resp) {
                if (resp.data.error_message === "success") {
                    context.commit("updateUser", {
                        ...resp.data,
                        is_login: true,
                    });
                    data.success(); // 跳转到主页面
                } else {
                    data.error(resp);
                }
            }).catch(function (err) {
                data.error(err);
            })
        },
        logout(context) {
            localStorage.removeItem("jwt_token");
            context.commit("logout");
        }
    },
    modules: {
    }
}