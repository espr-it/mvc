package it.espr.mvc.view.json;

import com.fasterxml.jackson.databind.ObjectMapper;

public class JacksonView extends JsonView {

	private ObjectMapper objectMapper;

	public JacksonView(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
	}

	@Override
	public String toString() {
		return "Jackson JSonView";
	}

	@Override
	protected String out(Object data) throws Exception {
		return this.objectMapper.writeValueAsString(data);
	}
}
