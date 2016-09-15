package it.espr.mvc.view;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.espr.mvc.view.json.Json;

public class JsonView implements View {

	private static final Logger log = LoggerFactory.getLogger(JsonView.class);

	private Json json;

	private Object implemention;

	public JsonView() {
		this(null);
	}

	public JsonView(Object implementation) {
		this.implemention = implementation;
	}

	@Override
	public void view(HttpServletResponse response, Object data) {
		if (data != null) {
			try {
				response.getWriter().write(this.json.serialise(data));
			} catch (Exception e) {
				log.error("Problem when writing json output", e);
			}
		}
	}

	@Override
	public boolean isAvailable() {
		if (this.implemention == null) {
			try {
				Class<?> c = (Class<?>) Class.forName("com.fasterxml.jackson.databind.ObjectMapper", false, this.getClass().getClassLoader());
				if (c != null) {
					this.json = (Json) Class.forName("it.espr.mvc.view.json.Jackson").newInstance();
				}
			} catch (Exception e) {
				log.debug("Can't find default json implementation on classpath", e);
			}
		}

		if (this.json == null) {
			log.debug("Couldn't load json implementation, JSON view is not available");
			return false;
		}
		return true;
	}
}
