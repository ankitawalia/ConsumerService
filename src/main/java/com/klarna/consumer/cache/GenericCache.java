package com.klarna.consumer.cache;

import java.util.concurrent.TimeUnit;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

public class GenericCache<T> {
	 
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
	
 
}
