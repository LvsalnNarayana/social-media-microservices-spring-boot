package com.example.social_media.shared_libs.rabbitMQ;

public final class RabbitMQConstants {

    // Exchanges
    public static final String EXCHANGE_POST = "post.notifications.exchange";
    public static final String EXCHANGE_COMMENT = "comment.notifications.exchange";
    public static final String EXCHANGE_REPLY = "reply.notifications.exchange";

    // Routing Keys
    public static final String ROUTING_POST = "post.notifications";
    public static final String ROUTING_COMMENT = "comment.notifications";
    public static final String ROUTING_REPLY = "reply.notifications";

    // Queues
    public static final String QUEUE_POST = "post.notifications.queue";
    public static final String QUEUE_COMMENT = "comment.notifications.queue";
    public static final String QUEUE_REPLY = "reply.notifications.queue";

    private RabbitMQConstants() {
    }
}
