package it.espr.mvc.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.espr.mvc.view.json.JsonView;
import it.espr.mvc.view.json.JsonViewFinder;

public class ViewConfigurator {

	private static final Logger log = LoggerFactory.getLogger(ViewConfigurator.class);

	private List<ViewConfig> viewConfiguration = new ArrayList<>();

	private Map<String, ViewConfig> viewConfigurationMap = new HashMap<>();

	public ViewConfig view(String... accept) {
		ViewConfig viewConfig = new ViewConfig(accept);
		viewConfiguration.add(viewConfig);
		for (String a : viewConfig.accept) {
			this.viewConfigurationMap.put(a, viewConfig);
		}
		return viewConfig;
	}

	public ViewConfig get(String accept) {
		return this.viewConfigurationMap.get(accept);
	}

	public Map<String, Class<? extends View>> configure(boolean isJsonBound) {
		Map<String, Class<? extends View>> views = new LinkedHashMap<>();

		for (ViewConfig viewConfig : viewConfiguration) {
			for (String accept : viewConfig.accept) {
				views.put(accept, viewConfig.clazz);
			}
		}

		this.addDefaultViews(views, isJsonBound);

		return views;
	}

	private final void addDefaultViews(Map<String, Class<? extends View>> views, boolean isJsonBound) {
		log.debug("Adding default views...");

		// add simple view as a default and text/html option if user didn't
		// declared them
		if (!views.containsKey(null)) {
			log.debug("Adding SimpleView as a default view.");
			views.put(null, SimpleView.class);
		}
		if (!views.containsKey("text/html")) {
			log.debug("Adding SimpleView for {}.", "text/html");
			views.put("text/html", SimpleView.class);
		}

		// add json view in case user didn't declare any but put a json lib on
		// classpath
		if (!views.containsKey("application/json") && !isJsonBound) {
			Class<? extends JsonView> jsonView = new JsonViewFinder().find();
			if (jsonView != null) {
				log.debug("Adding {} as an auto configured json view for {}.", jsonView, "application/json");
				views.put("application/json", jsonView);
			}
		}
	}

}
