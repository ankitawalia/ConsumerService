package com.klarna.consumer.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.klarna.consumer.cache.ConsumerCacheManager;

@Service
public abstract class CacheServiceImpl implements CacheService{
	
	@Autowired
	ConsumerCacheManager cacheManager;


}
