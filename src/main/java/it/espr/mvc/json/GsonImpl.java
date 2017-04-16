package it.espr.mvc.json;

import javax.inject.Singleton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

@Singleton
public class GsonImpl implements Json {

	private static final Logger log = LoggerFactory.getLogger(GsonImpl.class);
	private Gson gson;

	public GsonImpl(Gson gson) {
		super();
		log.debug("Initializing gson");
		this.gson = gson;
	}

	@Override
	public <Type> Type deserialise(Class<Type> type, String string) {
		log.debug("Deserialising into {}", type);
		Type instance = gson.fromJson(string, type);
		log.debug("Deserialised into {}", type);
		return instance;
	}

	@Override
	public String serialise(Object object) {
		log.debug("Serialising");
		String json = gson.toJson(object);
		log.debug("Serialised");
		return json;
	}

}
