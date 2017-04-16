package it.espr.mvc.view.json;

import javax.inject.Singleton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class GsonView extends JsonView {

	private static Logger log = LoggerFactory.getLogger(GsonView.class);

	private com.google.gson.Gson gson;

	public GsonView(com.google.gson.Gson gson) {
		this.gson = gson;
	}

	@Override
	public String toString() {
		return "Gson JsonView";
	}

	@Override
	protected String out(Object data) throws Exception {
		log.debug("Serialising");
		String json = this.gson.toJson(data);
		log.debug("Serialised");
		return json;
	}
}
