package it.espr.mvc.route;

import java.lang.reflect.Method;
import java.util.List;
import java.util.regex.Pattern;

import it.espr.mvc.route.parameter.Parameter;

public class Route {

	public final Pattern path;
	public final String requestType;
	public final Class<?> model;
	public final Method method;
	public final List<? extends Parameter> parameters;
	public final String view;

	public Route(Pattern path, String requestType, Class<?> model, Method method, List<? extends Parameter> parameters, String view) {
		super();
		this.path = path;
		this.requestType = requestType;
		this.model = model;
		this.method = method;
		this.parameters = parameters;
		this.view = view;
	}

	public String toString() {
		return this.requestType + " " + this.path + ": " + this.getClass().getCanonicalName() + "." + this.method + "(" + this.parameters + ")";
	}
}