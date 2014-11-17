package com.klarna.consumer.service;

import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.CopyOnWriteArrayList;

import org.springframework.stereotype.Service;

import com.google.common.cache.Cache;
import com.klarna.consumer.api.Consumer;
import com.klarna.consumer.util.ConsumerKeyIndex;

@Service
public  class ConsumerCacheServiceImpl extends CacheServiceImpl implements ConsumerCacheService{
	
	private Cache<String, ConcurrentLinkedDeque<Consumer>> consumerCache;
	
	public static List<ConsumerKeyIndex> consumerKeyIndexList = new CopyOnWriteArrayList<>();
	
	@Override
	public void addConsumer(String consumerId, Consumer consumer) {
		ConcurrentLinkedDeque<Consumer> consumerQueue;
			consumerQueue = consumerCache.getIfPresent( consumerId);
			if(consumerQueue ==  null) {
				consumerQueue = new ConcurrentLinkedDeque<>();
				addSecondaryKeyMappings(consumer.getConsumerId(), consumer.getEmail());
			}
			boolean existFlag = validateConsumerData(consumerQueue,consumer);
			if(!existFlag){
				consumerQueue.addFirst(consumer);
				consumerCache.put(consumerId,consumerQueue);
			}
	}

	
	private void addSecondaryKeyMappings(String consumerId, String email) {
		ConsumerKeyIndex consumerKeyIndex = new ConsumerKeyIndex(consumerId, email);
		consumerKeyIndexList.add(consumerKeyIndex);
	}


	private boolean validateConsumerData(
			ConcurrentLinkedDeque<Consumer> consumerQueue,Consumer consumer) {
		if(!consumerQueue.isEmpty() && consumerQueue.getFirst().equals(consumer)){
			return true	;
		}
		return false;
	}


	@Override
	public ConcurrentLinkedDeque<Consumer> getConsumerHistoryById(String consumerId) {
		ConcurrentLinkedDeque<Consumer> consumerQueue =  consumerCache.getIfPresent(consumerId);
		  return consumerQueue;
	}



	@Override
	public Consumer getConsumer(String consumerId) {
			
			ConcurrentLinkedDeque<Consumer> consumerQueue =  consumerCache.getIfPresent(consumerId);
			  return consumerQueue == null ? null : consumerQueue.getFirst();
	}




	@Override
	public String getConsumerIdForEmail(String email) {
		ConsumerKeyIndex consumerKeyIndex = new ConsumerKeyIndex(null, email);
		int keyIndex =  consumerKeyIndexList.indexOf(consumerKeyIndex);
		return keyIndex != -1 ? consumerKeyIndexList.get(keyIndex).getId() : null;
	}
	
	public void removeConsumerFromSeconaryMappings(String id) {
		ConsumerKeyIndex consumerKeyIndex = new ConsumerKeyIndex(id, null);
		consumerKeyIndexList.remove(consumerKeyIndex);
	}


	@Override
	public void setCache(Cache cache) {
		this.consumerCache = cache;  		
	}
	
}
