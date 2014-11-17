package com.klarna.consumer.configuration;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import com.klarna.consumer.cache.CacheManager;
import com.klarna.consumer.cache.ConsumerCache;

@Configuration
@EnableWebMvc
@ComponentScan(basePackages = "com.klarna", excludeFilters = {@ComponentScan.Filter(Configuration.class)})
public class CacheConfigurator implements InitializingBean{
	
	
	@Autowired
	 public CacheManager cacheManager;
	
	@Autowired
	 public ConsumerCache consumerCache;

	@Override
	public void afterPropertiesSet() throws Exception {
		CacheManager.registerCache("ConsumerCache",consumerCache);
	}
	
	
}
