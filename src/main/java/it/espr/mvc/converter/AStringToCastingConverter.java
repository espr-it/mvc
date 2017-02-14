package it.espr.mvc.converter;

public abstract class AStringToCastingConverter<Type> implements StringToTypeConverter<Type> {

	@Override
	public <Cast> Cast convert(Class<Cast> c, String value) throws StringToTypeConverterException {
		throw new UnsupportedOperationException("Not supported");
	}
}
