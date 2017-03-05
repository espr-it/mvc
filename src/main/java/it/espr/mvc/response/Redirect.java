package it.espr.mvc.response;

public class Redirect {

	public int code;

	public String url;

	public int cache;

	public Redirect(int code, String url) {
		this.code = code;
		this.url = url;
	}

	public Redirect(int code, String url, int cache) {
		this.code = code;
		this.url = url;
		this.cache = cache;
	}

}
