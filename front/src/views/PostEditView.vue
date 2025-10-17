<template>
    <h2>게시글 수정</h2>
    <form @submit.prevent="submitPost" v-if="isLoaded">
        <div>
            <label>제목:</label>
            <input v-model="form.title" type="text" required />
        </div>

        <div>
            <label>작성자:</label>
            <input v-model="form.author" type="text" disabled />
        </div>

        <div>
            <label>카테고리:</label>
            <select v-model="form.category" required>
                <option value="">선택하세요</option>
                <option value="it">IT</option>
                <option value="game">게임</option>
                <option value="sports">스포츠</option>
            </select>
        </div>

        <div>
            <label>내용:</label>
            <textarea v-model="form.content" rows="10" required></textarea>
        </div>

        <div>
            <button type="submit">수정</button>
            <button type="button" @click="cancel">취소</button>
        </div>
    </form>

    <div v-else>
        <p>게시글을 불러오는 중...</p>
    </div>
</template>

<script setup>
    import { ref, reactive, onMounted } from 'vue'
    import { useRouter, useRoute } from 'vue-router'
    import axios from 'axios'

    const router = useRouter()
    const route = useRoute()
    const API_BASE_URL = 'http://localhost:8080/api'

    const isLoaded = ref(false)

    const form = reactive({
        id: null,
        title: '',
        author: '',
        category: '',
        content: ''
    })

    const fetchPost = async () => {
        try {
            const id = route.params.id
            const response = await axios.get(`${API_BASE_URL}/posts/${id}`)
            
            if (response.data.status === 'success') {
                const post = response.data.data
                form.id = post.id
                form.title = post.title
                form.author = post.author
                form.category = post.category
                form.content = post.content
                isLoaded.value = true
            }
        } catch (error) {
            console.error('게시글 조회 실패:', error)
            alert('게시글을 불러오는데 실패했습니다.')
        }
    }

    const submitPost = async () => {
        try {
            const response = await axios.put(`${API_BASE_URL}/posts/${form.id}`, form)
            
            if (response.data.status === 'success') {
                alert('게시글이 수정되었습니다.')
                router.push(`/posts/${form.id}`)
            }
        } catch (error) {
            console.error('게시글 수정 실패:', error)
            alert('게시글 수정에 실패했습니다.')
        }
    }

    const cancel = () => {
        if (confirm('수정을 취소하시겠습니까?')) {
            router.push(`/posts/${form.id}`)
        }
    }

    onMounted(() => {
        fetchPost()
    })
</script>

<style lang="scss" scoped>

</style>