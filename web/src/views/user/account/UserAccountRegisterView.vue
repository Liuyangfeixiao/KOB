<template>
    <ContentField>
        <div class="row justify-content-md-center">
            <div class="col-3">
                <form @submit.prevent="register">
                    <div class="mb-3">
                        <label for="username" class="form-label">用户名</label>
                        <input v-model="username" type="text" class="form-control" id="username" placeholder="请输入用户名">
                    </div>
                    <div class="mb-3">
                        <label for="password" class="form-label">密码</label>
                        <input v-model="password" type="password" class="form-control" id="password" placeholder="请输入密码">
                    </div>
                    <div class="mb-3">
                        <label for="confirmedPassword" class="form-label">确认密码</label>
                        <input v-model="confirmedPassword" type="password" class="form-control" id="confirmedPassword" placeholder="请再次输入密码">
                    </div>
                    <div class="error-message">{{ error_message }}</div>
                    <button type="submit" class="btn btn-primary">提交</button>
                </form>
            </div>
        </div>
    </ContentField>
</template>

<script setup>
import ContentField from "@/components/ContentField.vue";
import { inject, ref } from "vue";
// import { useStore } from 'vuex'
import router from '@/router/index'
// import { Alert } from "bootstrap";

// const store = useStore();
let username = ref('');
let password = ref('');
let confirmedPassword = ref('');
let error_message = ref('');
const axios = inject('axios');

const register = async() => {
    await axios.post("/user/account/register/", {  // 用inject导入的axios设置了base_url: http://localhost:3000
                username: username.value,
                password: password.value,
                confirmedPassword: confirmedPassword.value
            })
            .then(function (resp) {
                if (resp.data.error_message === "success") {
                    router.push({name: "user_account_login"});
                } else {
                    alert(resp.data.error_message);
                    // error_message.value = resp.data.error_message;
                }
            })
            .catch(function (err) {
                console.log(err);
            })
}

</script>

<style scoped>
button {
    width: 100%;
}
div.error-message {
    color: red;
}
</style>
