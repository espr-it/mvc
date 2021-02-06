package it.espr.mvc.route;

public class StaticResourcesRoute {

	public static final String BASE_DIR = "/static/";

	public it.espr.mvc.view.ForwardView.Forward get(String path) {
		it.espr.mvc.view.ForwardView.Forward forward = new it.espr.mvc.view.ForwardView.Forward();
		forward.path = BASE_DIR;
		if (path == null || "".equals(path)) {
			forward.path += "index.html";
		} else {
			forward.path += path;
		}
		return forward;
	}
}
