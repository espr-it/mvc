package it.espr.mvc.converter;

public interface StringToTypeConverter<Type> {

	public Class<Type> getType();

	public Type convert(String value) throws StringToTypeConverterException;

	public <Cast> Cast convert(Class<Cast> c, String value) throws StringToTypeConverterException;
}
