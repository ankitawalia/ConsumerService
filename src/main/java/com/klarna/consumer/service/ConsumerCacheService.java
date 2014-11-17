package com.klarna.consumer.service;

import java.util.concurrent.ConcurrentLinkedDeque;

import com.google.common.cache.Cache;
import com.klarna.consumer.api.Consumer;

public interface ConsumerCacheService  extends CacheService{

public 	void addConsumer(String consumerId, Consumer consumer);

public ConcurrentLinkedDeque<Consumer> getConsumerHistoryById(String consumerId);

public Consumer getConsumer(String consumerId);

public String getConsumerIdForEmail(String email);

public void removeConsumerFromSeconaryMappings(String id);

public void setCache(Cache cache);

}
