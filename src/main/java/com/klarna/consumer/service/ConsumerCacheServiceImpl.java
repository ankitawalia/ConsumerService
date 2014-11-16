package com.klarna.consumer.service;

import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ExecutionException;

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
			consumerQueue = consumerCache.getIfPresent(consumerKey);
			if(consumerQueue ==  null) {
				consumerQueue = new ConcurrentLinkedDeque<>();
			}
			consumerQueue.add(consumer);
			consumerCache.put(ConsumerEquivalence.get().wrap(consumerKey),consumerQueue);
	}

	
	@Override
	public List<Consumer> getConsumerHistoryById(ConsumerKey consumerKey) {
		// TODO Auto-generated method stub
		return null;
	}



	@Override
	public Consumer getConsumer(ConsumerKey consumerKey) {
			
			ConcurrentLinkedDeque<Consumer> consumerQueue =  consumerCache.getIfPresent(ConsumerEquivalence.get().wrap(consumerKey));
			  return consumerQueue == null ? null : consumerQueue.getLast();
	}


	@Override
	public void afterPropertiesSet() throws Exception {
		consumerCache = cacheManager.getCache("ConsumerCache");		
	}

	
}
