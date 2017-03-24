package it.espr.mvc.response;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Forwarder {

	private static Logger log = LoggerFactory.getLogger(Forwarder.class);

	public void forward(HttpServletRequest request, HttpServletResponse response, Forward forward) {
		String path = forward.path;
		String query = request.getQueryString();
		if (query != null && !"".equals(query)) {
			path += "?" + query;
		}
		log.debug("Forwarding to {}", path);
		try {
			request.getRequestDispatcher(path).forward(request, response);
			;
		} catch (Exception exception) {
			log.error("Problem when forwarding to {}", forward.path, exception);
		}
	}
}
