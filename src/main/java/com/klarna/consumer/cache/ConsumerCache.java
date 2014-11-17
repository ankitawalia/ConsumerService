package com.klarna.consumer.cache;

import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.cache.AbstractCache;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;
import com.klarna.consumer.api.Consumer;
import com.klarna.consumer.service.ConsumerCacheService;

public class ConsumerCache extends GenericCache<ConcurrentLinkedDeque<Consumer>>{
	
	@Autowired
	private ConsumerCacheService cacheService;
	
	public ConsumerCache() {
		buildCache();
	}

	@Override
	protected CacheBuilder<String,ConcurrentLinkedDeque<Consumer>> buildCacheBuilder() {
		return CacheBuilder.newBuilder().removalListener(new ConsumerRemovalListener())
				  .expireAfterWrite(5, TimeUnit.HOURS).maximumSize(100);	
	};

	private class ConsumerRemovalListener implements RemovalListener<String, ConcurrentLinkedDeque<Consumer>> {

		@Override
		public void onRemoval(
				RemovalNotification<String, ConcurrentLinkedDeque<Consumer>> notification) {
			cacheService.removeConsumerFromSeconaryMappings(notification.getKey());
		} 
	}
	
}
