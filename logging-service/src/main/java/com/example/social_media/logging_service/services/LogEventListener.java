package com.example.social_media.logging_service.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.TopicPartition;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class LogEventListener {

    private final LogStorageService storageService;

    public LogEventListener(LogStorageService storageService) {
        this.storageService = storageService;
    }


    // -------------------------------------------------------------------------
    // 1️⃣ LISTEN TO POST TOPICS
    // -------------------------------------------------------------------------
    @KafkaListener(
            topicPartitions = @TopicPartition(
                    topic = "${logging.post.topic-strings}",
                    partitions = {"0"}
            ),
            groupId = "logging-post",
            containerFactory = "kafkaStringListenerContainerFactory"
    )
    public void listenPostTopicStringPartition0(String message) {
        System.out.println("🟦 Partition 0 → " + message);
    }

    @KafkaListener(
            topicPartitions = @TopicPartition(
                    topic = "${logging.post.topic-json}",
                    partitions = {"0"}
            ),
            groupId = "logging-post",
            containerFactory = "kafkaStringListenerContainerFactory"
    )
    public void listenPostTopicJsonPartition0(String message) {
        System.out.println("🟦 Partition 0 → " + message);
    }

    // -------------------------------------------------------------------------
    // 1️⃣ LISTEN TO POST TOPICS ERRORS
    // -------------------------------------------------------------------------
    @KafkaListener(
            topicPartitions = @TopicPartition(
                    topic = "${logging.post.error.topic-strings}",
                    partitions = {"0"}
            ),
            groupId = "logging-post",
            containerFactory = "kafkaStringListenerContainerFactory"
    )
    public void listenPostErrorTopicStringPartition0(String message) {
        System.out.println("🟦 Partition 0 → " + message);
    }

    @KafkaListener(
            topicPartitions = @TopicPartition(
                    topic = "${logging.post.error.topic-json}",
                    partitions = {"0"}
            ),
            groupId = "logging-post",
            containerFactory = "kafkaStringListenerContainerFactory"
    )
    public void listenPostErrorTopicJsonPartition0(String message) {
        System.out.println("🟦 Partition 0 → " + message);
    }

    // -------------------------------------------------------------------------
    // 2️⃣ LISTEN TO COMMENT TOPICS
    // -------------------------------------------------------------------------
    @KafkaListener(
            topicPartitions = @TopicPartition(
                    topic = "${logging.comment.topic-strings}",
                    partitions = {"0"}
            ),
            groupId = "logging-comment",
            containerFactory = "kafkaStringListenerContainerFactory"
    )
    public void listenCommentTopicStringPartition0(String message) {
        System.out.println("🟨 Comment Partition 0 → " + message);
    }

    @KafkaListener(
            topicPartitions = @TopicPartition(
                    topic = "${logging.comment.topic-json}",
                    partitions = {"0"}
            ),
            groupId = "logging-comment",
            containerFactory = "kafkaStringListenerContainerFactory"
    )
    public void listenCommentTopicJsonPartition0(String message) {
        System.out.println("🟨 Comment Partition 0 → " + message);
    }

    // -------------------------------------------------------------------------
    // 2️⃣ LISTEN TO COMMENT TOPICS ERRORS
    // -------------------------------------------------------------------------
    @KafkaListener(
            topicPartitions = @TopicPartition(
                    topic = "${logging.comment.error.topic-strings}",
                    partitions = {"0"}
            ),
            groupId = "logging-comment",
            containerFactory = "kafkaStringListenerContainerFactory"
    )
    public void listenCommentErrorTopicStringPartition0(String message) {
        System.out.println("🟨 Comment ERROR Partition 0 → " + message);
    }

    @KafkaListener(
            topicPartitions = @TopicPartition(
                    topic = "${logging.comment.error.topic-json}",
                    partitions = {"0"}
            ),
            groupId = "logging-comment",
            containerFactory = "kafkaStringListenerContainerFactory"
    )
    public void listenCommentErrorTopicJsonPartition0(String message) {
        System.out.println("🟨 Comment ERROR Partition 0 → " + message);
    }

    // -------------------------------------------------------------------------
    // 3️⃣ LISTEN TO REPLY TOPICS
    // -------------------------------------------------------------------------
    @KafkaListener(
            topicPartitions = @TopicPartition(
                    topic = "${logging.reply.topic-strings}",
                    partitions = {"0"}
            ),
            groupId = "logging-reply",
            containerFactory = "kafkaStringListenerContainerFactory"
    )
    public void listenReplyTopicStringPartition0(String message) {
        System.out.println("🟪 Reply Partition 0 → " + message);
    }

    @KafkaListener(
            topicPartitions = @TopicPartition(
                    topic = "${logging.reply.topic-json}",
                    partitions = {"0"}
            ),
            groupId = "logging-reply",
            containerFactory = "kafkaStringListenerContainerFactory"
    )
    public void listenReplyTopicJsonPartition0(String message) {
        System.out.println("🟪 Reply Partition 0 → " + message);
    }

    // -------------------------------------------------------------------------
    // 3️⃣ LISTEN TO REPLY TOPICS ERRORS
    // -------------------------------------------------------------------------
    @KafkaListener(
            topicPartitions = @TopicPartition(
                    topic = "${logging.reply.error.topic-strings}",
                    partitions = {"0"}
            ),
            groupId = "logging-reply",
            containerFactory = "kafkaStringListenerContainerFactory"
    )
    public void listenReplyErrorTopicStringPartition0(String message) {
        System.out.println("🟪 Reply ERROR Partition 0 → " + message);
    }

    @KafkaListener(
            topicPartitions = @TopicPartition(
                    topic = "${logging.reply.error.topic-json}",
                    partitions = {"0"}
            ),
            groupId = "logging-reply",
            containerFactory = "kafkaStringListenerContainerFactory"
    )
    public void listenReplyErrorTopicJsonPartition0(String message) {
        System.out.println("🟪 Reply ERROR Partition 0 → " + message);
    }

    // -------------------------------------------------------------------------
    // 2️⃣ LISTEN TO RAW STRINGS FROM ALL PARTITIONS
    // -------------------------------------------------------------------------
//    @KafkaListener(
//            topics = "${logging.topic}",
//            groupId = "logging-string-all",
//            containerFactory = "kafkaStringListenerContainerFactory"
//    )
//    public void listenAllRaw(String message) {
//        System.out.println("🟩 RAW Log → " + message);
//    }

    // -------------------------------------------------------------------------
    // 3️⃣ LISTEN TO STRUCTURED JSON LogEvent
    // -------------------------------------------------------------------------
//    @KafkaListener(
//            topicPartitions = @TopicPartition(
//                    topic = "${logging.post.topic-json}",
//                    partitions = {"0"}
//            ),
//            groupId = "logging-event-json",
//            containerFactory = "kafkaEventListenerContainerFactory"
//    )
//    public void listenJson(LogEvent event) {
//        System.out.println("🟧 JSON LogEvent → " + event);
//
//        // storageService.storeEvent(event);
//    }

    // -------------------------------------------------------------------------
    // 4️⃣ LISTEN ONLY TO ERROR MESSAGES (STRING FILTERING)
    // -------------------------------------------------------------------------
//    @KafkaListener(
//            topics = "${logging.topic}",
//            groupId = "logging-errors",
//            containerFactory = "kafkaStringListenerContainerFactory",
//            properties = {"value.deserializer=org.apache.kafka.common.serialization.StringDeserializer"}
//    )
//    public void listenErrors(String message) {
//        if (message.contains("[ERROR]")) {
//            System.out.println("🟥 ERROR LOG → " + message);
//        }
//    }

    // -------------------------------------------------------------------------
    // 5️⃣ LISTEN WITH ANOTHER GROUP (FAN-OUT)
    // -------------------------------------------------------------------------
//    @KafkaListener(
//            topics = "${logging.topic}",
//            groupId = "logging-analytics",
//            containerFactory = "kafkaStringListenerContainerFactory"
//    )
//    public void listenForAnalytics(String message) {
//        System.out.println("📊 Analytics Consumer → " + message);
//    }
}
