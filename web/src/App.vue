<template>
  <div>
    <div v-for="name, idx in names" :key="idx">Botname: {{ name }}</div>
  </div>
  <router-view/>
</template>

<script setup>
import { onMounted, ref, inject } from 'vue';
const axios = inject('axios')
const names = ref([])
onMounted(async () => {
  const res = await axios.get("pk/getbotinfo/")
  names.value = res.data
})
</script>

<style>
body {
  background-image: url("@/assets/background.png");
  background-size: cover;
}
</style>

