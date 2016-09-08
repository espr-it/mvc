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

import it.espr.injector.Utils.Pair;
import it.espr.mvc.view.JsonView;
import it.espr.mvc.view.SimpleView;
import it.espr.mvc.view.View;

public abstract class Configuration extends it.espr.injector.Configuration {

	private static final Pattern PATH_VARIABLE_PATTERN = Pattern.compile("\\((.*?)\\)");

	public static final String DEFAULT_MATCHER = "[-_0-9a-zA-Z]+";

	private static final Logger log = LoggerFactory.getLogger(Configuration.class);

	Map<String, Route> routes = new LinkedHashMap<>();

	Map<String, View> views = new LinkedHashMap<>();

	public void addRoute(String path, Class<?> model, String method) {
		this.addRoute(path, "get", model, method, null);
	}

	public void addRoute(String path, String requestType, Class<?> model, String method, String[] parameters) {
		Method m = null;
		List<Pair<String, Class<?>>> pathVariables = new ArrayList<>();
		Map<String, Class<?>> params = null;
		try {
			Method[] methods = model.getMethods();
			for (Method candidate : methods) {
				if (utils.isPublic(candidate) && candidate.getName().equals(method) && candidate.getParameterTypes().length >= (parameters == null ? 0 : parameters.length)) {
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
					params.put(parameters[i - index], methodParameters[i]);
				}
			}
		} catch (Exception e) {
			log.error("Problem when configuring route: {} {} {} {} {}", path, requestType, model, method, parameters);
			throw new RuntimeException("Problem when configuring route '" + requestType + " " + path + " " + model + " " + method + " " + parameters);
		}

		pathVariables = pathVariables.size() == 0 ? null : pathVariables;
		Route route = new Route(Pattern.compile(path + "(?:$|\\?.*)"), requestType, model, m, pathVariables, params);
		String key = requestType + " | " + path;
		routes.put(key, route);
	}

	public void registerView(View view) {
		if (view.isAvailable()) {
			for (String type : view.getTypes()) {
				this.views.put(type, view);
			}
		}
	}

	final void configure() {
		this.configureRoutes();

		this.configureDefaultViews();
		this.configureViews();
	}

	protected abstract void configureRoutes();

	void configureDefaultViews() {
		this.registerView(new SimpleView());
		this.registerView(new JsonView());
	}

	protected void configureViews() {
		// override if you want to add custom views
	}

	private String parsePathVariables(List<Pair<String, Class<?>>> pathVariables, String path) {
		Matcher matcher = PATH_VARIABLE_PATTERN.matcher(path);
		String newPath = new String(path);
		while (matcher.find()) {
			String group = matcher.group(1);
			String[] items = group.split(":");
			String replacement = DEFAULT_MATCHER;
			String variable = items[1];
			if (!utils.isEmpty(items[0])) {
				replacement = items[0];
			}
			pathVariables.add(new Pair<String, Class<?>>(variable, String.class));
			newPath = newPath.replaceFirst(Pattern.quote(group), replacement);
		}
		return newPath;
	}
}
