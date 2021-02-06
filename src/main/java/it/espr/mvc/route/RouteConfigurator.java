package it.espr.mvc.route;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.espr.injector.Utils;
import it.espr.mvc.controller.DefaultController;
import it.espr.mvc.route.parameter.Parameter;
import it.espr.mvc.route.parameter.PathVariable;
import it.espr.mvc.view.View;

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
				Route route = this.configureRoute(routeConfig.getUri(), requestType, routeConfig.getClazz(),
						routeConfig.getMethod(), routeConfig.getParameters(), routeConfig.getView());
				routes.put(routeConfig, route);
			}
		}

		List<Route> list = new ArrayList<>();
		Route defaultRoute = null;
		for (Route route : routes.values()) {
			if (route.model.isAssignableFrom(DefaultController.class)) {
				defaultRoute = route;
				continue;
			}
			list.add(route);
		}

		if (defaultRoute != null)
			list.add(defaultRoute);
		return list;
	}

	private Route configureRoute(String path, String requestType, Class<?> controller, String method,
			List<? extends Parameter> parameters, Class<? extends View> view) {

		List<Method> candidates = new ArrayList<>();
		List<String> pathVariables = new ArrayList<>();
		List<Parameter> params = null;
		try {
			List<Method> methods = this.getSuitableMethods(controller.getMethods());

			for (Method candidate : methods) {
				if (candidate.getName().equals(method) && areParametersMatching(candidate, parameters)) {
					try {
						path = this.parsePathVariables(pathVariables, path);
						int pathVariablesSize = pathVariables.size();

						Class<?>[] methodParameters = candidate.getParameterTypes();
						if (methodParameters != null && methodParameters.length > 0) {
							params = new ArrayList<>();
							for (int i = 0; i < methodParameters.length; i++) {
								if (pathVariablesSize > i) {
									params.add(new PathVariable(pathVariables.get(i), methodParameters[i]));
								} else {
									Parameter parameter = parameters.get(i - pathVariablesSize);
									parameter.cls = methodParameters[i];
									params.add(parameter);
								}
							}
						}

						candidates.add(candidate);
					} catch (Exception e) {
						log.debug(
								"Found a candidate route method {} with non-matching number/typo of parameters: {}, skipping",
								method, candidate.getParameterTypes().length);
					}
				}
			}

			// if could find candidate method, check if model has one method only which
			// matches parameters and use it
			if (candidates.size() == 0) {
				methods = this.getSuitableMethods(controller.getDeclaredMethods());
				if (methods.size() == 1) {
					Method singleDeclaredMethod = methods.get(0);
					if (Utils.isPublic(singleDeclaredMethod)
							&& areParametersMatching(singleDeclaredMethod, parameters)) {
						candidates.add(singleDeclaredMethod);
					}
				}
			}

			if (candidates.size() != 1) {
				throw new Exception("Couldn't find a route - found " + candidates.size() + " candidates!");
			}

		} catch (Exception e) {
			log.error("Problem when configuring route: {} {} {} {} {}", path, requestType, controller, method,
					parameters, e);
			throw new RuntimeException("Problem when configuring route '" + requestType + " " + path + " " + controller
					+ " " + method + " " + parameters, e);
		}

		return new Route(Pattern.compile(path + "(?:$|\\?.*)"), requestType, controller, candidates.get(0), params,
				view);
	}

	private List<Method> getSuitableMethods(Method[] methods) {
		List<Method> filtered = new ArrayList<>();

		for (Method m : methods) {
			if (Utils.isPublic(m) && !Modifier.isStatic(m.getModifiers()) && !m.isSynthetic()) {
				filtered.add(m);
			}
		}

		return filtered;
	}

	private boolean areParametersMatching(Method method, List<? extends Parameter> parameters) {
		return method.getParameterTypes().length >= (parameters == null ? 0 : parameters.size());
	}

	private String parsePathVariables(List<String> pathVariables, String path) {
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
			pathVariables.add(variable);
			newPath = newPath.replaceFirst(Pattern.quote(group), replacement);
		}
		return newPath;
	}

}
