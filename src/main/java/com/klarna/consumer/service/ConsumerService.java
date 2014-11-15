package com.klarna.consumer.service;

import com.klarna.consumer.api.Consumer;

public interface ConsumerService {
	
	public Consumer saveConsumerInfo(Consumer consumer);
	
	public Consumer getConsumerInfoForId(String consumerId);
	
	public Consumer getConsumerInfoForEmail(String email);

}
