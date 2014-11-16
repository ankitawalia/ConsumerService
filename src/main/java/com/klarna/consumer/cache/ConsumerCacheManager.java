package com.klarna.consumer.cache;

import java.util.concurrent.ConcurrentLinkedDeque;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.base.Equivalence;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.klarna.consumer.api.Consumer;
import com.klarna.consumer.util.ConsumerKey;

@Service
public class ConsumerCacheManager {
		
		public CacheBuilder getCacheBuilder() {
		return cacheBuilder;
	}

	public void setCacheBuilder(CacheBuilder cacheBuilder) {
		this.cacheBuilder = cacheBuilder;
	}

		private CacheBuilder cacheBuilder;
		
		public ConsumerCacheManager() {
			
		}
		
		public Cache<Equivalence.Wrapper<ConsumerKey>, ConcurrentLinkedDeque<Consumer>> getCache(String name) {
			Cache<Equivalence.Wrapper<ConsumerKey>, ConcurrentLinkedDeque<Consumer>> cache=cacheBuilder.build();
			return cache;
		}
		
		public static class ConsumerEquivalence extends Equivalence<ConsumerKey>{

		private static ConsumerEquivalence equiv;
		private ConsumerEquivalence() {
			
		}
		
		public static ConsumerEquivalence get() {
			if(equiv==null) {
				equiv = new ConsumerEquivalence();
			}
			return equiv;
		}
			
			
		@Override
		protected boolean doEquivalent(ConsumerKey a, ConsumerKey b) {
			return a.equals(b);
		}

		@Override
		protected int doHash(ConsumerKey t) {
			return t.hashCode();
		}
		}
}
