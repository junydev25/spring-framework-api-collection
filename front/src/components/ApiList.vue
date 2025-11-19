<template>
    <v-app>
        <v-main>
            <v-container class="fill-height">
                <v-row align="center" justify="center">
                    <v-col cols="12">
                        <!-- 헤더 -->
                        <div class="text-center mb-10">
                            <h1 class="text-h3 font-weight-bold mb-4">
                                🚀 Welcome
                            </h1>
                            <p class="text-h6 text-grey">
                                원하는 메뉴를 선택하세요
                            </p>
                        </div>

                        <!-- API 카드 목록 -->
                        <v-row justify="center">
                            <v-col
                                v-for="api in apiList"
                                :key="api.path"
                                cols="12"
                                sm="6"
                                md="4"
                                lg="3"
                            >
                                <v-card
                                class="menu-card"
                                elevation="4"
                                hover
                                @click="move(api.path)"
                                >
                                    <v-card-text class="text-center pa-8">
                                        <v-icon
                                        :icon="api.icon"
                                        :color="api.color"
                                        size="80"
                                        class="mb-4"
                                        ></v-icon>
                                        
                                        <h2 class="text-h5 font-weight-bold mb-2">
                                        {{ api.name }}
                                        </h2>
                                        
                                        <p class="text-body-2 text-grey">
                                        {{ api.description }}
                                        </p>
                                    </v-card-text>

                                    <v-card-actions class="justify-center pb-4">
                                        <v-btn
                                        :color="api.color"
                                        variant="outlined"
                                        prepend-icon="mdi-arrow-right"
                                        >
                                        이동하기
                                        </v-btn>
                                    </v-card-actions>
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
import { ref } from 'vue'
import { useRouter } from 'vue-router'

const apiList = ref([
    {
        name: "게시판",
        path: "/posts",
        icon: "mdi-post-outline",
        color: "blue",
        description: "게시글을 작성하고 관리하세요"
    },
    {
        name: "채팅",
        path: "/chat",
        icon: "mdi-chat-outline",
        color: "green",
        description: "실시간으로 대화를 나누세요"
    }
])

const router = useRouter()

const move = (path) => {
    router.push(path)
}
</script>

<style scoped>
.fill-height {
    min-height: 100vh;
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.menu-card {
    cursor: pointer;
    transition: all 0.3s ease;
    border-radius: 16px !important;
}

.menu-card:hover {
    transform: translateY(-8px);
    box-shadow: 0 12px 24px rgba(0, 0, 0, 0.15) !important;
}
</style>