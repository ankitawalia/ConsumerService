package com.klarna.consumer.cache;

import com.klarna.consumer.configuration.CacheConfigurator;


/**
 * 
 * Cache listener to be implemented by all the application caches.
 * Used to hook all the caches bootstrapped at start up in service classes
 * so as to make them available for cache operations.
 * 
 * All the caches are automatically made available in service layer once they are registered
 * with Cache manager during start up.
 * 
 * See {@link CacheConfigurator} to see where caches are registered.
 *
 */
public interface CacheCreationListener {
	
	void setCache(String name);

}
