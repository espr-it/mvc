package it.espr.mvc.view;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.espr.mvc.json.Json;
import it.espr.mvc.route.Route;

public class ViewConfigurator {

	private static final Logger log = LoggerFactory.getLogger(ViewConfigurator.class);

	private Map<Object, ViewConfig> viewConfigs = new HashMap<>();

	// this is used for chaining/config from the mvc configuration
	public ViewConfig view(Object type) {
		ViewConfig config = new ViewConfig(type);

		// keep the original handler just in case user doesn't finish overriding
		if (this.hasConfigurationFor(type)) {
			config.clazz = this.viewConfigs.get(type).clazz;
		}

		this.viewConfigs.put(type, config);
		return config;
	}

	public boolean hasConfigurationFor(Object type) {
		return viewConfigs.containsKey(type);
	}

	public Map<Object, Class<? extends View>> configure(Class<? extends Json> json, List<Route> routes) {
		Map<Object, Class<? extends View>> views = new LinkedHashMap<>();

		for (ViewConfig config : viewConfigs.values()) {
			views.put(config.type, config.clazz);
		}

		// add all views configured via routes
		for (Route route : routes) {
			Class<? extends View> view = route.view;
			if (!views.containsKey(view)) {
				views.put(view, view);
			}
		}
		
		if (views.containsKey(null)) {
			Class<? extends View> defaultView = views.get(null);
			views.remove(null);
			views.put(null, defaultView);
		}
		
		return views;
	}
}
