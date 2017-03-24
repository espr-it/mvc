package it.espr.mvc.route;

import it.espr.mvc.response.Forward;

public class StaticResourcesRoute {

	public static final String BASE_DIR = "/static/";

	public Forward get(String path) {
		Forward forward = new Forward();
		forward.path = BASE_DIR;
		if (path == null || "".equals(path)) {
			forward.path += "index.html";
		} else {
			forward.path += path;
		}
		return forward;
	}
}
