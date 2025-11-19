<template>
    <v-app>
        <v-main>
            <v-container class="py-8">
                <v-row justify="center">
                    <v-col cols="12" md="10" lg="8">
                        <!-- 로딩 -->
                        <v-card v-if="!post" elevation="4" class="pa-10">
                            <div class="text-center">
                                <v-progress-circular
                                    indeterminate
                                    color="primary"
                                    size="64"
                                    class="mb-4"
                                ></v-progress-circular>
                                <p class="text-h6 text-grey">게시글을 불러오는 중...</p>
                            </div>
                        </v-card>

                        <!-- 게시글 내용 -->
                        <div v-else>
                            <!-- 헤더 -->
                            <div class="d-flex justify-space-between align-center mb-4">
                                <v-btn
                                    variant="text"
                                    prepend-icon="mdi-arrow-left"
                                    :to="{ name: 'posts' }"
                                >
                                    목록으로
                                </v-btn>
                            </div>

                            <!-- 게시글 카드 -->
                            <v-card elevation="4" class="mb-6">
                                <v-card-text class="pa-6">
                                    <!-- 카테고리 -->
                                    <v-chip
                                        :color="getCategoryColor(post.category)"
                                        size="small"
                                        class="mb-4"
                                    >
                                        {{ getCategoryName(post.category) }}
                                    </v-chip>

                                    <!-- 제목 -->
                                    <h1 class="text-h4 font-weight-bold mb-4">
                                        {{ post.title }}
                                    </h1>

                                    <!-- 메타 정보 -->
                                    <div class="d-flex flex-wrap gap-4 mb-4 text-grey">
                                        <div class="d-flex align-center">
                                            <v-icon size="small" class="mr-1">mdi-account</v-icon>
                                            <span>{{ post.author }}</span>
                                        </div>
                                        <div class="d-flex align-center">
                                            <v-icon size="small" class="mr-1">mdi-clock-outline</v-icon>
                                            <span>{{ formatDate(post.createdAt) }}</span>
                                        </div>
                                        <div class="d-flex align-center">
                                            <v-icon size="small" class="mr-1">mdi-eye</v-icon>
                                            <span>{{ post.statistics.viewCounts }}</span>
                                        </div>
                                        <div class="d-flex align-center">
                                            <v-icon size="small" class="mr-1">mdi-heart</v-icon>
                                            <span>{{ post.statistics.likeCounts }}</span>
                                        </div>
                                        <div class="d-flex align-center">
                                            <v-icon size="small" class="mr-1">mdi-comment</v-icon>
                                            <span>{{ post.statistics.commentCounts }}</span>
                                        </div>
                                    </div>

                                    <v-divider class="my-6"></v-divider>

                                    <!-- 내용 -->
                                    <div class="post-content text-body-1">
                                        {{ post.content }}
                                    </div>

                                    <v-divider class="my-6"></v-divider>

                                    <!-- 액션 버튼 -->
                                    <div class="d-flex justify-space-between align-center">
                                        <div class="d-flex gap-2">
                                            <v-btn
                                                color="primary"
                                                variant="outlined"
                                                prepend-icon="mdi-pencil"
                                                :to="{ name: 'postEdit', params: { id: post.id } }"
                                            >
                                                수정
                                            </v-btn>
                                            <v-btn
                                                color="error"
                                                variant="outlined"
                                                prepend-icon="mdi-delete"
                                                @click="deletePost"
                                            >
                                                삭제
                                            </v-btn>
                                        </div>
                                        <v-btn
                                            :color="liked ? 'error' : 'grey'"
                                            :variant="liked ? 'flat' : 'outlined'"
                                            prepend-icon="mdi-heart"
                                            @click="updateLikeCounts(post.id)"
                                        >
                                            {{ liked ? "좋아요 취소" : "좋아요" }}
                                        </v-btn>
                                    </div>
                                </v-card-text>
                            </v-card>

                            <!-- 댓글 작성 폼 -->
                            <v-card elevation="4" class="mb-6" ref="commentForm">
                                <v-card-title class="bg-grey-lighten-4">
                                    <v-icon class="mr-2">mdi-comment-edit</v-icon>
                                    {{ isEditing ? '댓글 수정' : '댓글 작성' }}
                                </v-card-title>
                                <v-card-text class="pa-6">
                                    <v-form @submit.prevent="submitPostComment">
                                        <v-text-field
                                            v-model="form.author"
                                            label="작성자"
                                            variant="outlined"
                                            required
                                            prepend-inner-icon="mdi-account"
                                            class="mb-4"
                                        ></v-text-field>

                                        <v-textarea
                                            v-model="form.comment"
                                            label="댓글"
                                            variant="outlined"
                                            required
                                            rows="4"
                                            prepend-inner-icon="mdi-comment"
                                            class="mb-4"
                                        ></v-textarea>

                                        <div class="d-flex justify-end gap-2">
                                            <v-btn
                                                variant="outlined"
                                                @click="cancel"
                                            >
                                                취소
                                            </v-btn>
                                            <v-btn
                                                type="submit"
                                                color="primary"
                                            >
                                                {{ isEditing ? '수정' : '작성' }}
                                            </v-btn>
                                        </div>
                                    </v-form>
                                </v-card-text>
                            </v-card>

                            <!-- 댓글 목록 -->
                            <v-card elevation="4">
                                <v-card-title class="bg-grey-lighten-4">
                                    <v-icon class="mr-2">mdi-comment-multiple</v-icon>
                                    댓글 {{ post.statistics.commentCounts }}개
                                </v-card-title>
                                <v-card-text class="pa-0">
                                    <v-list>
                                        <template v-for="(comment, index) in comments" :key="comment.id">
                                            <v-list-item class="py-4">
                                                <div class="w-100">
                                                    <!-- 댓글 헤더 -->
                                                    <div class="d-flex justify-space-between align-center mb-2">
                                                        <div class="d-flex align-center">
                                                            <v-avatar color="primary" size="32" class="mr-2">
                                                                <span class="text-white text-body-2">
                                                                    {{ comment.author.charAt(0).toUpperCase() }}
                                                                </span>
                                                            </v-avatar>
                                                            <div>
                                                                <div class="font-weight-bold">{{ comment.author }}</div>
                                                                <div class="text-caption text-grey">
                                                                    {{ formatDate(comment.createdAt) }}
                                                                </div>
                                                            </div>
                                                        </div>
                                                        <div class="d-flex gap-1">
                                                            <v-btn
                                                                size="small"
                                                                variant="text"
                                                                icon="mdi-pencil"
                                                                @click="modifyPostComment(comment, index)"
                                                            ></v-btn>
                                                            <v-btn
                                                                size="small"
                                                                variant="text"
                                                                icon="mdi-delete"
                                                                color="error"
                                                                @click="deletePostComment(comment)"
                                                            ></v-btn>
                                                        </div>
                                                    </div>
                                                    <!-- 댓글 내용 -->
                                                    <div class="text-body-2 ml-10">
                                                        {{ comment.comment }}
                                                    </div>
                                                </div>
                                            </v-list-item>
                                            <v-divider v-if="index < comments.length - 1"></v-divider>
                                        </template>

                                        <!-- 댓글이 없을 때 -->
                                        <v-list-item v-if="comments.length === 0" class="text-center pa-8">
                                            <div class="text-grey">
                                                <v-icon size="48" class="mb-2">mdi-comment-off-outline</v-icon>
                                                <p>첫 댓글을 작성해보세요!</p>
                                            </div>
                                        </v-list-item>
                                    </v-list>
                                </v-card-text>
                            </v-card>
                        </div>
                    </v-col>
                </v-row>
            </v-container>
        </v-main>
    </v-app>
</template>

<script setup>
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

    const getCategoryColor = (category) => {
        const colors = {
            it: 'blue',
            game: 'purple',
            sports: 'green'
        }
        return colors[category] || 'grey'
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
        const updatedCounts = liked.value ? -1 : 1

        try {
            const response = await axios.post(`${API_BASE_URL}/posts/${id}/stat/like`, {}, {
                params: { counts: updatedCounts }
            })

            if (response.data.status === "success") {
                liked.value = !liked.value
                post.value.statistics.likeCounts += updatedCounts
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
                    cancel()
                    alert('댓글이 수정되었습니다.')
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
                    post.value.statistics.commentCounts += 1
                    cancel()
                    alert('댓글이 작성되었습니다.')
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
        commentForm.value?.$el.scrollIntoView({ behavior: 'smooth' })
    }

    const deletePostComment = async (comment) => {
        if (!confirm('정말 삭제하시겠습니까?')) return

        try {
            const response = await axios.delete(`${API_BASE_URL}/posts/${route.params.id}/comment/${comment.id}`)
            
            if (response.data.status === 'success') {
                const index = comments.value.findIndex(c => c.id === comment.id)
                if (index > -1) {
                    comments.value.splice(index, 1)
                }
                post.value.statistics.commentCounts -= 1
                alert('댓글이 삭제되었습니다.')
            }
        } catch (error) {
            console.error('댓글 삭제 실패:', error)
            alert('댓글 삭제에 실패했습니다.')
        }
    }

    const cancel = () => {
        form.id = null
        form.author = ''
        form.comment = ''
        commentIdx.value = null
    }

    onMounted(() => {
        fetchPost()
    })
</script>

<style scoped>
    .gap-2 {
        gap: 8px;
    }

    .gap-4 {
        gap: 16px;
    }

    .post-content {
        white-space: pre-wrap;
        word-break: break-word;
        line-height: 1.8;
    }
</style>