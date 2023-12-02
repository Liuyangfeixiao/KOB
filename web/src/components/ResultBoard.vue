<template>
    <div class="result-board">
        <div class="result-board-text">
            {{ result_msg }}
        </div>
        <div class="result-board-btn">
            <button type="button" class="btn btn-warning btn-lg" @click="restart">
                重新匹配
            </button>
        </div>
    </div>

</template>

<script setup>
import { ref } from 'vue';
import { useStore } from 'vuex';
const store = useStore();
let result_msg = ref('');
if (store.state.pk.loser === 'all') {
    result_msg.value = "Draw";
} else if (store.state.pk.loser === 'A' && store.state.pk.a_id === parseInt(store.state.user.id)) {
    result_msg.value = "Lose";
} else if (store.state.pk.loser === 'B' && store.state.pk.b_id === parseInt(store.state.user.id)) {
    result_msg.value = "Lose";
} else {
    result_msg.value = "Win";
}

const restart = () => {
    store.commit("updateStatus", "matching");  // 界面显示为匹配界面
    store.commit("updateLoser", "none");  // 记分板消失
    store.commit("updateOpponent", {
        username: "MyOpponent",
        photo: "https://cdn.acwing.com/media/article/image/2022/08/09/1_1db2488f17-anonymous.png"
    })
}
</script>

<style scoped>
div.result-board {
    height: 30vh;
    width: 30vw;
    background-color: rgba(50, 50, 50, 0.5);
    position: absolute;
    top: 30vh;
    left: 35vw;
    border-radius: 2rem;
}
div.result-board-text {
    text-align: center;
    color: aliceblue;
    font-size: 60px;
    font-weight: 600;
    font-style: italic;
    padding-top: 6vh;
}
div.result-board-btn {
    padding-top: 6vh;
    text-align: center;
}
</style>