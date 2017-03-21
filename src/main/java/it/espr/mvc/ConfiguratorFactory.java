package it.espr.mvc;

import it.espr.mvc.cache.CacheConfigurator;
import it.espr.mvc.converter.StringToTypeConverterConfigurator;
import it.espr.mvc.route.RouteConfigurator;
import it.espr.mvc.view.ViewConfigurator;

public class ConfiguratorFactory {

	private static ViewConfigurator viewConfigurator;

	private static RouteConfigurator routeConfigurator;

	private static CacheConfigurator cacheConfigurator;

	private static StringToTypeConverterConfigurator stringToTypeConverterConfigurator;

	public ViewConfigurator viewConfigurator() {
		if (viewConfigurator == null) {
			viewConfigurator = new ViewConfigurator();
		}
		return viewConfigurator;
	}

	public RouteConfigurator routeConfigurator() {
		if (routeConfigurator == null) {
			routeConfigurator = new RouteConfigurator();
		}
		return routeConfigurator;
	}

	public CacheConfigurator cacheConfigurator() {
		if (cacheConfigurator == null) {
			cacheConfigurator = new CacheConfigurator();
		}
		return cacheConfigurator;
	}

	public StringToTypeConverterConfigurator stringToTypeConverterConfigurator() {
		if (stringToTypeConverterConfigurator == null) {
			stringToTypeConverterConfigurator = new StringToTypeConverterConfigurator();
		}
		return stringToTypeConverterConfigurator;
	}

}
