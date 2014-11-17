package com.klarna.consumer.service;

import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedDeque;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.klarna.consumer.api.Address;
import com.klarna.consumer.api.Consumer;
import com.klarna.consumer.util.AddressNormalizer;


@Service
public class ConsumerServiceImpl implements ConsumerService {

	public Address address;
	
	@Autowired
	private ConsumerCacheService consumerCacheService;

	@Override
	public String saveConsumerInfo(Consumer consumer) {
		Consumer consumerToSave ;
		String consumerId = checkIfConsumerAlreadyExists(consumer.getEmail());
		if(consumerId == null) {
			consumerId = UUID.randomUUID().toString();
		}
		Address normalizedAddress = AddressNormalizer.normalize(consumer.getAddress());
		consumerToSave = consumer.withConsumerId(consumerId).withAddress(normalizedAddress);
		//consumerToSave = consumer.withAddress(normalizedAddress);
		consumerCacheService.addConsumer(consumerId, consumerToSave);
        return consumerToSave.getConsumerId();
		
	}
	
	@Override
	public Consumer getConsumerInfoForId(String consumerId) {
        return consumerCacheService.getConsumer(consumerId);
	}
	
	@Override
	public Consumer getConsumerInfoForEmail(String email){
		String consumerId = consumerCacheService.getConsumerIdForEmail(email);
		return consumerCacheService.getConsumer(consumerId);
	}
	
	private String checkIfConsumerAlreadyExists(String email) {
		String consumerId  = consumerCacheService.getConsumerIdForEmail(email);
		//Consumer consumer = consumerCacheService.getConsumer(consumerId);
		return consumerId;
	}

	@Override
	public ConcurrentLinkedDeque<Consumer> getConsumerHistoryById(String consumerId) {
		return consumerCacheService.getConsumerHistoryById(consumerId);
	}
}
