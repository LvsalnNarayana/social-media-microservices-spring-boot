package com.example.social_media.shared_libs.rabbitMQ;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class NotificationRabbitConfig {

    // ------------------------
    // EXCHANGES
    // ------------------------
    @Bean
    public DirectExchange postExchange() {
        return new DirectExchange(RabbitMQConstants.EXCHANGE_POST, true, false);
    }

    @Bean
    public DirectExchange commentExchange() {
        return new DirectExchange(RabbitMQConstants.EXCHANGE_COMMENT, true, false);
    }

    @Bean
    public DirectExchange replyExchange() {
        return new DirectExchange(RabbitMQConstants.EXCHANGE_REPLY, true, false);
    }

    // ------------------------
    // QUEUES
    // ------------------------
    @Bean
    public Queue postQueue() {
        return QueueBuilder.durable(RabbitMQConstants.QUEUE_POST).build();
    }

    @Bean
    public Queue commentQueue() {
        return QueueBuilder.durable(RabbitMQConstants.QUEUE_COMMENT).build();
    }

    @Bean
    public Queue replyQueue() {
        return QueueBuilder.durable(RabbitMQConstants.QUEUE_REPLY).build();
    }

    // ------------------------
    // BINDINGS
    // ------------------------
    @Bean
    public Binding bindPostQueue(Queue postQueue, DirectExchange postExchange) {
        return BindingBuilder.bind(postQueue)
                .to(postExchange)
                .with(RabbitMQConstants.ROUTING_POST);
    }

    @Bean
    public Binding bindCommentQueue(Queue commentQueue, DirectExchange commentExchange) {
        return BindingBuilder.bind(commentQueue)
                .to(commentExchange)
                .with(RabbitMQConstants.ROUTING_COMMENT);
    }

    @Bean
    public Binding bindReplyQueue(Queue replyQueue, DirectExchange replyExchange) {
        return BindingBuilder.bind(replyQueue)
                .to(replyExchange)
                .with(RabbitMQConstants.ROUTING_REPLY);
    }
}
