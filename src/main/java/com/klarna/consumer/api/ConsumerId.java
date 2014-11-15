package com.klarna.consumer.api;

public class ConsumerId extends ValueObject {

    private String consumerId;

    public ConsumerId() {}

    public ConsumerId(String consumerId) {
        this.consumerId = consumerId;
    }

    public String getConsumerId() {
        return consumerId;
    }

}
