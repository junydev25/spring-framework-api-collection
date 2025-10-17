<template>
    <h2>게시글 작성</h2>
    <div>
        <form @submit.prevent="submitPost">
            <div>
                <label>제목:</label>
                <input v-model="form.title" type="text" required />
            </div>

            <div>
                <label>작성자:</label>
                <input v-model="form.author" type="text" required />
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
                <button type="submit">작성</button>
                <button type="button" @click="cancel">취소</button>
            </div>
        </form>
    </div>
</template>

<script setup>
    import { reactive } from 'vue'
    import { useRouter } from 'vue-router'
    import axios from 'axios'

    const router = useRouter()
    const API_BASE_URL = 'http://localhost:8080/api'

    const form = reactive({
        title: '',
        author: '',
        category: '',
        content: ''
    })

    const submitPost = async () => {
        try {
            const response = await axios.post(`${API_BASE_URL}/posts`, form)
            
            if (response.data.status === 'success') {
                alert('게시글이 작성되었습니다.')
                router.push(`/posts/${response.data.data.id}`)
            }
        } catch (error) {
            console.error('게시글 작성 실패:', error)
            alert('게시글 작성에 실패했습니다.')
        }
    }

    const cancel = () => {
        if (confirm('작성을 취소하시겠습니까?')) {
            router.push('/posts')
        }
    }
</script>

<style lang="scss" scoped>

</style>