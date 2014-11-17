package com.klarna.consumer.service;

import java.util.concurrent.ConcurrentLinkedDeque;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

import com.google.common.base.Equivalence;
import com.google.common.cache.Cache;
import com.klarna.consumer.api.Consumer;
import com.klarna.consumer.cache.ConsumerCacheManager.ConsumerEquivalence;
import com.klarna.consumer.util.ConsumerKey;

@Service
public  class ConsumerCacheServiceImpl extends CacheServiceImpl implements ConsumerCacheService,InitializingBean{
	
	private Cache<Equivalence.Wrapper<ConsumerKey>, ConcurrentLinkedDeque<Consumer>> consumerCache;
	
	@Override
	public void addConsumer(ConsumerKey consumerKey, Consumer consumer) {
		ConcurrentLinkedDeque<Consumer> consumerQueue;
			consumerQueue = consumerCache.getIfPresent(ConsumerEquivalence.get().wrap(consumerKey));
			if(consumerQueue ==  null) {
				consumerQueue = new ConcurrentLinkedDeque<>();
			}
			boolean existFlag = validateConsumerData(consumerQueue,consumer);
			if(!existFlag){
				consumerQueue.addFirst(consumer);
				consumerCache.put(ConsumerEquivalence.get().wrap(consumerKey),consumerQueue);
			}
	}

	
	private boolean validateConsumerData(
			ConcurrentLinkedDeque<Consumer> consumerQueue,Consumer consumer) {
		if(!consumerQueue.isEmpty() && consumerQueue.getFirst().equals(consumer)){
			return true	;
		}
		return false;
	}


	@Override
	public ConcurrentLinkedDeque<Consumer> getConsumerHistoryById(ConsumerKey consumerKey) {
		ConcurrentLinkedDeque<Consumer> consumerQueue =  consumerCache.getIfPresent(ConsumerEquivalence.get().wrap(consumerKey));
		  return consumerQueue;
	}



	@Override
	public Consumer getConsumer(ConsumerKey consumerKey) {
			
			ConcurrentLinkedDeque<Consumer> consumerQueue =  consumerCache.getIfPresent(ConsumerEquivalence.get().wrap(consumerKey));
			  return consumerQueue == null ? null : consumerQueue.getFirst();
	}


	@Override
	public void afterPropertiesSet() throws Exception {
		consumerCache = cacheManager.getCache("ConsumerCache");		
	}

	
}
