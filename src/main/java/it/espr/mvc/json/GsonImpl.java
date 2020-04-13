package it.espr.mvc.json;

import javax.inject.Named;
import javax.inject.Singleton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

@Singleton
public class GsonImpl implements Json {

	private static final Logger log = LoggerFactory.getLogger(GsonImpl.class);

	private GsonBuilder gsonBuilder;

	private Gson gson;

	public GsonImpl(@Named("view") GsonBuilder gsonBuilder) {
		this.gsonBuilder = gsonBuilder;
	}

	Gson getGson() {
		if (this.gson == null) {
			log.debug("Initializing gson");
			
			// default settings
			this.gsonBuilder = this.gsonBuilder.disableHtmlEscaping();
			this.gsonBuilder = this.gsonBuilder.setDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz");
			
			this.gson = this.gsonBuilder.create();
		}
		return this.gson;
	}

	@Override
	public <Type> Type deserialise(Class<Type> type, String string) {
		log.debug("Deserialising into {}", type);
		Type instance = this.getGson().fromJson(string, type);
		log.debug("Deserialised into {}", type);
		return instance;
	}

	@Override
	public String serialise(Object object) {
		log.debug("Serialising");
		String json = this.getGson().toJson(object);
		log.debug("Serialised");
		return json;
	}

}
