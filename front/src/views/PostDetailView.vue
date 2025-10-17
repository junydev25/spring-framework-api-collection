<template>
    <RouterLink :to="{ name: 'posts' }">
        <button>목록으로</button>
    </RouterLink>

    <div v-if="post">
        <h2>{{ post.title }}</h2>
        <p>카테고리: {{ getCategoryName(post.category) }}</p>
        <p>작성자: {{ post.author }}</p>
        <p>작성일: {{ formatDate(post.createdAt) }}</p>
        <p>수정일: {{ formatDate(post.updatedAt) }}</p>
        <p>조회수: {{ post.statistics.viewCounts }}</p>
        <p>좋아요: {{ post.statistics.likeCounts }}</p>
        
        <hr>
        
        <div>{{ post.content }}</div>
        
        <hr>
        
        <RouterLink :to="{ name: 'postEdit', params: { id: post.id } }">
            <button>수정</button>
        </RouterLink>
        <button @click="deletePost">삭제</button>
        <button @click="updateLikeCounts(post.id)">{{ liked ? "좋아요 취소" : "좋아요" }}</button>
        
        <hr>

        <form ref="commentForm" @submit.prevent="submitPostComment">
            <div>
                <label>작성자:</label>
                <input v-model="form.author" type="text" required />
            </div>

            <div>
                <label>댓글:</label>
                <textarea v-model="form.comment" rows="10" required></textarea>
            </div>

            <div>
                <button type="submit">{{ isEditing ? '수정' : '작성' }}</button>
                <button type="button" @click="cancel">취소</button>
            </div>
        </form>

        <hr>

        <p>댓글 수: {{ post.statistics.commentCounts }}</p>

        <div v-for="(comment, index) in comments" :key="comment.id">
            <span>작성자: {{comment.author}} / </span>
            <span>댓글: {{comment.comment}} / </span>
            <span>작성일: {{ formatDate(comment.createdAt) }} / </span>
            <span>작성일: {{ formatDate(comment.updatedAt) }} / </span>
            <button @click="modifyPostComment(comment, index)">수정</button>
            <button @click="deletePostComment(comment)">제거</button>
        </div>

    </div>

    <div v-else>
        <p>게시글을 불러오는 중...</p>
    </div>
</template>

<script setup>
    import { RouterLink } from 'vue-router';

    import { ref, onMounted, reactive, computed } from 'vue'
    import { useRouter, useRoute } from 'vue-router'
    import axios from 'axios'

    const router = useRouter()
    const route = useRoute()
    const API_BASE_URL = 'http://localhost:8080/api'

    const post = ref(null)
    const comments = ref([])
    const liked = ref(false)
    const commentForm = ref(null)
    const commentIdx = ref(null)
    
    const form = reactive({
        id: null,
        author: '',
        comment: ''
    })

    const isEditing = computed(() => form.id !== null)

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

    const fetchPost = async () => {
        try {
            const id = route.params.id
            const response = await axios.get(`${API_BASE_URL}/posts/${id}`)
            
            if (response.data.status === 'success') {
                post.value = response.data.data
                comments.value = response.data.data.comments.data
                upViewCount(id)
            }
        } catch (error) {
            console.error('게시글 조회 실패:', error)
            alert('게시글을 불러오는데 실패했습니다.')
        }
    }

    const upViewCount = async (id) => {
        try {
            const response = await axios.post(`${API_BASE_URL}/posts/${id}/stat/view`, {}, {
                params: { counts: 1 }
            })

            if (response.data.status === 'success') {
                post.value.statistics.viewCounts += 1
            }
        } catch (error) {
            console.error('View count 업데이트 실패:', error)
            alert('View count를 업데이트 하는 데 실패했습니다.')
        }
    }

    const deletePost = async () => {
        if (!confirm('정말 삭제하시겠습니까?')) return

        try {
            const response = await axios.delete(`${API_BASE_URL}/posts/${route.params.id}`)
            
            if (response.data.status === 'success') {
                alert('게시글이 삭제되었습니다.')
                router.push('/posts')
            }
        } catch (error) {
            console.error('게시글 삭제 실패:', error)
            alert('게시글 삭제에 실패했습니다.')
        }
    }

    const updateLikeCounts = async (id) => {
        const updatedCounts = ref(0);
        if (liked.value) {
            updatedCounts.value = -1
        } else {
            updatedCounts.value = 1
        }

        try {
            const response = await axios.post(`${API_BASE_URL}/posts/${id}/stat/like`, {}, {
                params: { counts: updatedCounts.value }
            })

            if (response.data.status == "success") {
                liked.value = !liked.value
                post.value.statistics.likeCounts += updatedCounts.value
                alert("좋아요 수가 " + post.value.statistics.likeCounts + "로 업데이트 되었습니다.")
            }
        } catch (error) {
            alert("좋아요 수를 업데이트 하는 데 실패했습니다.")
        }
    }

    const submitPostComment = async () => {
        if (isEditing.value) {
            try {
                const response = await axios.patch(`${API_BASE_URL}/posts/${route.params.id}/comment`, form)
                
                if (response.data.status === 'success') {
                    comments.value[commentIdx.value] = response.data.data
                    form.id = null
                    form.author = ''
                    form.comment = ''
                    commentIdx.value = null
                    alert('댓글이 수정되었습니다.')
                    router.push(`/posts/${response.data.data.postId}`)
                }
            } catch (error) {
                console.error('댓글 수정 실패:', error)
                alert('댓글 수정에 실패했습니다.')
            }
        } else {
            try {
                const response = await axios.post(`${API_BASE_URL}/posts/${route.params.id}/comment`, form)
                
                if (response.data.status === 'success') {
                    comments.value.push(response.data.data)
                    form.author = ''
                    form.comment = ''
                    post.value.statistics.commentCounts += 1
                    alert('댓글이 작성되었습니다.')
                    router.push(`/posts/${route.params.id}`)
                }
            } catch (error) {
                console.error('댓글 작성 실패:', error)
                alert('댓글 작성에 실패했습니다.')
            }
        }
    }

    const modifyPostComment = (comment, index) => {
        form.id = comment.id
        form.author = comment.author
        form.comment = comment.comment

        commentIdx.value = index
    
        commentForm.value?.scrollIntoView({ behavior: 'smooth' })
    }

    const deletePostComment = async (comment) => {
        if (!confirm('정말 삭제하시겠습니까?')) return

        try {
            const response = await axios.delete(`${API_BASE_URL}/posts/${route.params.id}/comment/${comment.id}`)
            
            if (response.data.status === 'success') {
                post.value.statistics.commentCounts -= 1
                alert('댓글이 삭제되었습니다.')
                router.push(`/posts/${route.params.id}`)
            }
        } catch (error) {
            console.error('댓글 삭제 실패:', error)
            alert('댓글 삭제에 실패했습니다.')
        }
    }

    onMounted(() => {
        fetchPost()
    })
</script>

<style lang="scss" scoped>

</style>