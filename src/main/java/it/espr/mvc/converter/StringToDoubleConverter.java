package it.espr.mvc.converter;

import it.espr.injector.Utils;

public class StringToDoubleConverter implements StringToTypeConverter<Double> {

	@Override
	public Class<Double> getType() {
		return Double.class;
	}

	@Override
	public Double convert(String value) throws StringToTypeConverterException {
		try {
			if (!Utils.isEmpty(value)) {
				return Double.parseDouble(value);
			}
		} catch (Exception e) {
			throw new StringToTypeConverterException("Couldn't parse value '" + value + "'", e);
		}
		return null;
	}

}
