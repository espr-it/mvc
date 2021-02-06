package it.espr.mvc.view;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.espr.mvc.route.Route;

public class StringView implements View {

	private static final Logger log = LoggerFactory.getLogger(StringView.class);
	
	@Override
	public void view(HttpServletRequest request, HttpServletResponse response, Route route, Object data) {
		if (data != null) {
			try {
				response.getWriter().write(data.toString());
			} catch (IOException e) {
				log.error("Problem when generating string view for route {}", route);
			}
		}
	}
}
