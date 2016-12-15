package it.espr.mvc.view;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.espr.mvc.view.json.Json;

public class JsonView implements View {

	private static final Logger log = LoggerFactory.getLogger(JsonView.class);

	private static final Map<String, String> jsonViews = new LinkedHashMap<>();

	{
		jsonViews.put("com.fasterxml.jackson.databind.ObjectMapper", "it.espr.mvc.view.json.Jackson");
		jsonViews.put("com.google.gson.Gson", "it.espr.mvc.view.json.Gson");
	}

	private Json json;

	public JsonView() {
		this(null);
	}

	public JsonView(Json json) {
		this.json = json;
	}

	@Override
	public void view(HttpServletResponse response, Object data) {
		if (data != null) {
			try {
				response.addHeader("Access-Control-Allow-Origin", "*");
				response.addHeader("Access-Control-Allow-Credentials", "true");
				response.addHeader("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT, OPTIONS, HEAD");
				response.addHeader("Access-Control-Allow-Headers", "Content-Type, Accept, X-Requested-With");
				response.getWriter().write(this.json.serialise(data));
			} catch (Exception e) {
				log.error("Problem when writing json output with {}", json, e);
			}
		}
	}

	@Override
	public boolean isAvailable() {
		if (this.json == null) {
			log.debug("No JSON view registered in config - trying to load some of the default implementations");
			for (Entry<String, String> jsonView : jsonViews.entrySet()) {
				try {
					log.debug("looking for json view {}", jsonView.getKey());
					Class<?> c = (Class<?>) Class.forName(jsonView.getKey(), false, this.getClass().getClassLoader());
					if (c != null) {
						this.json = (Json) Class.forName(jsonView.getValue()).newInstance();
					}
					log.debug("Found and initialised json view {}", jsonView.getKey());
					break;
				} catch (Exception e) {
					log.debug("Couldn't find a {} json impl on classpath", jsonView.getKey(), e);
				}
			}
		}

		if (this.json == null) {
			log.info("No JSON view registered and none of default implementations found on class path - JSON view is not available.");
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "" + this.json;
	}

}