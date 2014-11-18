package com.klarna.consumer.cache;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author ankita walia
 * 
 * Generic class for cache cleanup
 */
@Service
public class CacheCleanup {
 
	private ScheduledExecutorService scheduler;
 
	@Autowired
	CacheManager cacheManager;
 
	@PreDestroy
	public void shutdown() {		
		scheduler.shutdown();
	}
 
	@PostConstruct
	public void cacheCleanup() {
		scheduler = Executors.newScheduledThreadPool(1);
		scheduler.scheduleWithFixedDelay(new Runnable() {
			@Override
			public void run() {
				for (String cacheName : cacheManager.getCacheNames()) {
					( cacheManager.getCache(cacheName)).cleanUp();
				}
			}
		}, 5, 1, TimeUnit.HOURS);
 
	}
}
