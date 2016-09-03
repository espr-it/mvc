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
				if (utils.isPublic(candidate) && candidate.getName().equals(method) && candidate.getParameterCount() >= (parameters == null ? 0 : parameters.length)) {
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

	public void addView(String type, View view) {
		this.views.put(type, view);
	}

	final void configure() {
		this.configureRoutes();
		this.configureViews();
	}

	protected abstract void configureRoutes();

	protected void configureViews() {
		this.addView(null, new SimpleView());
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

	private void OldThing(Map<String, Class<?>> pathVariables, String path) {
		// Pattern pattern = Pattern.compile(".*\\((.*)\\).*");
		Pattern pattern = Pattern.compile("(\\([^\\(]+\\))");
		Matcher matcher = pattern.matcher(path);
		if (matcher.matches()) {
			pathVariables = new LinkedHashMap<>();
			for (int i = 0; i < matcher.groupCount(); i++) {
				String group = matcher.group(i);
				String[] items = group.split(":");
				String replacement = "(.*)";
				String variable = items[0];
				if (items.length > 1) {
					replacement = items[0];
					variable = items[1];
				}
				pathVariables.put(variable, String.class);
				path = path.replaceFirst(group, replacement);
			}
		}
	}
}
