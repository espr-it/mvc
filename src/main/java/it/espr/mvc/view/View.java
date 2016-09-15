package it.espr.mvc.view;

import javax.servlet.http.HttpServletResponse;

public interface View {

	public void view(HttpServletResponse response, Object data);

	boolean isAvailable();
}
