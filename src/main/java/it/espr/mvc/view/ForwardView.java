package it.espr.mvc.view;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.espr.mvc.route.Route;

public class ForwardView implements View {

	private static Logger log = LoggerFactory.getLogger(ForwardView.class);

	public static class Forward {

		public String path;
	}

	public void forward(HttpServletRequest request, HttpServletResponse response, Forward forward) {
		String path = forward.path;
		String query = request.getQueryString();
		if (query != null && !"".equals(query)) {
			path += "?" + query;
		}
		log.debug("Forwarding to {}", path);
		try {
			request.getRequestDispatcher(path).forward(request, response);
		} catch (Exception exception) {
			log.error("Problem when forwarding to {}", forward.path, exception);
		}
	}

	@Override
	public void view(HttpServletRequest request, HttpServletResponse response, Route route, Object data) {
		this.forward(request, response, (Forward) data);
	}
}
