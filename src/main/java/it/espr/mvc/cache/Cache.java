package it.espr.mvc.cache;

import it.espr.mvc.route.Route;

public interface Cache {

	void put(String requestType, String uri, Route route, Object data);

	Object get(String requestType, String uri, Route route);
}
