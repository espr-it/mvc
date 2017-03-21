package it.espr.mvc.cache;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.espr.mvc.route.Route;

public class CacheFactory implements Cache {

	private static final Logger log = LoggerFactory.getLogger(CacheFactory.class);

	private Map<Route, List<Cache>> caches = new HashMap<>();

	public CacheFactory(@Named("MvcCaches") Map<Route, List<Cache>> caches) {
		super();
		this.caches = caches;
	}

	@Override
	public void put(String requestType, String uri, Route route, Object data) {
		List<Cache> routeCaches = caches.get(route);
		if (routeCaches == null) {
			log.trace("No cache configured for route {}", route);
			return;
		}

		for (Cache cache : routeCaches) {
			cache.put(requestType, uri, route, data);
			log.debug("PUT! {} for {} {} ({})", cache, requestType, uri, route);
		}
	}

	@Override
	public Object get(String requestType, String uri, Route route) {
		List<Cache> routeCaches = caches.get(route);
		if (routeCaches == null) {
			log.trace("No cache configured for route {}", route);
			return null;
		}

		Object data = null;
		for (Cache cache : routeCaches) {
			data = cache.get(requestType, uri, route);
			if (data != null) {
				log.debug("HIT! {} for {} {} ({})", cache, requestType, uri, route);
				break;
			} else {
				log.debug("MISS! {} for {} {} ({})", cache, requestType, uri, route);
			}
		}
		return data;
	}

}
