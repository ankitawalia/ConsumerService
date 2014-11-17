package com.klarna.consumer.service;

import java.util.concurrent.ConcurrentLinkedDeque;

import com.klarna.consumer.api.Consumer;

public interface ConsumerService {
	
	public String saveConsumerInfo(Consumer consumer);
	
	public Consumer getConsumerInfoForId(String consumerId);
	
	public Consumer getConsumerInfoForEmail(String email);

	ConcurrentLinkedDeque<Consumer> getConsumerHistoryById(String consumerId);

}
