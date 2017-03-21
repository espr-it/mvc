package it.espr.mvc.cache;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.espr.mvc.ConfiguratorFactory;
import it.espr.mvc.route.Route;
import it.espr.mvc.route.RouteConfig;
import it.espr.mvc.route.RouteConfigurator;

public class CacheConfigurator {

	private static final Logger log = LoggerFactory.getLogger(CacheConfigurator.class);

	private Map<RouteConfig, List<CacheConfig>> configurations = new LinkedHashMap<>();

	private Map<String, Class<? extends Cache>> caches = new LinkedHashMap<>();

	private Map<String, String> registry = new LinkedHashMap<>();

	private boolean enabled;

	public void register(String cacheName, String cacheClass) {
		if (!registry.containsKey(cacheName)) {
			this.registry.put(cacheName, cacheClass);
		}
	}

	@SuppressWarnings("unchecked")
	Class<? extends Cache> loadCacheClass(String className) {
		Class<? extends Cache> cacheClass = null;
		try {
			cacheClass = (Class<? extends Cache>) Class.forName(className);
		} catch (Exception e) {
			log.error("Problem finding up {} cache class on classpath", e);
		}
		return cacheClass;
	}

	public void cache(boolean enabled) {
		this.enabled = enabled;
	}

	public CacheConfig cache(RouteConfig... routeConfigs) {
		CacheConfig cacheConfig = new CacheConfig();
		for (RouteConfig routeConfig : routeConfigs) {
			List<CacheConfig> configs = this.configurations.get(routeConfig);
			if (configs == null) {
				configs = new ArrayList<>();
				this.configurations.put(routeConfig, configs);
			}
			configs.add(cacheConfig);
		}
		return cacheConfig;
	}

	public Map<Route, List<Cache>> configure() {
		Map<Route, List<Cache>> caches = new LinkedHashMap<>();
		if (!enabled) {
			if (configurations.size() > 0) {
				log.debug("Found some cache configurations but cache is disabled - you might forgot to call cache(\"true\") in the mvc configuration.");
			} else {
				log.debug("Cache not enabled.");
			}
			return caches;
		}

		if (registry != null && registry.size() > 0) {
			log.debug("Looking for cache classes");
			for (Entry<String, String> entry : registry.entrySet()) {
				Class<? extends Cache> cacheClass = this.loadCacheClass(entry.getValue());
				if (cacheClass != null) {
					this.caches.put(entry.getKey(), cacheClass);
				}
			}
			log.debug("Finished looking for cache class, found {} ({})", this.caches.size(), this.caches);
		}

		if (configurations.size() > 0) {
			log.debug("Cache is enabled, configuring...");
			RouteConfigurator routeConfigurator = new ConfiguratorFactory().routeConfigurator();
			for (Entry<RouteConfig, List<CacheConfig>> configuration : configurations.entrySet()) {
				RouteConfig routeConfig = configuration.getKey();
				Route route = routeConfigurator.findRoute(routeConfig);

				List<Cache> routeCaches = new ArrayList<>();
				List<CacheConfig> cacheConfigs = configuration.getValue();
				for (CacheConfig cacheConfig : cacheConfigs) {
					if (cacheConfig.name != null && !this.caches.containsKey(cacheConfig.name)) {
						log.debug("No cache with name {} configured, skipping", cacheConfig.name);
						continue;
					}
					routeCaches.addAll(this.instantiateCaches(cacheConfig));
				}
				if (routeCaches.size() > 0) {
					caches.put(route, routeCaches);
				}
			}
		}
		return caches;
	}

	private List<Cache> instantiateCaches(CacheConfig cacheConfig) {
		List<Cache> caches = new ArrayList<>();
		Map<String, Class<? extends Cache>> toInstantiate = new LinkedHashMap<>();
		if (cacheConfig.name == null) {
			// instantiate all available caches
			for (Entry<String, Class<? extends Cache>> cacheClass : this.caches.entrySet()) {
				toInstantiate.put(cacheClass.getKey(), cacheClass.getValue());
			}
		} else {
			// instantiate only the cache specified in config
			if (this.caches.containsKey(cacheConfig.name)) {
				toInstantiate.put(cacheConfig.name, this.caches.get(cacheConfig.name));
			}
		}

		log.debug("Found {} cache classes for {} to instantiate", toInstantiate.size(), cacheConfig.name);
		for (Entry<String, Class<? extends Cache>> cacheClass : toInstantiate.entrySet()) {
			try {
				log.debug("Trying to instantiate cache {}:{}", cacheClass.getKey(), cacheClass.getValue());
				caches.add(this.instantiateCache(cacheClass.getKey(), cacheClass.getValue(), cacheConfig));
			} catch (Exception e) {
				log.error("Problem when instantiating.", e);
			}
		}

		return caches;
	}

	private Cache instantiateCache(String cacheName, Class<? extends Cache> cacheClass, CacheConfig cacheConfig) throws Exception {
		Constructor<? extends Cache> constructorWitCacheConfig = null;

		try {
			constructorWitCacheConfig = cacheClass.getConstructor(CacheConfig.class);
		} catch (Exception e) {
			// nothing important
		}

		if (constructorWitCacheConfig != null) {
			log.debug("Cache {}:{} supports CacheConfig parameter in constructor.", cacheName, cacheClass);
			return constructorWitCacheConfig.newInstance(cacheConfig);
		} else {
			log.debug("Cache {}:{} doesn't provide constructor with CacheConfig parameter, instatiating through default non-parametric constructor.", cacheName, cacheClass);
			return (Cache) cacheClass.getConstructors()[0].newInstance();
		}
	}
}
