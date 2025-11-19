<template>
    <v-app>
        <v-main>
        <v-container class="py-8">
            <!-- 헤더 -->
            <v-row class="mb-6">
                <v-col cols="12">
                    <div class="d-flex justify-space-between align-center">
                        <div>
                            <h1 class="text-h4 font-weight-bold">📋 게시판</h1>
                            <p class="text-grey mt-2">
                            총 <span class="text-primary font-weight-bold">{{ totalCount }}</span>개의 게시글
                            </p>
                        </div>
                        <v-btn
                            color="primary"
                            size="large"
                            prepend-icon="mdi-pencil"
                            :to="{ name: 'postCreate' }"
                        >
                            새 게시글 작성
                        </v-btn>
                    </div>
                </v-col>
            </v-row>

            <!-- 필터 -->
            <v-card class="mb-6" elevation="2">
                <v-card-text>
                    <v-row>
                        <v-col cols="12" sm="4">
                            <v-select
                            v-model="filters.category"
                            :items="categoryOptions"
                            label="카테고리"
                            variant="outlined"
                            density="comfortable"
                            hide-details
                            clearable
                            ></v-select>
                        </v-col>
                    
                        <v-col cols="12" sm="6">
                            <v-text-field
                            v-model="filters.author"
                            label="작성자 검색"
                            variant="outlined"
                            density="comfortable"
                            hide-details
                            prepend-inner-icon="mdi-account-search"
                            clearable
                            ></v-text-field>
                        </v-col>
                    
                        <v-col cols="12" sm="2">
                            <v-btn
                            block
                            color="primary"
                            size="large"
                            @click="fetchPosts"
                            prepend-icon="mdi-magnify"
                            >
                            검색
                            </v-btn>
                        </v-col>
                    </v-row>
                </v-card-text>
            </v-card>

            <!-- 게시글 목록 -->
            <v-row>
                <v-col cols="12">
                    <v-card
                    v-for="post in posts"
                    :key="post.id"
                    class="mb-4 post-card"
                    elevation="2"
                    hover
                    >
                    <v-card-text>
                        <v-row align="center">
                        <!-- 게시글 정보 -->
                            <v-col cols="12" md="8">
                                <div class="d-flex align-center mb-2">
                                <v-chip
                                    :color="getCategoryColor(post.category)"
                                    size="small"
                                    class="mr-2"
                                >
                                    {{ getCategoryName(post.category) }}
                                </v-chip>
                                
                                <h3 class="text-h6 font-weight-bold">
                                    {{ post.title }}
                                </h3>
                                </div>

                                <div class="d-flex align-center text-grey">
                                <v-icon size="small" class="mr-1">mdi-account</v-icon>
                                <span class="mr-4">{{ post.author }}</span>
                                
                                <v-icon size="small" class="mr-1">mdi-clock-outline</v-icon>
                                <span>{{ formatDate(post.createdAt) }}</span>
                                </div>
                            </v-col>

                            <!-- 버튼 -->
                            <v-col cols="12" md="4" class="text-right">
                                <v-btn
                                color="primary"
                                variant="outlined"
                                :to="{ name: 'postDetail', params: { id: post.id } }"
                                append-icon="mdi-arrow-right"
                                >
                                자세히 보기
                                </v-btn>
                            </v-col>
                        </v-row>
                    </v-card-text>
                    </v-card>

                    <!-- 게시글이 없을 때 -->
                    <v-card v-if="posts.length === 0" elevation="0" class="text-center pa-10">
                    <v-icon size="80" color="grey-lighten-1" class="mb-4">
                        mdi-text-box-remove-outline
                    </v-icon>
                    <h3 class="text-h6 text-grey">게시글이 없습니다</h3>
                    <p class="text-grey-darken-1 mt-2">첫 번째 게시글을 작성해보세요!</p>
                    </v-card>
                </v-col>
            </v-row>
        </v-container>
        </v-main>
    </v-app>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'

const router = useRouter()
const API_BASE_URL = "http://localhost:8080/api"

const posts = ref([])
const totalCount = ref(0)

const filters = reactive({
    category: "",
    author: ""
})

const categoryOptions = [
    { title: '전체 카테고리', value: '' },
    { title: 'IT', value: 'IT' },
    { title: '게임', value: 'Game' },
    { title: '스포츠', value: 'Sports' }
]

const categoryNames = {
    it: "IT",
    game: "게임",
    sports: "스포츠",
    IT: "IT",
    Game: "게임",
    Sports: "스포츠"
}

const getCategoryName = (category) => {
    return categoryNames[category] || category
}

const getCategoryColor = (category) => {
    const colors = {
        it: 'blue',
        IT: 'blue',
        game: 'purple',
        Game: 'purple',
        sports: 'green',
        Sports: 'green'
    }
    return colors[category] || 'grey'
}

const formatDate = (dateString) => {
    if (!dateString) return ''
    const date = new Date(dateString)
    return date.toLocaleString('ko-KR', {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit',
        hour: '2-digit',
        minute: '2-digit'
    })
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

<style scoped>
.post-card {
    transition: all 0.3s ease;
    cursor: pointer;
}

.post-card:hover {
    transform: translateY(-4px);
    box-shadow: 0 8px 16px rgba(0, 0, 0, 0.1) !important;
}
</style>