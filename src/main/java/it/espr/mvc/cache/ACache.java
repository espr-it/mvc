package it.espr.mvc.cache;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class ACache implements Cache {

	private static final Logger log = LoggerFactory.getLogger(ACache.class);
	
	protected int expiration = 60 * 10;

	protected static final class CacheItem {

		CacheItem(Object data) {
			super();
			this.insertion = new Date().getTime();
			this.data = data;
		}

		long insertion;

		Object data;
	}

	protected ACache(CacheConfig cacheConfig) {
		if (cacheConfig.expiration > 0) {
			this.expiration = cacheConfig.expiration;
		}
	}

	public Object get(String key, CacheItem cacheItem) {
		if (cacheItem == null) {
			return cacheItem;
		}

		if (cacheItem.insertion < new Date().getTime() - expiration * 1000) {
			log.debug("Found item in the cache for {} but it's outdated (expiration set to {} seconds) - removing.", key, expiration);
			this.remove(key, cacheItem);
			return null;
		}
		return cacheItem.data;
	}

	public abstract void remove(String key, CacheItem cacheItem);

}
