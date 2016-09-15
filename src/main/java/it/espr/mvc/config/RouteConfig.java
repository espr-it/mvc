package it.espr.mvc.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RouteConfig {

	public class RequestType {

		private Controller controller;

		RequestType(Controller controller) {
			this.controller = controller;
		}

		public Controller to(Class<?> clazz) {
			return this.to(clazz, null);
		}

		public Controller to(Class<?> clazz, String method) {
			this.controller.clazz = clazz;
			this.controller.method = method;
			return this.controller;
		}
	}

	public class Controller {

		Class<?> clazz;

		String method;

		List<String> parameters;

		public void with(String... parameters) {
			if (parameters != null && parameters.length > 0) {
				this.parameters = new ArrayList<>(Arrays.asList(parameters));
			}
		}
	}

	private String uri;

	private String[] requestTypes;

	private Controller controller;

	public RequestType get(String uri) {
		return this.requestType(uri, "get");
	}

	public RequestType post(String uri) {
		return this.requestType(uri, "post");
	}

	public RequestType all(String uri) {
		return this.requestType(uri, "get", "post");
	}

	private RequestType requestType(String uri, String... types) {
		this.uri = uri;
		if (types == null || types.length == 0) {
			requestTypes = new String[] { "get" };
		} else {
			requestTypes = types.clone();
		}
		controller = new Controller();
		return new RequestType(this.controller);
	}

	public String getUri() {
		return uri;
	}

	public String[] getRequestTypes() {
		return requestTypes;
	}

	public List<String> getParameters() {
		return this.controller.parameters;
	}

	public String getMethod() {
		return this.controller.method;
	}

	public Class<?> getClazz() {
		return this.controller.clazz;
	}

}
