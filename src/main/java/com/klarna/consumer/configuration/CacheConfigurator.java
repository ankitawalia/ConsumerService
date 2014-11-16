package com.klarna.consumer.configuration;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import com.google.common.cache.CacheBuilder;
import com.klarna.consumer.cache.ConsumerCacheManager;

@Configuration(value="consumerCacheManager")
@EnableWebMvc
@ComponentScan(basePackages = "com.klarna", excludeFilters = {@ComponentScan.Filter(Configuration.class)})
public class CacheConfigurator implements InitializingBean {
	
	private CacheBuilder cacheBuilder;
	
	@Bean
	 public ConsumerCacheManager cacheManager() {
	   cacheBuilder = CacheBuilder.newBuilder()
			  .expireAfterWrite(2, TimeUnit.HOURS).maximumSize(100);
	  ConsumerCacheManager cacheManager = new ConsumerCacheManager();
	  return cacheManager;
	 }

	@Override
	public void afterPropertiesSet() throws Exception {
		cacheManager().setCacheBuilder(cacheBuilder);
	}
	
	
	
}
