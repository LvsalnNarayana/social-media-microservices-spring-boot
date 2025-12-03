package com.example.social_media.shared_libs.configuration;

import com.example.social_media.shared_libs.rabbitMQ.RabbitMQConstants;
import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class NotificationRabbitDeclarations {

    @Bean
    public Declarables notificationDeclarables() {

        DirectExchange postExchange = new DirectExchange(
                RabbitMQConstants.EXCHANGE_POST, true, false);

        DirectExchange commentExchange = new DirectExchange(
                RabbitMQConstants.EXCHANGE_COMMENT, true, false);

        DirectExchange replyExchange = new DirectExchange(
                RabbitMQConstants.EXCHANGE_REPLY, true, false);

        Queue postQueue = QueueBuilder.durable(RabbitMQConstants.QUEUE_POST).build();
        Queue commentQueue = QueueBuilder.durable(RabbitMQConstants.QUEUE_COMMENT).build();
        Queue replyQueue = QueueBuilder.durable(RabbitMQConstants.QUEUE_REPLY).build();

        Binding postBinding = BindingBuilder.bind(postQueue)
                .to(postExchange)
                .with(RabbitMQConstants.ROUTING_POST);

        Binding commentBinding = BindingBuilder.bind(commentQueue)
                .to(commentExchange)
                .with(RabbitMQConstants.ROUTING_COMMENT);

        Binding replyBinding = BindingBuilder.bind(replyQueue)
                .to(replyExchange)
                .with(RabbitMQConstants.ROUTING_REPLY);

        return new Declarables(
                postExchange, commentExchange, replyExchange,
                postQueue, commentQueue, replyQueue,
                postBinding, commentBinding, replyBinding
        );
    }
}
