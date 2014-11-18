package com.klarna.consumer.cache;

import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.TimeUnit;

import org.eclipse.jetty.util.Scanner.Notification;
import org.springframework.stereotype.Component;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;
import com.klarna.consumer.api.Consumer;

/**
 * 
 * Consumercache class to store all the consumer data in memory. Method
 * to build cache for Consumer.
 * Max Cache Size (Consumer objects that can be stored
 * in the cache) are 1000 
 * Cache expires after 5 hours (Can be fine tuned depending on requirements).
 * 
 * Also calls {@link ConsumerRemovalListener} when cache item is removed to clean the secondary keys mapping data.
 */

@Component
public class ConsumerCache extends
		GenericCache<ConcurrentLinkedDeque<Consumer>> implements
		CacheCreationListener {

	public ConsumerCache() {
		buildCache();
	}

	@Override
	protected CacheBuilder<String, ConcurrentLinkedDeque<Consumer>> buildCacheBuilder() {
		return CacheBuilder.newBuilder().softValues()
				.removalListener(new ConsumerRemovalListener())
				.expireAfterWrite(5, TimeUnit.HOURS).maximumSize(1000);
	};

	private class ConsumerRemovalListener implements
			RemovalListener<String, ConcurrentLinkedDeque<Consumer>> {

		@Override
		public void onRemoval(
				RemovalNotification<String, ConcurrentLinkedDeque<Consumer>> notification) {
			if (notification.wasEvicted()
					|| notification.getCause().equals(Notification.REMOVED)) {
				consumerCacheService
						.removeConsumerFromSeconaryMappings(notification
								.getKey());
			}
		}
	}

	@Override
	public void setCache(final String name) {
		consumerCacheService.setCache(cacheManager.getCache("ConsumerCache"));
	}

}
