package it.espr.mvc.view.json;

import com.fasterxml.jackson.databind.ObjectMapper;

public class Jackson implements Json {

	private ObjectMapper objectMapper;

	public Jackson() {
		this.objectMapper = new ObjectMapper();
	}

	@Override
	public String serialise(Object data) throws Exception {
		return this.objectMapper.writeValueAsString(data);
	}

	@Override
	public String toString() {
		return "Jackson";
	}
}
