package it.espr.mvc.view.json;

public class GsonView extends JsonView {

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
		return this.gson.toJson(data);
	}
}
