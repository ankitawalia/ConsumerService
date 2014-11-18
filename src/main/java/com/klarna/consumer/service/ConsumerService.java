package com.klarna.consumer.service;

import java.util.Map;
import java.util.concurrent.ConcurrentLinkedDeque;

import com.klarna.consumer.api.Consumer;

/**
 * @author ankita walia
 * Interface to rest controller and ConsumerService Impl 
 *
 */
public interface ConsumerService {
	
	public Map<String, String> saveConsumerInfo(Consumer consumer);
	
	public Consumer getConsumerInfoForId(String consumerId);
	
	public Consumer getConsumerInfoForEmail(String email);

	ConcurrentLinkedDeque<Consumer> getConsumerHistoryById(String consumerId);

}
