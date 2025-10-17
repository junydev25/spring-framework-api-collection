<template>
    <div v-for="api in apiList" :key="api.path">
        <button @click="move(api.path)">
            {{ api.name }}으로 이동
        </button>
    </div>
</template>

<script setup>
    import { ref } from 'vue'
    import { useRouter } from 'vue-router'

    const apiList =ref([
        {name: "게시판", path: "/posts"}
    ])

    const router = useRouter()

    const move = (path) => {
        router.push(path)
    }

</script>

<style lang="scss" scoped>

</style>