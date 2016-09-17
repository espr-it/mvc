package it.espr.mvc.view.json;

public class Gson implements Json {

	private com.google.gson.Gson gson;

	public Gson() {
		this.gson = new com.google.gson.Gson();
	}

	@Override
	public String serialise(Object data) throws Exception {
		return this.gson.toJson(data);
	}

	@Override
	public String toString() {
		return "Gson";
	}
}
