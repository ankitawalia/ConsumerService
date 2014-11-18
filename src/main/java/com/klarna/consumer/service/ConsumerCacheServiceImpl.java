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
	
	
	/**
	  * Method to add consumer info in the cache.
	  * 
	  * It checks if the consumer with the consumerId is present in the cache.
	  * It validates if consumer at the head is same then does not add the consumer again in the cache.
	  * If it is not the same then add consumer to the head of the deque.
	  * And add Queue to the consumer cache.
	  * 
	  * If consumerQueue in cache is null then create new one 
	  * and also store a mapping for the consumerId and email in SecondaryList
	  * 
	  * @Param Consumer object
	  * @return void
	  * 
	  */
	@Override
	public void addConsumer(final String consumerId,final Consumer consumer) {
		ConcurrentLinkedDeque<Consumer> consumerQueue;
			consumerQueue = consumerCache.getIfPresent(consumerId);
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

	
	private void addSecondaryKeyMappings(final String consumerId, final String email) {
		ConsumerKeyIndex consumerKeyIndex = new ConsumerKeyIndex(consumerId, email);
		consumerKeyIndexList.add(consumerKeyIndex);
	}


	private boolean validateConsumerData(
			final ConcurrentLinkedDeque<Consumer> consumerQueue,final Consumer consumer) {
		if(!consumerQueue.isEmpty() && consumerQueue.getFirst().equals(consumer)){
			return true	;
		}
		return false;
	}


	@Override
	public ConcurrentLinkedDeque<Consumer> getConsumerHistoryById(final String consumerId) {
		ConcurrentLinkedDeque<Consumer> consumerQueue =  consumerCache.getIfPresent(consumerId);
		  return consumerQueue;
	}


	/**
	  * Method to retrieve Consumer object from the Cache.
	  * If consumer with the given Id already exists then return 
	  * the most recently added consumer info else return null
	  * @Param consumerId 
	  * @return Consumer object
	  * 
	  */
	@Override
	public Consumer getConsumer(final String consumerId) {
			
			ConcurrentLinkedDeque<Consumer> consumerQueue =  consumerCache.getIfPresent(consumerId);
			  return consumerQueue == null ? null : consumerQueue.getFirst();
	}

	/**
	  * Method to retrieve consumerId if email exists.
	  * 
	  * get indexOf the object with the given email 
	  * and then retrieve Id  based on Index
	  * 
	  * @Param email 
	  * @return consumerIdString
	  * 
	  */

	@Override
	public String getConsumerIdForEmail(final String email) {
		ConsumerKeyIndex consumerKeyIndex = new ConsumerKeyIndex(null, email);
		int keyIndex =  consumerKeyIndexList.indexOf(consumerKeyIndex);
		return keyIndex != -1 ? consumerKeyIndexList.get(keyIndex).getId() : null;
	}
	
	/**
	  * To remove data from the List based on the id.
	  * To cleanUp the list if something is removed from the Consumer cache
	  * 
	  * @Param id 
	  * @return void
	  */
	public void removeConsumerFromSeconaryMappings(final String id) {
		ConsumerKeyIndex consumerKeyIndex = new ConsumerKeyIndex(id, null);
		consumerKeyIndexList.remove(consumerKeyIndex);
	}


	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void setCache(Cache cache) {
		this.consumerCache = cache;  		
	}
	
}
