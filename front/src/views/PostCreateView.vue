<template>
    <v-app>
        <v-main>
            <v-container class="py-8">
                <v-row justify="center">
                    <v-col cols="12" md="10" lg="8">
                        <!-- 헤더 -->
                        <div class="mb-6">
                            <h1 class="text-h4 font-weight-bold mb-2">✍️ 게시글 작성</h1>
                            <p class="text-grey">새로운 게시글을 작성해보세요</p>
                        </div>

                        <!-- 폼 카드 -->
                        <v-card elevation="4">
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
                                        :rules="[v => !!v || '제목을 입력해주세요']"
                                        class="mb-4"
                                    ></v-text-field>

                                    <!-- 작성자 -->
                                    <v-text-field
                                        v-model="form.author"
                                        label="작성자"
                                        variant="outlined"
                                        required
                                        prepend-inner-icon="mdi-account"
                                        placeholder="작성자 이름을 입력하세요"
                                        :rules="[v => !!v || '작성자를 입력해주세요']"
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
                                        placeholder="카테고리를 선택하세요"
                                        :rules="[v => !!v || '카테고리를 선택해주세요']"
                                        class="mb-4"
                                    ></v-select>

                                    <!-- 내용 -->
                                    <v-textarea
                                        v-model="form.content"
                                        label="내용"
                                        variant="outlined"
                                        required
                                        rows="12"
                                        prepend-inner-icon="mdi-text"
                                        placeholder="내용을 입력하세요"
                                        :rules="[v => !!v || '내용을 입력해주세요']"
                                        class="mb-4"
                                    ></v-textarea>

                                    <!-- 버튼 -->
                                    <div class="d-flex justify-end gap-2">
                                        <v-btn
                                            size="large"
                                            variant="outlined"
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
                                            작성 완료
                                        </v-btn>
                                    </div>
                                </v-form>
                            </v-card-text>
                        </v-card>
                    </v-col>
                </v-row>
            </v-container>
        </v-main>
    </v-app>
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

    const categoryOptions = [
        { title: 'IT', value: 'it' },
        { title: '게임', value: 'game' },
        { title: '스포츠', value: 'sports' }
    ]

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

<style scoped>
    .gap-2 {
        gap: 8px;
    }
</style>