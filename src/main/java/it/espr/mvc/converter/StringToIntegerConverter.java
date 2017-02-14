package it.espr.mvc.converter;

import it.espr.injector.Utils;

public class StringToIntegerConverter extends AStringToCastingConverter<Integer> {

	@Override
	public Class<Integer> getType() {
		return Integer.class;
	}

	@Override
	public Integer convert(String value) throws StringToTypeConverterException {
		try {
			if (!Utils.isEmpty(value)) {
				return Integer.parseInt(value);
			}
		} catch (Exception e) {
			throw new StringToTypeConverterException("Couldn't parse value '" + value + "'", e);
		}
		return null;
	}
}
