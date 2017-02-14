package it.espr.mvc.converter;

public class StringToStringConverter extends AStringToCastingConverter<String> {

	@Override
	public Class<String> getType() {
		return String.class;
	}

	@Override
	public String convert(String value) {
		return value;
	}

}
