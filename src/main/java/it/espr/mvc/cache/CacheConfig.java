package it.espr.mvc.cache;

public class CacheConfig {

	String name;

	int expiration;

	public CacheConfig expiresIn(int seconds) {
		this.expiration = seconds;
		return this;
	}
}
