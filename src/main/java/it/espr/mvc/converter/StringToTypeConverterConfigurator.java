package it.espr.mvc.converter;

import java.util.ArrayList;
import java.util.List;

public class StringToTypeConverterConfigurator {

	public List<Class<? extends StringToTypeConverter<?>>> configure() {
		List<Class<? extends StringToTypeConverter<?>>> converters = new ArrayList<>();

		converters.add(StringToStringConverter.class);
		converters.add(StringToBooleanConverter.class);
		converters.add(StringToDoubleConverter.class);
		converters.add(StringToIntegerConverter.class);
		converters.add(StringToObjectConverter.class);

		return converters;
	}
}
