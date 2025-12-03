package com.example.social_media.shared_libs.rabbitMQ;

import com.example.social_media.shared_libs.utils.ExtendedCorrelationData;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class NotificationEventProducer {

    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;

    /**
     * Setup publisher confirms and returns on the injected RabbitTemplate.
     */
    @PostConstruct
    public void setupCallbacks() {

        // Publisher Confirm Callback (ACK / NACK)
        rabbitTemplate.setConfirmCallback((correlationData, ack, cause) -> {
            if (ack) {
                System.out.println("🟢 [RabbitMQ ACK] Delivered → correlationId=" +
                        (correlationData != null ? correlationData.getId() : "null"));
            } else {
                System.out.println("🔴 [RabbitMQ NACK] Failed → cause=" + cause);
            }
        });

        // Returned Messages (routing failure)
        rabbitTemplate.setReturnsCallback(returned -> {
            System.out.println(
                    "⚠️ [RabbitMQ RETURN] Unroutable message → " +
                            "Exchange=" + returned.getExchange() +
                            ", RoutingKey=" + returned.getRoutingKey() +
                            ", ReplyCode=" + returned.getReplyCode() +
                            ", ReplyText=" + returned.getReplyText() +
                            ", Message=" + returned.getMessage()
            );
        });
    }

    /**
     * Send ANY Java object as JSON with publisher-confirm support.
     */
    public void sendEvent(String exchange, String routingKey, Object payload) {
        try {
            String json = objectMapper.writeValueAsString(payload);

            ExtendedCorrelationData correlationData =
                    new ExtendedCorrelationData(
                            UUID.randomUUID().toString(),
                            exchange,
                            routingKey,
                            json
                    );
            System.out.println("routing Key : "+routingKey);
            rabbitTemplate.convertAndSend(
                    exchange,
                    routingKey,
                    json,
                    correlationData
            );

        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException(
                    "Failed to serialize RabbitMQ payload: " + payload.getClass().getSimpleName(),
                    e
            );
        }
    }

}
