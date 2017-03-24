package it.espr.mvc.view;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import it.espr.mvc.route.Route;

public interface View {

	public void view(HttpServletRequest request, HttpServletResponse response, Route route, Object data);
}
