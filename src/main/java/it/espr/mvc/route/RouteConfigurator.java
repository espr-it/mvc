package it.espr.mvc.route;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.espr.injector.Utils;
import it.espr.mvc.Pair;

public class RouteConfigurator {

	private static final Logger log = LoggerFactory.getLogger(RouteConfigurator.class);

	private static final Pattern PATH_VARIABLE_PATTERN = Pattern.compile("\\((.*?)\\)");

	public static final String DEFAULT_MATCHER = "[-_0-9a-zA-Z ]+";

	private List<RouteConfig> routesConfiguration = new ArrayList<>();

	private Map<RouteConfig, Route> routes = new LinkedHashMap<>();

	public Route findRoute(RouteConfig routeConfig) {
		return routes.get(routeConfig);
	}

	public RouteConfig route() {
		RouteConfig routeConfig = new RouteConfig();
		this.routesConfiguration.add(routeConfig);
		return routeConfig;
	}

	public List<Route> configure() {
		for (RouteConfig routeConfig : routesConfiguration) {
			for (String requestType : routeConfig.getRequestTypes()) {
				Route route = this.configureRoute(routeConfig.getUri(), requestType, routeConfig.getClazz(), routeConfig.getMethod(), routeConfig.getParameters());
				routes.put(routeConfig, route);
			}
		}
		return new ArrayList<>(routes.values());
	}

	private Route configureRoute(String path, String requestType, Class<?> model, String method, List<String> parameters) {

		Method m = null;
		List<Pair<String, Class<?>>> pathVariables = new ArrayList<>();
		Map<String, Class<?>> params = null;
		try {
			Method[] methods = model.getMethods();
			for (Method candidate : methods) {
				if (Utils.isPublic(candidate) && candidate.getName().equals(method) && candidate.getParameterTypes().length >= (parameters == null ? 0 : parameters.size())) {
					m = candidate;
					break;
				}
			}
			if (m == null) {
				throw new Exception("Couldn't find a route");
			}

			path = this.parsePathVariables(pathVariables, path);
			int pathVariablesSize = pathVariables.size();

			Class<?>[] methodParameters = m.getParameterTypes();
			if (methodParameters != null && methodParameters.length > 0) {
				params = new LinkedHashMap<>();
				for (int i = 0; i < methodParameters.length; i++) {
					if (pathVariablesSize > i) {
						params.put(pathVariables.get(i).p1, methodParameters[i]);
					} else {
						params.put(parameters.get(i - pathVariablesSize), methodParameters[i]);
					}
				}
			}
		} catch (Exception e) {
			log.error("Problem when configuring route: {} {} {} {} {}", path, requestType, model, method, parameters, e);
			throw new RuntimeException("Problem when configuring route '" + requestType + " " + path + " " + model + " " + method + " " + parameters, e);
		}

		pathVariables = pathVariables.size() == 0 ? null : pathVariables;
		return new Route(Pattern.compile(path + "(?:$|\\?.*)"), requestType, model, m, pathVariables, params);
	}

	private String parsePathVariables(List<Pair<String, Class<?>>> pathVariables, String path) {
		Matcher matcher = PATH_VARIABLE_PATTERN.matcher(path);
		String newPath = new String(path);
		while (matcher.find()) {
			String group = matcher.group(1);
			String[] items = group.split(":");
			String replacement = DEFAULT_MATCHER;
			String variable = items[1];
			if (!Utils.isEmpty(items[0])) {
				replacement = items[0];
			}
			pathVariables.add(new Pair<String, Class<?>>(variable, String.class));
			newPath = newPath.replaceFirst(Pattern.quote(group), replacement);
		}
		return newPath;
	}

}
