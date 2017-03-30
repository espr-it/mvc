package it.espr.mvc.route;

import java.util.HashMap;
import java.util.LinkedHashMap;
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

	private Map<String, Pair<Route, Map<String, Object>>> cache;

	public Router(@Named("MvcRoutes") List<Route> routes) {
		this.routes = routes;
		this.cache = new HashMap<>();
	}

	public Pair<Route, Map<String, Object>> route(String uri, String requestType) {
		String cacheKey = requestType + " " + uri;
		log.debug("Looking up route for {}", cacheKey);

		if (this.cache.containsKey(cacheKey)) {
			log.debug("Found a route in cache for {}", cacheKey);
			return this.cache.get(cacheKey);
		}

		log.debug("Looping over {} available routes ", cacheKey);
		Route route = null;
		Map<String, Object> pathVariables = null;
		for (Route candidate : routes) {
			if (!candidate.requestType.equals(requestType)) {
				continue;
			}
			Matcher m = candidate.path.matcher(uri);
			if (m.matches()) {
				route = candidate;
				log.debug("Found route {} for {}", route, cacheKey);
				pathVariables = new LinkedHashMap<>();
				for (int i = 1; i <= m.groupCount(); i++) {
					pathVariables.put(route.pathVariables.get(i - 1).p1, m.group(i));
				}
				break;
			}
		}
		log.debug("Caching route {} for {}", route, cacheKey);

		Pair<Route, Map<String, Object>> pair = null;
		if (route != null) {
			pair = new Pair<Route, Map<String, Object>>(route, pathVariables);
		}
		this.cache.put(cacheKey, pair);

		return pair;
	}
}