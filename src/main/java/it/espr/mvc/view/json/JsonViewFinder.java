package it.espr.mvc.view.json;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JsonViewFinder {

	private static final Logger log = LoggerFactory.getLogger(JsonViewFinder.class);

	private static final Map<String, Class<? extends JsonView>> jsonViews = new LinkedHashMap<>();

	{
		jsonViews.put("com.google.gson.Gson", GsonView.class);
		jsonViews.put("com.fasterxml.jackson.databind.ObjectMapper", JacksonView.class);
	}

	public Class<? extends JsonView> find() {
		for (Entry<String, Class<? extends JsonView>> jsonView : jsonViews.entrySet()) {
			try {
				log.debug("Looking for json view {}", jsonView.getKey());
				Class<?> c = (Class<?>) Class.forName(jsonView.getKey(), false, this.getClass().getClassLoader());
				if (c != null) {
					log.debug("Found json view {}", jsonView.getKey());
					return jsonView.getValue();
				}
			} catch (Exception e) {
				log.debug("Couldn't find a {} json impl on classpath", jsonView.getKey(), e);
			}
		}
		log.info("Couldn't find any of {} json libraries on classpath.", jsonViews.keySet());
		return null;
	}
}
