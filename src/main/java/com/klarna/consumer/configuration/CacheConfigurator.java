package com.klarna.consumer.configuration;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import com.klarna.consumer.cache.CacheManager;
import com.klarna.consumer.cache.ConsumerCache;
import com.klarna.consumer.cache.GenericCache;

@Configuration
@EnableWebMvc
@ComponentScan(basePackages = "com.klarna", excludeFilters = {@ComponentScan.Filter(Configuration.class)})
public class CacheConfigurator implements InitializingBean{
	
	
	@Bean
	 public CacheManager cacheManager() {
	  CacheManager cacheManager = new CacheManager();
	  
	  return cacheManager;
	 }

	@Override
	public void afterPropertiesSet() throws Exception {
		CacheManager.registerCache("ConsumerCache",new ConsumerCache().getCache());
		  CacheManager.registerCache("ProducerCache",new GenericCache<String>().getCache());
	}
}
