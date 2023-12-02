<template>
    <PlayGround v-if="$store.state.pk.status === 'playing'"/>
    <MatchGround v-if="$store.state.pk.status === 'matching'"/>
    <ResultBoard v-if="$store.state.pk.loser != 'none'" />
</template>

<script setup>
import PlayGround from '@/components/PlayGround.vue';
import MatchGround from '@/components/MatchGround.vue'
import ResultBoard from '@/components/ResultBoard.vue'
import { onMounted, onUnmounted } from 'vue';
import { useStore } from 'vuex';

const store = useStore();
const socketUrl = `ws://localhost:3000/websocket/${store.state.user.token}`;
let socket = null;
onMounted(() => {
    store.commit("updateOpponent", {
        username: "MyOpponent",
        photo: "https://cdn.acwing.com/media/article/image/2022/08/09/1_1db2488f17-anonymous.png"
    })
    socket = new WebSocket(socketUrl);  // 建立连接

    socket.onopen = () => {
        console.log("connected!");
        store.commit("updateSocket", socket);
    }

    socket.onmessage = (msg) => {
        const data = JSON.parse(msg.data);
        if (data.event === "start-matching") { // 匹配成功
            store.commit("updateOpponent", {
                username: data.opponent_username,
                photo: data.opponent_photo,
            })
            // 匹配成功将返回对战页面
            setTimeout(() => {  // 延迟两秒
                store.commit("updateStatus", "playing")
            }, 2000);
            // 更新地图
            store.commit("updateGame", data.game);
        } else if (data.event === "move") { // 获取GameMapObject，设置Snake的移动
            console.log(data);
            const game = store.state.pk.gameObject;
            const [snake0, snake1] = game.snakes;
            snake0.set_direction(data.a_direction);
            snake1.set_direction(data.b_direction);
        } else if (data.event === "result") {
            console.log(data);
            const game = store.state.pk.gameObject;
            const [snake0, snake1] = game.snakes;

            if (data.loser === "all" || data.loser === "A") {
                snake0.status = "die";
            }
            if (data.loser === "all" || data.loser === "B") {
                snake1.status = "die";
            }
            store.commit("updateLoser", data.loser);
        }
    }

    socket.onclose = () => {
        console.log("disconnected!");
    }
});

onUnmounted(() => {
    socket.close();  // 否则容易产生冗余连接
    store.commit("updateStatus", "matching"); // 一旦退出PK页面则视为放弃游戏，重新匹配
})

</script>

<style scoped>
</style>