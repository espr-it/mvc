package it.espr.mvc.json;

public interface Json {

	public <Type> Type deserialise(Class<Type> type, String string) throws Exception;

	public String serialise(Object object) throws Exception;
}
