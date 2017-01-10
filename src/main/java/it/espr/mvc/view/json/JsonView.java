package it.espr.mvc.view.json;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.espr.mvc.view.View;

public abstract class JsonView implements View {

	private static final Logger log = LoggerFactory.getLogger(JsonView.class);

	@Override
	public void view(HttpServletResponse response, Object data) {
		if (data != null) {
			try {
				response.getWriter().write(this.out(data));
			} catch (Exception e) {
				log.error("Problem when writing json output with", e);
			}
		}
	}

	protected abstract String out(Object data) throws Exception;
}