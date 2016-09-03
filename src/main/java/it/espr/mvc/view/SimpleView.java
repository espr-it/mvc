package it.espr.mvc.view;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

public class SimpleView implements View {

	@Override
	public void view(HttpServletResponse response, Object data) {
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
