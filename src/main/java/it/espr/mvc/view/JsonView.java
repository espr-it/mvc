package it.espr.mvc.view;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.espr.mvc.json.Json;
import it.espr.mvc.route.Route;

@Singleton
public class JsonView implements View {

	private static final Logger log = LoggerFactory.getLogger(JsonView.class);

	@Inject
	private Json json;

	@Override
	public void view(HttpServletRequest request, HttpServletResponse response, Route route, Object data) {
		response.setCharacterEncoding("UTF-8");
		response.setContentType("application/json");
		response.addHeader("Content-type", "application/json; charset=utf-8");
		if (data != null) {
			try {
				response.getWriter().write(this.json.serialise(data));
			} catch (Exception e) {
				log.error("Problem when writing json output with", e);
			}
		}
	}
}
