package it.espr.mvc.json;

import org.boon.json.JsonFactory;
import org.boon.json.ObjectMapper;

public class BoonImpl implements Json {

	private ObjectMapper boon;

	public BoonImpl() {
		super();
		this.boon = JsonFactory.create();
	}

	@Override
	public <Type> Type deserialise(Class<Type> type, String string) {
		return boon.fromJson(string, type);
	}

	@Override
	public String serialise(Object object) {
		return boon.toJson(object);
	}

}
