package com.klarna.consumer.cache;

import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.TimeUnit;

import org.eclipse.jetty.util.Scanner.Notification;
import org.springframework.stereotype.Component;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;
import com.klarna.consumer.api.Consumer;

@Component
public class ConsumerCache extends
		GenericCache<ConcurrentLinkedDeque<Consumer>> implements
		CacheCreationListener {

	public ConsumerCache() {
		buildCache();
	}

	@Override
	protected CacheBuilder<String, ConcurrentLinkedDeque<Consumer>> buildCacheBuilder() {
		return CacheBuilder.newBuilder()
				.removalListener(new ConsumerRemovalListener())
				.expireAfterWrite(5, TimeUnit.HOURS).maximumSize(100);
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
