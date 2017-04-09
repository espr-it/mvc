package it.espr.mvc.cache;

import com.googlecode.concurrentlinkedhashmap.ConcurrentLinkedHashMap;

import it.espr.mvc.route.Route;

public class InMemoryCache extends ACache implements Cache {

	private CacheConfig cacheConfig;

	private ConcurrentLinkedHashMap<String, CacheItem> cache;

	public InMemoryCache(CacheConfig cacheConfig) {
		super(cacheConfig);
		this.cacheConfig = cacheConfig;
		int size = cacheConfig.size > 0 ? cacheConfig.size : 200;
		cache = new ConcurrentLinkedHashMap.Builder<String, CacheItem>().maximumWeightedCapacity(size).build();
	}

	@Override
	public void put(String requestType, String uri, Route route, Object data) {
		this.cache.put(key(requestType, uri), new CacheItem(data));
	}

	@Override
	public Object get(String requestType, String uri, Route route) {
		return super.get(key(requestType, uri), this.cache.get(key(requestType, uri)));
	}

	String key(String requestType, String uri) {
		return requestType + " " + uri;
	}

	@Override
	public void remove(String key, CacheItem cacheItem) {
		this.cache.remove(key);
	}
}
