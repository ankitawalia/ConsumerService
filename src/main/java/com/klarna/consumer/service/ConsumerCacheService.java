package com.klarna.consumer.service;

import java.util.List;

import com.klarna.consumer.api.Consumer;
import com.klarna.consumer.util.ConsumerKey;

public interface ConsumerCacheService  extends CacheService{

public 	void addConsumer(ConsumerKey consumerKey, Consumer consumer);

public List<Consumer> getConsumerHistoryById(ConsumerKey consumerKey);

public Consumer getConsumer(ConsumerKey consumerKey);

}
