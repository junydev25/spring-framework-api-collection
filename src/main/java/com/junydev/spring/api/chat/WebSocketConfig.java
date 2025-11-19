package com.junydev.spring.api.chat;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // /topic으로 시작하는 경로로 들어오는 메시지를 구독하는 클라이언트에게 자동 브로드캐스트(Broker -> Client)
        registry.enableSimpleBroker("/topic");
        // 클라이언트가 서버로 메시지를 보낼 때 사용하는 prefix
        // /app으로 들어오는 메시지는 @MessageMapping 핸들러로 라우팅됨(Broker -> Client)
        registry.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws") // Client가 WebSocket 연결을 시작할 URL
                .setAllowedOriginPatterns("*")
                .withSockJS(); // SocketJS 지원 활성화(WebSocket을 지원하지 않는 브라우저에서도 폴백(ajax, long-polling)으로 도작하게 해줌
    }
}
