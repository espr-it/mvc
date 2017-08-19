package it.espr.mvc.route;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.espr.mvc.Pair;

public class Router {

	private static final Logger log = LoggerFactory.getLogger(Router.class);

	private List<Route> routes;

	private Map<String, Pair<Route, List<String>>> cache;

	public Router(@Named("MvcRoutes") List<Route> routes) {
		this.routes = routes;
		this.cache = new HashMap<>();
	}

	public Pair<Route, List<String>> route(String uri, String requestType) {
		String cacheKey = requestType + " " + uri;
		log.debug("Looking up route for {}", cacheKey);

		if (this.cache.containsKey(cacheKey)) {
			log.debug("Found a route in cache for {}", cacheKey);
			return this.cache.get(cacheKey);
		}

		log.debug("Looping over {} available routes ", cacheKey);
		Route route = null;
		List<String> pathVariables = new ArrayList<>();
		for (Route candidate : routes) {
			if (!candidate.requestType.equals(requestType)) {
				continue;
			}
			Matcher m = candidate.path.matcher(uri);
			if (m.matches()) {
				route = candidate;
				log.debug("Found route {} for {}", route, cacheKey);
				for (int i = 1; i <= m.groupCount(); i++) {
					pathVariables.add(m.group(i));
				}
				break;
			}
		}
		log.debug("Caching route {} for {}", route, cacheKey);

		Pair<Route, List<String>> pair = null;
		if (route != null) {
			pair = new Pair<Route, List<String>>(route, pathVariables);
		}
		this.cache.put(cacheKey, pair);

		return pair;
	}
}
