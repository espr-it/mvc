package it.espr.mvc.view;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.espr.mvc.route.Route;

public class JspView implements View {

	private static final Logger log = LoggerFactory.getLogger(JspView.class);

	private RegexpFilter filter;

	public JspView(RegexpFilter filter) {
		super();
		this.filter = filter;
	}

	@Override
	public void view(HttpServletRequest request, HttpServletResponse response, Route route, Object data) {
		String path = "/jsp" + filter.filterRegexpFromPattern(route.path) + "/.jsp";
		try {
			request.setAttribute("data", data);
			request.getRequestDispatcher(path).forward(request, response);
		} catch (Exception e) {
			log.error("Problem when forwarding request to static html {}", path, e);
		}
	}
}
