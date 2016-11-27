package it.espr.mvc;

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
import it.espr.mvc.config.RouteConfig;
import it.espr.mvc.config.ViewConfig;
import it.espr.mvc.view.JsonView;
import it.espr.mvc.view.SimpleView;
import it.espr.mvc.view.View;

public abstract class Configuration extends it.espr.injector.Configuration {

	private static final Pattern PATH_VARIABLE_PATTERN = Pattern.compile("\\((.*?)\\)");

	public static final String DEFAULT_MATCHER = "[-_0-9a-zA-Z]+";

	private static final Logger log = LoggerFactory.getLogger(Configuration.class);

	List<RouteConfig> routesConfiguration = new ArrayList<>();

	List<ViewConfig> defaultViewConfiguration = new ArrayList<>();

	List<ViewConfig> viewConfiguration = new ArrayList<>();

	Map<String, Route> routes = new LinkedHashMap<>();

	Map<String, View> views = new LinkedHashMap<>();

	public Configuration() {
		super();
		this.view(defaultViewConfiguration, null, "text/plain", "text/html").with(new SimpleView());
		this.view(defaultViewConfiguration, "application/json").with(new JsonView());
	}

	public RouteConfig route() {
		RouteConfig routeConfig = new RouteConfig();
		this.routesConfiguration.add(routeConfig);
		return routeConfig;
	}

	public ViewConfig view(String... accept) {
		return view(this.viewConfiguration, accept);
	}

	public ViewConfig view(List<ViewConfig> viewConfiguration, String... accept) {
		ViewConfig viewConfig = new ViewConfig(accept);
		viewConfiguration.add(viewConfig);
		return viewConfig;
	}

	private void addRoute(String path, String requestType, Class<?> model, String method, List<String> parameters) {
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
			int index = pathVariables == null ? 0 : pathVariables.size();

			Class<?>[] methodParameters = m.getParameterTypes();
			if (methodParameters != null && methodParameters.length > 0) {
				params = new LinkedHashMap<>();
				for (int i = index; i < methodParameters.length; i++) {
					params.put(parameters.get(i - index), methodParameters[i]);
				}
			}
		} catch (Exception e) {
			log.error("Problem when configuring route: {} {} {} {} {}", path, requestType, model, method, parameters, e);
			throw new RuntimeException("Problem when configuring route '" + requestType + " " + path + " " + model + " " + method + " " + parameters, e);
		}

		pathVariables = pathVariables.size() == 0 ? null : pathVariables;
		Route route = new Route(Pattern.compile(path + "(?:$|\\?.*)"), requestType, model, m, pathVariables, params);
		String key = requestType + " | " + path;
		routes.put(key, route);
	}

	protected final void configure() {
		for (RouteConfig routeConfig : routesConfiguration) {
			for (String requestType : routeConfig.getRequestTypes()) {
				this.addRoute(routeConfig.getUri(), requestType, routeConfig.getClazz(), routeConfig.getMethod(), routeConfig.getParameters());
			}
		}
		for (ViewConfig viewConfig : viewConfiguration) {
			if (viewConfig.getClazz().isAvailable()) {
				for (String accept : viewConfig.getAccept()) {
					this.views.put(accept, viewConfig.getClazz());
				}
			}
		}
		for (ViewConfig viewConfig : defaultViewConfiguration) {
			if (viewConfig.getClazz().isAvailable()) {
				for (String accept : viewConfig.getAccept()) {
					if (!this.views.containsKey(accept)) {
						this.views.put(accept, viewConfig.getClazz());
					}
				}
			}
		}
	}

	protected abstract void configureMvc();

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
