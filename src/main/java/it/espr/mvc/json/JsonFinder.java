package it.espr.mvc.json;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JsonFinder {

	private static final Logger log = LoggerFactory.getLogger(JsonFinder.class);

	private static final Map<String, Class<? extends Json>> jsonImpls = new LinkedHashMap<>();

	{
		jsonImpls.put("com.fasterxml.jackson.databind.ObjectMapper", JacksonImpl.class);
		jsonImpls.put("com.google.gson.Gson", GsonImpl.class);
	}

	public Class<? extends Json> find() {
		for (Entry<String, Class<? extends Json>> jsonView : jsonImpls.entrySet()) {
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
		log.info("Couldn't find any of {} json libraries on classpath.", jsonImpls.keySet());
		return null;
	}
}
