package it.espr.mvc.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JacksonImpl implements Json {

	private ObjectMapper objectMapper = new ObjectMapper();

	@Override
	public <Type> Type deserialise(Class<Type> type, String string) throws Exception {
		return objectMapper.readValue(string, type);
	}

	@Override
	public String serialise(Object object) throws JsonProcessingException {
		return objectMapper.writeValueAsString(object);
	}

}
