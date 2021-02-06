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
			String path = "jsp/" + request.getRequestURI() + ".jsp";
			request.setAttribute("model", data);
			request.getRequestDispatcher(path).forward(request, response);
		} catch (Exception e) {
			log.error("Problem when forwarding request jsp {}", route.view, e);
		}
	}
}
