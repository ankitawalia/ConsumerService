package com.klarna.consumer.service;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.klarna.consumer.api.Address;
import com.klarna.consumer.api.Consumer;
import com.klarna.consumer.util.ConsumerKey;


@Service
public class ConsumerServiceImpl implements ConsumerService {

	public Address address;
	
	@Autowired
	private ConsumerCacheService consumerCacheService;

	@Override
	@Cacheable(value = { "consumerInfo" })
	public Consumer saveConsumerInfo(Consumer consumer) {
		Consumer consumerToSave ;
		ConsumerKey consumerKey = getConsumerKey(consumer.getConsumerId(), consumer.getEmail());
		String consumerId = checkIfConsumerAlreadyExists(consumerKey);
		if(consumerId == null) {
			consumerId = UUID.randomUUID().toString();
			consumerKey = getConsumerKey(consumerId, consumer.getEmail());
		}
		consumerToSave = consumer.withConsumerId(consumerId);
		consumerCacheService.addConsumer(consumerKey, consumerToSave);
        return consumerToSave;
		
	}
	
	@Override
	@Cacheable(value = { "consumerInfoForId" })
	public Consumer getConsumerInfoForId(String consumerId) {
		ConsumerKey consumerKey = getConsumerKey(consumerId, null);
        return consumerCacheService.getConsumer(consumerKey);
	}
	
	@Override
	@Cacheable(value = { "consumerInfoForEmail" })
	public Consumer getConsumerInfoForEmail(String email){
		ConsumerKey consumerKey = getConsumerKey(null, email);
		return consumerCacheService.getConsumer(consumerKey);
	}
	
	private String checkIfConsumerAlreadyExists(ConsumerKey consumerKey) {
		Consumer consumer = consumerCacheService.getConsumer(consumerKey);
		return consumer == null ? null : consumer.getConsumerId();
	}

	private ConsumerKey getConsumerKey(String consumerId, String email) {
		ConsumerKey consumerKey = new ConsumerKey(consumerId, email);
		return consumerKey;
		
	}
}
