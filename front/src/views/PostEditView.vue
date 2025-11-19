<template>
    <v-app>
        <v-main>
            <v-container class="py-8">
                <v-row justify="center">
                    <v-col cols="12" lg="10">
                        <!-- 헤더 -->
                        <div class="mb-6">
                            <h1 class="text-h4 font-weight-bold mb-2">📝 게시글 수정</h1>
                            <p class="text-grey">게시글 내용을 수정해보세요</p>
                        </div>

                        <!-- 로딩 -->
                        <v-card v-if="!isLoaded" elevation="4" class="pa-10">
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

                        <v-row v-else>
                            <!-- 폼 영역 -->
                            <v-col cols="12" md="7">
                                <v-card elevation="4">
                                    <v-card-title class="bg-primary text-white">
                                        <v-icon class="mr-2">mdi-pencil</v-icon>
                                        수정하기
                                    </v-card-title>
                                    <v-card-text class="pa-6">
                                        <v-form @submit.prevent="submitPost">
                                            <!-- 제목 -->
                                            <v-text-field
                                                v-model="form.title"
                                                label="제목"
                                                variant="outlined"
                                                required
                                                prepend-inner-icon="mdi-format-title"
                                                placeholder="제목을 입력하세요"
                                                class="mb-4"
                                            ></v-text-field>

                                            <!-- 작성자 (수정 불가) -->
                                            <v-text-field
                                                v-model="form.author"
                                                label="작성자"
                                                variant="outlined"
                                                disabled
                                                prepend-inner-icon="mdi-account"
                                                hint="작성자는 수정할 수 없습니다"
                                                persistent-hint
                                                class="mb-4"
                                            ></v-text-field>

                                            <!-- 카테고리 -->
                                            <v-select
                                                v-model="form.category"
                                                :items="categoryOptions"
                                                label="카테고리"
                                                variant="outlined"
                                                required
                                                prepend-inner-icon="mdi-tag"
                                                class="mb-4"
                                            >
                                                <template v-slot:selection="{ item }">
                                                    <v-chip :color="getCategoryColor(item.value)" size="small">
                                                        {{ item.title }}
                                                    </v-chip>
                                                </template>
                                            </v-select>

                                            <!-- 내용 -->
                                            <v-textarea
                                                v-model="form.content"
                                                label="내용"
                                                variant="outlined"
                                                required
                                                rows="15"
                                                prepend-inner-icon="mdi-text"
                                                placeholder="내용을 입력하세요"
                                                class="mb-4"
                                            ></v-textarea>

                                            <!-- 버튼 -->
                                            <div class="d-flex justify-end gap-2">
                                                <v-btn
                                                    size="large"
                                                    variant="outlined"
                                                    prepend-icon="mdi-close"
                                                    @click="cancel"
                                                >
                                                    취소
                                                </v-btn>
                                                <v-btn
                                                    type="submit"
                                                    size="large"
                                                    color="primary"
                                                    prepend-icon="mdi-check"
                                                >
                                                    수정 완료
                                                </v-btn>
                                            </div>
                                        </v-form>
                                    </v-card-text>
                                </v-card>
                            </v-col>

                            <!-- 미리보기 영역 -->
                            <v-col cols="12" md="5">
                                <v-card elevation="4" class="sticky-preview">
                                    <v-card-title class="bg-grey-lighten-3">
                                        <v-icon class="mr-2">mdi-eye</v-icon>
                                        미리보기
                                    </v-card-title>
                                    <v-card-text class="pa-6">
                                        <!-- 제목 -->
                                        <div class="mb-4">
                                            <h2 class="text-h5 font-weight-bold">{{ form.title }}</h2>
                                        </div>

                                        <!-- 메타 정보 -->
                                        <div class="d-flex align-center mb-4 text-grey">
                                            <v-chip
                                                :color="getCategoryColor(form.category)"
                                                size="small"
                                                class="mr-2"
                                            >
                                                {{ getCategoryName(form.category) }}
                                            </v-chip>
                                            <span>
                                                <v-icon size="small">mdi-account</v-icon>
                                                {{ form.author }}
                                            </span>
                                        </div>

                                        <v-divider class="mb-4"></v-divider>

                                        <!-- 내용 -->
                                        <div class="preview-content">
                                            {{ form.content }}
                                        </div>
                                    </v-card-text>
                                </v-card>
                            </v-col>
                        </v-row>
                    </v-col>
                </v-row>
            </v-container>
        </v-main>
    </v-app>
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

    const categoryOptions = [
        { title: 'IT', value: 'it' },
        { title: '게임', value: 'game' },
        { title: '스포츠', value: 'sports' }
    ]

    const getCategoryName = (category) => {
        const names = {
            it: 'IT',
            game: '게임',
            sports: '스포츠'
        }
        return names[category] || category
    }

    const getCategoryColor = (category) => {
        const colors = {
            it: 'blue',
            game: 'purple',
            sports: 'green'
        }
        return colors[category] || 'grey'
    }

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
            router.push('/posts')
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

<style scoped>
    .gap-2 {
        gap: 8px;
    }

    .sticky-preview {
        position: sticky;
        top: 20px;
    }

    .preview-content {
        white-space: pre-wrap;
        word-break: break-word;
        line-height: 1.8;
    }
</style>