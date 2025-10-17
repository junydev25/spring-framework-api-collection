<template>
    <h1>게시판</h1>
    <RouterLink :to="{ name: 'postCreate' }">
        <button>새 게시글 작성</button>
    </RouterLink>

    <div>
        <p>총 {{ totalCount }}개의 게시글</p>
        <div v-for="post in posts" :key="post.id">
            


            <span>{{ post.title }} / </span>
            <span>카테고리: {{ getCategoryName(post.category) }} / </span>
            <span>작성자: {{ post.author }} / </span>
            <span>작성일: {{ formatDate(post.createdAt) }} / </span>
            <RouterLink :to="{ name: 'postDetail', params: { id: post.id } }">
                <button>자세히</button>
            </RouterLink>
        </div>
    </div>

    <div>
        <select v-model="filters.category">
            <option value="">전체 카테고리</option>
            <option value="IT">IT</option>
            <option value="Game">게임</option>
            <option value="Sports">스포츠</option>
        </select>
        <input v-model="filters.author" type="text" placeholder="작성자 검색"/>

        <button @click="fetchPosts">검색</button>
    </div>
</template>

<script setup>
    import { ref, reactive, onMounted } from 'vue'
    import { RouterLink, useRouter } from 'vue-router'
    import axios from 'axios'

    const router = useRouter()
    const API_BASE_URL = "http://localhost:8080/api"

    const posts = ref([])
    const totalCount = ref(0)

    const filters = reactive({
        category: "",
        author: ""
    })

    const categoryNames = {
        it: "IT",
        game: "게임",
        sports: "스포츠"
    }

    const getCategoryName = (category) => {
        return categoryNames[category] || category
    }

    const formatDate = (dateString) => {
        if (!dateString) return ''
        const date = new Date(dateString)
        return date.toLocaleString('ko-KR')
    }

    const fetchPosts = async () => {
        try {
            const params = {}
            if (filters.category) params.category = filters.category
            if (filters.author) params.author = filters.author

            const response = await axios.get(`${API_BASE_URL}/posts`, { params })
            
            if (response.data.status === 'success') {
                posts.value = response.data.data.posts
                totalCount.value = response.data.data.totalCount
            }
        } catch (error) {
            console.error('게시글 목록 조회 실패:', error)
            alert('게시글 목록을 불러오는데 실패했습니다.')
        }
    }

    onMounted(() => {
        fetchPosts()
    })
</script>

<style lang="scss" scoped>

</style>