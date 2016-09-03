package it.espr.mvc;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import it.espr.mvc.view.View;

public class ViewResolver {

	private Map<String, View> views;

	public ViewResolver(Map<String, View> views) {
		super();
		this.views = views;
	}

	public void resolve(HttpServletRequest request, HttpServletResponse response, Object data) {
		this.views.get(null).view(response, data);
	}
}
