package it.espr.mvc;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.espr.mvc.response.Forward;
import it.espr.mvc.response.Forwarder;
import it.espr.mvc.response.Redirect;
import it.espr.mvc.response.Redirector;
import it.espr.mvc.route.Route;
import it.espr.mvc.view.JspView;
import it.espr.mvc.view.View;

public class ViewResolver {

	private final Logger log = LoggerFactory.getLogger(ViewResolver.class);

	private JspView jspView;

	private Map<String, View> views;

	private Redirector redirector;

	private Forwarder forwarder;

	public ViewResolver(JspView jspView, @Named("MvcViews") Map<String, View> views, Redirector redirector, Forwarder forwarder) {
		super();
		this.jspView = jspView;
		this.views = views;
		this.redirector = redirector;
		this.forwarder = forwarder;
	}

	public void resolve(HttpServletRequest request, HttpServletResponse response, Route route, Object data) {
		if (data instanceof Redirect) {
			this.redirector.redirect(response, (Redirect) data);
			return;
		}

		if (data instanceof Forward) {
			this.forwarder.forward(request, response, (Forward) data);
			return;
		}

		if (route.view != null && route.view.endsWith(".jsp")) {
			jspView.view(request, response, route, data);
			return;
		}

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
		accept.add(null);
		return accept;
	}
}
