package com.klarna.consumer.cache;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.springframework.stereotype.Service;

import com.google.common.cache.Cache;

@Service
@SuppressWarnings("rawtypes")
public class CacheManager {
	
		private final static ConcurrentMap<String, Cache> cacheMap = new ConcurrentHashMap<String, Cache>(16);
		
		public CacheManager() {
			
		}
		
		public Cache getCache(String name) {
			Cache cache=cacheMap.get(name);
			return cache;
		}
		
		public static void registerCache(String name, Cache cache) {
			cacheMap.putIfAbsent(name, cache);
		}

		public Set<String> getCacheNames() {
			return Collections.unmodifiableSet(cacheMap.keySet());
		}
}
