package it.espr.mvc.converter;

import java.util.ArrayList;
import java.util.List;

public class StringToTypeConverterConfigurator {

	private final List<Class<? extends StringToTypeConverter<?>>> defaultConverters = new ArrayList<>();

	{
		defaultConverters.add(StringToStringConverter.class);
		defaultConverters.add(StringToBooleanConverter.class);
		defaultConverters.add(StringToDoubleConverter.class);
		defaultConverters.add(StringToIntegerConverter.class);
		defaultConverters.add(StringToObjectConverter.class);
	}

	private final List<Class<? extends StringToTypeConverter<?>>> customConverters = new ArrayList<>();

	public void register(Class<? extends StringToTypeConverter<?>> converter) {
		this.customConverters.add(converter);
	}

	public List<Class<? extends StringToTypeConverter<?>>> configure() {
		List<Class<? extends StringToTypeConverter<?>>> converters = new ArrayList<>();
		converters.addAll(defaultConverters);
		converters.addAll(customConverters);
		return converters;
	}
}
