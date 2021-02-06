package it.espr.mvc;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.espr.mvc.route.Route;
import it.espr.mvc.view.View;

public class ViewResolver {

	private final Logger log = LoggerFactory.getLogger(ViewResolver.class);

	private Map<Object, View> views;

	public ViewResolver(@Named("MvcViews") Map<Object, View> views) {
		this.views = views;
	}

	public void resolve(HttpServletRequest request, HttpServletResponse response, Route route, Object data) {
		View view = null;
		
		if (data != null) {
			view = this.views.get(data.getClass());	
		}

		if (view == null && route.view != null) {
			view = this.views.get(route.view);
		}

		if (view == null) {
			List<String> accepts = this.getAccept(request.getHeader("accept"));
			for (String accept : accepts) {
				if (views.containsKey(accept)) {
					view = this.views.get(accept);
					log.debug("Using '{}' view for accept parameter '{} ({})''", view.getClass(), accept, accepts);
					break;
				}
			}
		}

		if (view == null) {
			view = this.views.get(null);
			log.debug("Falling back to default view: {}", view.getClass());
		}

		view.view(request, response, route, data);
	}

	public List<String> getAccept(String header) {
		List<String> accept = new ArrayList<>();
		if (header != null && !"".equals(header.trim())) {
			try {
				String[] tokens = header.split(";")[0].split(",");
				for (String token : tokens) {
					accept.add(token.trim());
				}
			} catch (Exception e) {
				log.error("Problem when parsing accept headers", e);
			}
		}
		return accept;
	}
}
