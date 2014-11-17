package com.klarna.consumer.cache;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.klarna.consumer.service.ConsumerCacheService;

@Component
public class GenericCache<T> implements CacheCreationListener {
	@Autowired
	protected ConsumerCacheService consumerCacheService;
	@Autowired
	protected CacheManager cacheManager;
	
	protected  Cache<String, T > genericCache;
	protected CacheBuilder cacheBuilder;
 
	public GenericCache() {
		buildCache();
	}
	
	public void buildCache() {
		cacheBuilder =  buildCacheBuilder();
		build();
	}

	protected CacheBuilder buildCacheBuilder() {
		return CacheBuilder.newBuilder()
				  .expireAfterWrite(5, TimeUnit.HOURS).maximumSize(100);
	}

	private void build() {
		genericCache = cacheBuilder.build();
	}

	public  Cache getCache() {
		return genericCache;
	}

	@Override
	public void setCache(String name) {
		consumerCacheService.setCache(cacheManager.getCache("ConsumerCache"));	
	}
	
 
}
