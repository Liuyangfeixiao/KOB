<template>
    <div class="container">
        <div class="row">
            <div class="col-3">
                <div class="card" style="margin-top: 20px;">
                    <div class="card-body">
                        <img :src="$store.state.user.photo" alt="" style="width: 100%;">
                    </div>
                </div>
            </div>
            <div class="col-9">
                <div class="card" style="margin-top: 20px;">
                    <div class="card-header">
                        <span style="font-size: 140%;">我的Bot</span>
                        <button type="button" class="btn btn-primary float-end" data-bs-toggle="modal" data-bs-target="#add-bot">
                            创建bot
                        </button>
                        <div class="modal fade" id="add-bot" tabindex="-1">
                            <div class="modal-dialog modal-xl">
                                <div class="modal-content">
                                    <div class="modal-header">
                                        <h1 class="modal-title fs-5" id="exampleModalLabel" style="font-size: x-large;">创建Bot</h1>
                                        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                                    </div>
                                    <div class="modal-body">
                                        <div class="mb-3">
                                            <label for="add-bot-title" class="form-label" style="font-size: 120%;">Bot 名称</label>
                                            <input v-model="botadd.title" type="text" class="form-control" id="add-bot-title" placeholder="请输入Bot名称">
                                        </div>
                                        <div class="mb-3">
                                            <label for="add-bot-description" class="form-label" style="font-size: 120%;">Bot 简介</label>
                                            <textarea v-model="botadd.description" class="form-control" id="add-bot-description" rows="3" placeholder="请输入Bot简介"></textarea>
                                        </div>
                                        <div class="mb-3">
                                            <label for="add-bot-code" class="form-label" style="font-size: 120%;">Bot 代码</label>
                                            <VAceEditor
                                                v-model:value="botadd.content"
                                                @init="editorInit"
                                                lang="c_cpp"
                                                :theme="aceConfig.theme"
                                                :options="aceConfig.options"
                                                class="ace-editor"
                                                style="height: 300px" />
                                        </div>
                                    </div>
                                    <div class="modal-footer">
                                        <div class="error-message">{{ botadd.error_message }}</div>
                                        <button type="button" class="btn btn-primary" @click="add_bot">提交</button>
                                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">取消</button>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="card-body">
                        <table class="table table-striped table-hover">
                            <thead>
                                <tr>
                                    <th>名称</th>
                                    <th>创建时间</th>
                                    <th>最近修改时间</th>
                                    <th>操作</th>
                                </tr>
                            </thead>
                            <tbody>
                                <tr v-for="bot in bots" :key="bot.id">
                                    <td>{{ bot.title }}</td>
                                    <td>{{ bot.createtime }}</td>
                                    <td>{{ bot.modifytime }}</td>
                                    <td>
                                        <button type="button" class="btn btn-primary" data-bs-toggle="modal" :data-bs-target="'#update-bot'+bot.id">修改</button>
                                        <button type="button" class="btn btn-danger" @click="remove_bot(bot)">删除</button>
                                    </td>
                                    <div class="modal fade" :id="'update-bot' + bot.id" tabindex="-1">
                                        <div class="modal-dialog modal-xl">
                                            <div class="modal-content">
                                                <div class="modal-header">
                                                    <h1 class="modal-title fs-5" style="font-size: x-large;">修改Bot</h1>
                                                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                                                </div>
                                                <div class="modal-body">
                                                    <div class="mb-3">
                                                        <label for="update-bot-title" class="form-label" style="font-size: 120%;">Bot 名称</label>
                                                        <input v-model="bot.title" type="text" class="form-control" id="update-bot-title" placeholder="请输入Bot名称">
                                                    </div>
                                                    <div class="mb-3">
                                                        <label for="update-bot-description" class="form-label" style="font-size: 120%;">Bot 简介</label>
                                                        <textarea v-model="bot.description" class="form-control" id="update-bot-description" rows="3" placeholder="请输入Bot简介"></textarea>
                                                    </div>
                                                    <div class="mb-3">
                                                        <label for="update-bot-code" class="form-label" style="font-size: 120%;">Bot 代码</label>
                                                        <VAceEditor
                                                            v-model:value="bot.content"
                                                            @init="editorInit"
                                                            lang="c_cpp"
                                                            :theme="aceConfig.theme"
                                                            :options="aceConfig.options"
                                                            class="ace-editor"
                                                            style="height: 300px" />
                                                    </div>
                                                </div>
                                                <div class="modal-footer">
                                                    <div class="error-message">{{ bot.error_message }}</div>
                                                    <button type="button" class="btn btn-primary" @click="update_bot(bot)">提交</button>
                                                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">取消</button>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                </tr>
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>

<script setup>
// import ContentField from "@/components/ContentField.vue";
import { inject, reactive, ref } from "vue";
import { useStore } from "vuex";
import { Modal } from "bootstrap/dist/js/bootstrap";
import { VAceEditor } from 'vue3-ace-editor';
// import ace from 'ace-builds';
import ace from 'ace-builds';

// import "ace-builds/webpack-resolver";
import 'ace-builds/src-noconflict/mode-json';
import 'ace-builds/src-noconflict/theme-chrome';
import 'ace-builds/src-noconflict/ext-language_tools';
ace.config.set(
    "basePath", 
    "https://cdn.jsdelivr.net/npm/ace-builds@" + require('ace-builds').version + "/src-noconflict/");

const aceConfig = reactive({
    theme: 'monokai', // 主题
    arr: [
    "ambiance", "chaos", "chrome", "clouds", "clouds_midnight", "dracula", "dreamweaver", "eclipse", "github", "monokai", 
    "textmate", "tomorrow", "tomorrow_night", "xcode"
    ],
    readOnly: false, //是否只读
    options: {
                enableBasicAutocompletion: true,  // 启用基本自动补全
                enableSnippets: true,  // 启用代码段
                enableLiveAutocompletion: true,  // 启用实时自动补全
                tabSize: 2,  // 标签大小
                showPrintMargin: false,  // 去除编辑器中的竖线
                fontSize: 16,  // 字体大小
                highlightActiveLine: true, // 高亮激活行
            }
})

const store = useStore();
const axios = inject("axios");

const botadd = reactive({
    title: "",
    description: "",
    content: "",
    error_message: ""
})

let bots = ref([]);
const refresh_bots = () => {
    axios.get("/user/bot/getList/", {
        headers: {
            Authorization: "Bearer " + store.state.user.token
        }
    }).then(function (resp) {
        bots.value = resp.data;
        // console.log(resp);
    }).catch(function (err) {
        console.log(err);
    })
}

const clear_info = () => {
    botadd.title = "";
    botadd.content = "";
    botadd.description = "";
}

const add_bot = () => {
    botadd.error_message = "";
    axios.post("/user/bot/add/", {
        title: botadd.title,
        description: botadd.description,
        content: botadd.content
    }, {
        headers: {
            Authorization: "Bearer " + store.state.user.token
        }
    }).then(function (resp) {
        if (resp.data.error_message === "success") {
            refresh_bots();
            clear_info();
            Modal.getInstance("#add-bot").hide();
            alert("添加成功");
        } else {
            botadd.error_message = resp.data.error_message;
        }
        // console.log(resp)
    }).catch(function (err) {
        console.log(err)
    })
}

const update_bot = (bot) => {
    bot.error_message = "";
    axios.post("/user/bot/update/", {
        bot_id: bot.id,
        title: bot.title,
        description: bot.description,
        content: bot.content
    }, {
        headers: {
            Authorization: "Bearer " + store.state.user.token
        }
    }).then(function (resp) {
        if (resp.data.error_message === "success") {
            refresh_bots();
            // clear_info();
            Modal.getInstance("#update-bot" + bot.id).hide();
            alert("修改成功");
        } else {
            bot.error_message = resp.data.error_message;
            refresh_bots();
        }
        // console.log(resp)
    }).catch(function (err) {
        console.log(err)
    })
}

const remove_bot = (bot) => {
    axios.post("user/bot/remove/", {
        bot_id: bot.id
    },{
        headers: {
            Authorization: "Bearer " + store.state.user.token
        }
    }).then(function (resp) {
        if (resp.data.error_message === "success") {
            refresh_bots();
            alert("删除成功");
        } else {
            alert(resp.data.error_message);
        }
    }).catch(function (err) {
        console.log(err);
    })
}

refresh_bots();


// axios.post("/user/bot/remove/", {
//     bot_id: 6
// }, {
//     headers: {
//         Authorization: "Bearer " + store.state.user.token
//     }
// }).then(function (resp) {
//     console.log(resp)
// }).catch(function (err) {
//     console.log(err)
// })

</script>

<style scoped>
button {
    margin-right: 10px;
}
div.error-message {
    color: red;
}
</style>
