package it.espr.mvc.view;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import it.espr.mvc.route.Route;

public class ErrorView implements View {

	public static class Error {
		public String message;

		public Error(String message) {
			super();
			this.message = message;
		}
	}

	@Override
	public void view(HttpServletRequest request, HttpServletResponse response, Route route, Object data) {
		response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		data = new Error(((Exception) data).getMessage());
	}
}
