package com.klarna.consumer.cache;

import java.util.concurrent.ConcurrentLinkedDeque;

import org.springframework.stereotype.Service;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.klarna.consumer.api.Consumer;

@Service
public class ConsumerCacheManager {
		
		public CacheBuilder getCacheBuilder() {
		return cacheBuilder;
	}

	public void setCacheBuilder(CacheBuilder cacheBuilder) {
		this.cacheBuilder = cacheBuilder;
	}

		private CacheBuilder cacheBuilder;
		
		public ConsumerCacheManager() {
			
		}
		
		public Cache<String, ConcurrentLinkedDeque<Consumer>> getCache(String name) {
			Cache<String, ConcurrentLinkedDeque<Consumer>> cache=cacheBuilder.build();
			return cache;
		}
		
}
