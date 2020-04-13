package it.espr.mvc;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.espr.mvc.cache.Cache;
import it.espr.mvc.cache.CacheConfig;
import it.espr.mvc.cache.CacheConfigurator;
import it.espr.mvc.converter.StringToTypeConverter;
import it.espr.mvc.json.Json;
import it.espr.mvc.json.JsonFinder;
import it.espr.mvc.route.Route;
import it.espr.mvc.route.RouteConfig;
import it.espr.mvc.route.StaticResourcesRoute;
import it.espr.mvc.view.View;
import it.espr.mvc.view.ViewConfig;

public abstract class MvcConfiguration extends it.espr.injector.Configuration {

	private static final Logger log = LoggerFactory.getLogger(MvcConfiguration.class);

	private ConfiguratorFactory configuratorFactory;

	protected MvcConfiguration() {
		this(new ConfiguratorFactory());
	}

	protected MvcConfiguration(ConfiguratorFactory configuratorFactory) {
		this.configuratorFactory = configuratorFactory;
	}

	protected RouteConfig route() {
		return configuratorFactory.routeConfigurator().route();
	}

	protected void registerConverter(Class<? extends StringToTypeConverter<?>> converter) {
		configuratorFactory.stringToTypeConverterConfigurator().register(converter);
	}

	protected ViewConfig view(String... accept) {
		return configuratorFactory.viewConfigurator().view(accept);
	}

	protected CacheConfig cache(RouteConfig routeConfig) {
		return configuratorFactory.cacheConfigurator().cache(routeConfig);
	}

	protected void enableCache() {
		configuratorFactory.cacheConfigurator().cache(true);
	}

	protected final void configure() {
		// configure user bindings first
		log.debug("Configuring user bindings...");
		this.configureMvc();
		log.debug("Configured user bindings...");

		Class<? extends Json> json = new JsonFinder().find();
		if (json != null) {
			this.bind(Json.class).to(json);
		}

		// configure converters
		log.debug("Configuring String to Type converters...");
		List<Class<? extends StringToTypeConverter<?>>> converters = configuratorFactory
				.stringToTypeConverterConfigurator().configure();
		this.bind(converters).named("StringToTypeConverters");
		log.debug("Configured String to Type converters: {} converters ({})", converters.size(), converters);

		// configure routes
		log.debug("Configuring routes...");
		route().get("/static/(.*:path)").to(StaticResourcesRoute.class, "get");
		List<Route> routes = configuratorFactory.routeConfigurator().configure();
		this.bind(routes).named("MvcRoutes");
		log.debug("Configured routes: {}", routes.size());

		// configure views
		log.debug("Configuring views...");
		Map<String, Class<? extends View>> views = configuratorFactory.viewConfigurator().configure(json);

		this.bind(views).named("MvcViews");
		log.debug("Configured views: {} ({})", views.size(), views);

		// configure caches
		log.debug("Configuring caches...");
		CacheConfigurator cacheConfigurator = configuratorFactory.cacheConfigurator();
		cacheConfigurator.register("memory", "it.espr.mvc.cache.InMemoryCache");
		Map<Route, List<Cache>> caches = cacheConfigurator.configure();
		this.bind(caches).named("MvcCaches");
		log.debug("Configured caches: {} ({})", caches.size(), caches);
	}

	protected abstract void configureMvc();

}
