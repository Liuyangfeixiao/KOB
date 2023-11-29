<template>
    <div class="gamemap" ref="parent">
        <canvas ref="canvas" tabindex="0"></canvas>
    </div>
</template>

<script setup>
import { GameMap } from '@/assets/scripts/GameMap';
import { onMounted, ref } from 'vue';
import { useStore } from 'vuex';

const store = useStore();
let parent = ref(null);
let canvas = ref(null);

onMounted(() => {
    new GameMap(canvas.value.getContext('2d'), parent.value, store);
})
</script>

<style scoped>
div.gamemap {
    width: 100%;
    height: 100%;
    display: flex;
    justify-content: center;
    align-items: center;
}
</style>
