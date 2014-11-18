package com.klarna.consumer.service;

import java.util.HashMap;
import java.util.Map;
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

	/**
	  * Method to save consumer info in the cache.
	  * 
	  * It checks if the consumer with the same email address has already been 
	  * registered, the consumer id for this registered consumer is returned. 
	  * If it is a new consumer, a new id is generated.
	  * Also it stores all address fields in a normalized form
	  * 
	  * @Param Consumer object
	  * @return Map of String
	  * 
	  */
	@Override
	public Map<String, String> saveConsumerInfo(final Consumer consumer) {
		Consumer consumerToSave ;
		String consumerId = checkIfConsumerAlreadyExists(consumer.getEmail());
		if(consumerId == null) {
			consumerId = UUID.randomUUID().toString();
		}
		Address normalizedAddress = AddressNormalizer.normalize(consumer.getAddress());
		consumerToSave = consumer.withConsumerId(consumerId).withAddress(normalizedAddress);
		consumerCacheService.addConsumer(consumerId, consumerToSave);
		Map<String,String> consumerIdMap =new HashMap<String,String>();
		consumerIdMap.put("consumer_id", consumerToSave.getConsumerId());
        return consumerIdMap;
		
	}
	
	@Override
	public Consumer getConsumerInfoForId(final String consumerId) {
        return consumerCacheService.getConsumer(consumerId);
	}
	
	@Override
	public Consumer getConsumerInfoForEmail(final String email){
		String consumerId = consumerCacheService.getConsumerIdForEmail(email);
		return consumerId != null ? consumerCacheService.getConsumer(consumerId) : null;
	}
	
	/**
	  * Method to check if consumer with the Email already exists.
	  * 
	  * A CopyOnWriteArrayList is maintained which stores consumer email and id.
	  * Whenever post request comes to save or update consumer info email is checked in this list .
	  * If email exists then Id for that consumer is returned.
	  * 
	  * @Param email 
	  * @return consumerIdString
	  * 
	  */
	private String checkIfConsumerAlreadyExists(final String email) {
		String consumerId  = consumerCacheService.getConsumerIdForEmail(email);
		return consumerId;
	}

	@Override
	public ConcurrentLinkedDeque<Consumer> getConsumerHistoryById(final String consumerId) {
		return consumerCacheService.getConsumerHistoryById(consumerId);
	}
	
}
