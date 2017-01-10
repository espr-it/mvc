package it.espr.mvc.converter;

import it.espr.injector.Utils;

public class StringToBooleanConverter implements StringToTypeConverter<Boolean> {

	@Override
	public Class<Boolean> getType() {
		return Boolean.class;
	}

	@Override
	public Boolean convert(String value) throws StringToTypeConverterException {
		try {
			if (!Utils.isEmpty(value)) {
				if ("1".equals(value)) {
					return true;
				}
				return Boolean.valueOf(value);
			}
		} catch (Exception e) {
			throw new StringToTypeConverterException("Couldn't parse value '" + value + "'", e);
		}
		return null;
	}

}
