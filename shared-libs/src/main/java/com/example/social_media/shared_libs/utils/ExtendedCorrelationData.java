package com.example.social_media.shared_libs.utils;

import org.springframework.amqp.rabbit.connection.CorrelationData;

public class ExtendedCorrelationData extends CorrelationData {
    private final String exchange;
    private final String routingKey;
    private final String payload;

    public ExtendedCorrelationData(String id, String exchange, String routingKey, String payload) {
        super(id);
        this.exchange = exchange;
        this.routingKey = routingKey;
        this.payload = payload;
    }

    public String getExchange() { return exchange; }
    public String getRoutingKey() { return routingKey; }
    public String getPayload() { return payload; }
}
