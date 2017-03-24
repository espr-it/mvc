package it.espr.mvc.view;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import it.espr.mvc.route.Route;

public class SimpleView implements View {

	@Override
	public void view(HttpServletRequest request, HttpServletResponse response, Route route, Object data) {
		if (data != null) {
			try {
				response.getWriter().write(data.toString());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
