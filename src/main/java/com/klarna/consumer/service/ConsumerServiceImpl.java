package com.klarna.consumer.service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.klarna.consumer.api.Address;
import com.klarna.consumer.api.Consumer;


@Service
public class ConsumerServiceImpl implements ConsumerService {

	public Address address;
	Map<String, Consumer> consumerData = new HashMap<String, Consumer>();

	@Override
	@Cacheable(value = { "consumerInfo" })
	public Consumer saveConsumerInfo(Consumer consumer) {
		Consumer consumerToSave ;
		if(consumerData.containsKey(consumer.getEmail())){
			consumerToSave = consumer.withConsumerId(consumerData.toString());
		}
		else{
			consumerToSave = consumer.withConsumerId(UUID.randomUUID().toString());
		}
        consumerData.put(consumerToSave.getEmail(), consumerToSave);
        return consumerToSave;
		
	}
	
	@Override
	@Cacheable(value = { "consumerInfoForId" })
	public Consumer getConsumerInfoForId(String consumerId) {
		System.out.println("customer data"+ consumerData.get(consumerId));
        return consumerData.get(consumerId);
	}
	
	@Override
	@Cacheable(value = { "consumerInfoForEmail" })
	public Consumer getConsumerInfoForEmail(String email){
		return consumerData.get(email);
	}
}
