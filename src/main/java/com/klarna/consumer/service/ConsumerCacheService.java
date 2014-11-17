package com.klarna.consumer.service;

import java.util.concurrent.ConcurrentLinkedDeque;

import com.klarna.consumer.api.Consumer;
import com.klarna.consumer.util.ConsumerKey;

public interface ConsumerCacheService  extends CacheService{

public 	void addConsumer(ConsumerKey consumerKey, Consumer consumer);

public ConcurrentLinkedDeque<Consumer> getConsumerHistoryById(ConsumerKey consumerKey);

public Consumer getConsumer(ConsumerKey consumerKey);

}
