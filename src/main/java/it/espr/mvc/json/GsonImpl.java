package it.espr.mvc.json;

import com.google.gson.Gson;

public class GsonImpl implements Json {

	private Gson gson = new Gson();

	@Override
	public <Type> Type deserialise(Class<Type> type, String string) {
		return gson.fromJson(string, type);
	}

	@Override
	public String serialise(Object object) {
		return gson.toJson(object);
	}

}
