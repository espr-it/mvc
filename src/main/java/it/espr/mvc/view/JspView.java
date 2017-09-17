package it.espr.mvc.view;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.espr.mvc.route.Route;

public class JspView implements View {

	private static final Logger log = LoggerFactory.getLogger(JspView.class);

	@Override
	public void view(HttpServletRequest request, HttpServletResponse response, Route route, Object data) {
		try {
			request.setAttribute("model", data);
			request.getRequestDispatcher(route.view).forward(request, response);
		} catch (Exception e) {
			log.error("Problem when forwarding request to static html {}", route.view, e);
		}
	}
}
