package com.junydev.spring.api.board;

import com.junydev.spring.api.board.internal.dto.StatType;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqConfig {

    @Bean
    Exchange directExchange() {
        return new DirectExchange("post.stats.exchange");
    }

    @Bean
    Queue postStatViewQueue() {
        return new Queue("post.stats.view", false, false, true);
    }

    @Bean
    Queue postStatLikeQueue() {
        return new Queue("post.stats.like", false, false, true);
    }

    @Bean
    Queue postStatCommentQueue() {
        return new Queue("post.stats.comment", false, false, true);
    }

    @Bean
    Binding postStatViewBinding() {
        return BindingBuilder.bind(postStatViewQueue()).to(directExchange()).with(StatType.VIEW).noargs();
    }

    @Bean
    Binding postStatLikeBinding() {
        return BindingBuilder.bind(postStatLikeQueue()).to(directExchange()).with(StatType.LIKE).noargs();
    }

    @Bean
    Binding postStatCommentBinding() {
        return BindingBuilder.bind(postStatCommentQueue()).to(directExchange()).with(StatType.COMMENT).noargs();
    }

    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter());
        return template;
    }
}
