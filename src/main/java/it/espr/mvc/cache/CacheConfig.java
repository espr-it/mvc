package it.espr.mvc.cache;

public class CacheConfig {

	String name;

	int expiration;

	int size;
	
	public CacheConfig expiresIn(int seconds) {
		this.expiration = seconds;
		return this;
	}
	
	public CacheConfig maxSize(int size) {
		this.size = size;
		return this;
	}
	
}
