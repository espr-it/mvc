package it.espr.mvc;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.espr.mvc.view.View;

public class ViewResolver {

	private final Logger log = LoggerFactory.getLogger(ViewResolver.class);

	private Map<String, View> views;

	public ViewResolver(@Named("MvcViews") Map<String, View> views) {
		super();
		this.views = views;
	}

	public void resolve(HttpServletRequest request, HttpServletResponse response, Object data) {
		List<String> accepts = this.getAccept(request.getHeader("accept"));
		View view = null;
		for (String accept : accepts) {
			if (views.containsKey(accept)) {
				view = this.views.get(accept);
				log.debug("Using '{}' view implementation for '{}' (supplied accept parameters: {})", view, accept, accepts);
				break;
			}
		}

		if (view == null) {
			log.error("Couldn't find any view for {}! Have you removed default view?!");
			return;
		}

		view.view(response, data);
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
		accept.add(null);
		return accept;
	}
}
