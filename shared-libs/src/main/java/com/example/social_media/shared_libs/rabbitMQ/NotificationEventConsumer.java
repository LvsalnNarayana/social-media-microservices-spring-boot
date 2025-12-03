//package com.example.social_media.shared_libs.rabbitMQ;
//
//import org.springframework.amqp.rabbit.annotation.RabbitListener;
//import org.springframework.stereotype.Service;
//
//@Service
//public class NotificationEventConsumer {
//
////    private final WebSocketNotificationService webSocketService;
////
////    public NotificationEventConsumer(WebSocketNotificationService webSocketService) {
////        this.webSocketService = webSocketService;
////    }
//
//    @RabbitListener(queues = RabbitMQConstants.QUEUE_POST)
//    public void consumePostNotification(String message) {
//        System.out.println("📨 Post Notification → " + message);
////        webSocketService.pushToUsers(message);
//    }
//
//    @RabbitListener(queues = RabbitMQConstants.QUEUE_COMMENT)
//    public void consumeCommentNotification(String message) {
//        System.out.println("📨 Comment Notification → " + message);
////        webSocketService.pushToUsers(message);
//    }
//
//    @RabbitListener(queues = RabbitMQConstants.QUEUE_REPLY)
//    public void consumeReplyNotification(String message) {
//        System.out.println("📨 Reply Notification → " + message);
////        webSocketService.pushToUsers(message);
//    }
//}
