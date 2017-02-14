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
import it.espr.mvc.converter.StringToBooleanConverter;
import it.espr.mvc.converter.StringToDoubleConverter;
import it.espr.mvc.converter.StringToIntegerConverter;
import it.espr.mvc.converter.StringToObjectConverter;
import it.espr.mvc.converter.StringToStringConverter;
import it.espr.mvc.converter.StringToTypeConverter;
import it.espr.mvc.json.Json;
import it.espr.mvc.json.JsonFinder;
import it.espr.mvc.view.SimpleView;
import it.espr.mvc.view.View;
import it.espr.mvc.view.json.JsonView;
import it.espr.mvc.view.json.JsonViewFinder;

public abstract class Configuration extends it.espr.injector.Configuration {

	private static final Pattern PATH_VARIABLE_PATTERN = Pattern.compile("\\((.*?)\\)");

	public static final String DEFAULT_MATCHER = "[-_0-9a-zA-Z]+";

	private static final Logger log = LoggerFactory.getLogger(Configuration.class);

	List<RouteConfig> routesConfiguration = new ArrayList<>();

	List<ViewConfig> defaultViewConfiguration = new ArrayList<>();

	List<ViewConfig> viewConfiguration = new ArrayList<>();

	Map<String, Route> routes = new LinkedHashMap<>();

	Map<String, Class<? extends View>> views = new LinkedHashMap<>();

	List<Class<? extends StringToTypeConverter<?>>> converters = new ArrayList<>();

	public RouteConfig route() {
		RouteConfig routeConfig = new RouteConfig();
		this.routesConfiguration.add(routeConfig);
		return routeConfig;
	}

	public ViewConfig view(String... accept) {
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

	protected void addConverter(Class<? extends StringToTypeConverter<?>> converter) {
		
	}
	
	protected final void configure() {
		this.configureMvc();

		this.addConverter(StringToStringConverter.class);
		this.addConverter(StringToBooleanConverter.class);
		this.addConverter(StringToDoubleConverter.class);
		this.addConverter(StringToIntegerConverter.class);
		this.addConverter(StringToObjectConverter.class);
		this.bind(converters).named("StringToTypeConverters");

		for (RouteConfig routeConfig : routesConfiguration) {
			for (String requestType : routeConfig.getRequestTypes()) {
				this.addRoute(routeConfig.getUri(), requestType, routeConfig.getClazz(), routeConfig.getMethod(), routeConfig.getParameters());
			}
		}

		for (ViewConfig viewConfig : viewConfiguration) {
			for (String accept : viewConfig.getAccept()) {
				this.views.put(accept, viewConfig.getClazz());
			}
		}

		this.addDefaultViews();
		if (this.views.containsKey("application/json")) {
			this.bind(JsonView.class).to(this.views.get("application/json"));
		}

		Class<? extends Json> json = new JsonFinder().find();
		if (json != null) {
			this.bind(Json.class).to(json);
		}

		this.bind(routes).named("MvcRoutes");
		this.bind(views).named("MvcViews");
	}

	private final void addDefaultViews() {
		log.debug("Adding default views...");

		// add simple view as a default and text/html option if user didn't
		// declared them
		if (!this.views.containsKey(null)) {
			log.debug("Adding SimpleView as a default view.");
			this.views.put(null, SimpleView.class);
		}
		if (!this.views.containsKey("text/html")) {
			log.debug("Adding SimpleView for {}.", "text/html");
			this.views.put("text/html", SimpleView.class);
		}

		// add json view in case user didn't declare any but put a json lib on
		// classpath
		if (!this.views.containsKey("application/json") && !this.isBound(JsonView.class)) {
			Class<? extends JsonView> jsonView = new JsonViewFinder().find();
			if (jsonView != null) {
				log.debug("Adding {} as an auto configured json view for {}.", jsonView, "application/json");
				this.views.put("application/json", jsonView);
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
