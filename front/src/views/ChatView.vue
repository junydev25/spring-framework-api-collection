<template>
    <v-app>
        <!-- 로그인 화면 -->
        <v-container v-if="!connected" class="fill-height">
            <v-row align="center" justify="center">
                <v-col cols="12" sm="8" md="4">
                    <v-card class="pa-4" elevation="8">
                        <v-card-title class="text-h4 text-center mb-4">
                            💬 Chat App
                        </v-card-title>
                        <v-card-text>
                            <v-text-field
                                v-model="username"
                                label="닉네임을 입력하세요"
                                prepend-inner-icon="mdi-account"
                                variant="outlined"
                                @keyup.enter="connect"
                                autofocus
                            ></v-text-field>
                        </v-card-text>
                        <v-card-actions>
                            <v-btn
                                block
                                color="primary"
                                size="large"
                                @click="connect"
                            >
                                입장하기
                            </v-btn>
                        </v-card-actions>
                    </v-card>
                </v-col>
            </v-row>
        </v-container>

        <!-- 채팅 화면 (전체 화면) -->
        <div v-else class="chat-wrapper">
            <v-card class="chat-card" flat>
                <!-- 헤더 -->
                <v-app-bar color="primary" dark elevation="2">
                    <v-app-bar-title>
                        <v-icon class="mr-2">mdi-forum</v-icon>
                        채팅방
                    </v-app-bar-title>
                    <v-spacer></v-spacer>
                    <v-chip class="mr-2" color="white" text-color="primary">
                        {{ username }}
                    </v-chip>
                    <v-btn icon @click="disconnect">
                        <v-icon>mdi-logout</v-icon>
                    </v-btn>
                </v-app-bar>

                <!-- 메시지 영역 -->
                <v-card-text
                ref="messageArea"
                class="message-container"
                >
                    <div v-for="(msg, index) in messages" :key="index" class="mb-3">
                        <!-- 시스템 메시지 (입장/퇴장) -->
                        <v-row v-if="msg.type !== 'CHAT'" justify="center">
                            <v-col cols="auto">
                                <v-chip size="small" color="grey-lighten-2">
                                    <v-icon size="small" class="mr-1">
                                        {{ msg.type === 'JOIN' ? 'mdi-login' : 'mdi-logout' }}
                                    </v-icon>
                                    {{ msg.sender }} 님이 {{ msg.type === 'JOIN' ? '입장' : '퇴장' }}했습니다.
                                </v-chip>
                            </v-col>
                        </v-row>

                        <!-- 채팅 메시지 -->
                        <v-row v-else :justify="isMyMessage(msg) ? 'end' : 'start'" no-gutters>
                            <v-col cols="auto" style="max-width: 70%;">
                                <div :class="['d-flex', isMyMessage(msg) ? 'flex-row-reverse' : 'flex-row']">
                                <!-- 아바타 (내 메시지가 아닐 때만) -->
                                    <v-avatar
                                        v-if="!isMyMessage(msg)"
                                        :color="getColorBySender(msg.sender)"
                                        size="40"
                                        class="mr-2"
                                    >
                                        <span class="text-h6 text-white">
                                            {{ msg.sender.charAt(0).toUpperCase() }}
                                        </span>
                                    </v-avatar>

                                    <!-- 메시지 내용 -->
                                    <div>
                                        <!-- 발신자 이름 (내 메시지가 아닐 때만) -->
                                        <div
                                            v-if="!isMyMessage(msg)"
                                            class="text-caption text-grey-darken-1 mb-1 ml-1"
                                            >
                                            {{ msg.sender }}
                                        </div>

                                        <!-- 메시지 버블 -->
                                        <v-card
                                            :color="isMyMessage(msg) ? 'primary' : 'white'"
                                            :class="[
                                                'pa-3',
                                                isMyMessage(msg) ? 'text-white' : 'text-black'
                                            ]"
                                            elevation="1"
                                            :style="{
                                                borderRadius: isMyMessage(msg)
                                                ? '18px 18px 4px 18px'
                                                : '18px 18px 18px 4px'
                                            }"
                                        >
                                            <div class="text-body-1" style="word-break: break-word;">
                                                {{ msg.content }}
                                            </div>
                                        </v-card>

                                        <!-- 시간 표시 -->
                                        <div
                                            :class="[
                                                'text-caption text-grey mt-1',
                                                isMyMessage(msg) ? 'text-right mr-1' : 'ml-1'
                                            ]"
                                            >
                                            {{ formatTime(msg.timestamp) }}
                                        </div>
                                    </div>
                                </div>
                            </v-col>
                        </v-row>
                    </div>
                </v-card-text>

                <!-- 입력 영역 -->
                <v-card-actions class="input-container">
                <v-text-field
                    v-model="messageInput"
                    placeholder="메시지를 입력하세요..."
                    variant="outlined"
                    density="comfortable"
                    hide-details
                    @keyup.enter="sendMessage"
                    append-inner-icon="mdi-send"
                    @click:append-inner="sendMessage"
                >
                </v-text-field>
                </v-card-actions>
            </v-card>
        </div>
    </v-app>
</template>

<script setup>
    import { ref, nextTick } from 'vue'
    import { Client } from '@stomp/stompjs'

    const username = ref('')
    const messageInput = ref('')
    const messages = ref([])
    const connected = ref(false)
    const messageArea = ref(null)
    let stompClient = null

    // 내 메시지인지 확인
    const isMyMessage = (msg) => {
        return msg.sender === username.value
    }

    // 발신자별 색상 생성 (해시 기반)
    const getColorBySender = (sender) => {
        const colors = [
            'red', 'pink', 'purple', 'deep-purple', 'indigo',
            'blue', 'cyan', 'teal', 'green', 'light-green',
            'orange', 'deep-orange', 'brown', 'blue-grey'
        ]
        const hash = sender.split('').reduce((acc, char) => {
            return char.charCodeAt(0) + ((acc << 5) - acc)
        }, 0)
        return colors[Math.abs(hash) % colors.length]
    }

    // 시간 포맷팅
    const formatTime = (timestamp) => {
        if (!timestamp) return ''
        const date = new Date(timestamp)
        return date.toLocaleTimeString('ko-KR', {
            hour: '2-digit',
            minute: '2-digit'
        })
    }

    const connect = () => {
        if (!username.value.trim()) {
            alert('닉네임을 입력하세요!!')
            return
        }

        const socket = new WebSocket('ws://localhost:8080/ws/websocket')
        stompClient = new Client({
            reconnectDelay: 5000,
            webSocketFactory: () => socket,
            onConnect: (frame) => {
                connected.value = true

                // 구독
                stompClient.subscribe('/topic/room/1', (message) => {
                    const chatMessage = JSON.parse(message.body)
                    chatMessage.timestamp = new Date().toISOString()
                    messages.value.push(chatMessage)
                    scrollToBottom()
                })

                // 입장 메시지 전송
                stompClient.publish({
                    destination: '/app/chat.addUser/1',
                    body: JSON.stringify({
                    sender: username.value,
                    type: 'JOIN'
                    })
                })
            },
            onStompError: (frame) => {
                console.error('STOMP error', frame)
            }
        })

        stompClient.activate()
    }

    const disconnect = () => {
        if (stompClient) {
            stompClient.deactivate()
        }
        connected.value = false
        messages.value = []
        username.value = ''
    }

    const sendMessage = () => {
        if (!messageInput.value.trim()) return

        const chatMessage = {
            sender: username.value,
            content: messageInput.value,
            type: 'CHAT'
        }

        stompClient.publish({
            destination: '/app/chat.sendMessage/1',
            body: JSON.stringify(chatMessage)
        })

        messageInput.value = ''
    }

    const scrollToBottom = () => {
        nextTick(() => {
            if (messageArea.value) {
                messageArea.value.scrollTop = messageArea.value.scrollHeight
            }
        })
    }
</script>

<style scoped>
    /* 전체 화면 설정 */
    .chat-wrapper {
        position: fixed;
        top: 0;
        left: 0;
        right: 0;
        bottom: 0;
        width: 100vw;
        height: 100vh;
        overflow: hidden;
    }

    .chat-card {
        width: 100%;
        height: 100%;
        display: flex;
        flex-direction: column;
    }

    /* 메시지 영역 */
    .message-container {
        flex: 1;
        overflow-y: auto;
        padding: 20px;
        background-color: #f5f5f5;
        height: calc(100vh - 64px - 80px); /* 100vh - 헤더 - 입력창 */
    }

    /* 입력 영역 */
    .input-container {
        padding: 16px;
        background-color: white;
        border-top: 1px solid #e0e0e0;
        flex-shrink: 0;
    }

    /* 로그인 화면 전체 높이 */
    .fill-height {
        height: 100vh;
    }

    /* 스크롤바 스타일링 */
    ::-webkit-scrollbar {
        width: 8px;
    }

    ::-webkit-scrollbar-track {
        background: #f1f1f1;
    }

    ::-webkit-scrollbar-thumb {
        background: #888;
        border-radius: 4px;
    }

    ::-webkit-scrollbar-thumb:hover {
        background: #555;
    }
</style>