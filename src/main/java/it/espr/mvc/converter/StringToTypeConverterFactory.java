package it.espr.mvc.converter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Named;

public class StringToTypeConverterFactory {

	private final Map<Class<?>, StringToTypeConverter<?>> converters = new HashMap<>();

	private static double defaultDouble;

	private static boolean defaultBoolean;

	private static int defaultInt;

	private static Map<Class<?>, Class<?>> primitivesToClass;

	static {
		primitivesToClass = new HashMap<>();
		primitivesToClass.put(int.class, Integer.class);
		primitivesToClass.put(double.class, Double.class);
		primitivesToClass.put(int.class, Integer.class);
		primitivesToClass.put(boolean.class, Boolean.class);
	}

	public StringToTypeConverterFactory(@Named("StringToTypeConverters") List<StringToTypeConverter<?>> converters) {
		for (StringToTypeConverter<?> converter : converters) {
			this.add(converter);
		}
	}

	public <Type> void add(StringToTypeConverter<Type> converter) {
		this.converters.put(converter.getType(), converter);
	}

	@SuppressWarnings("unchecked")
	private <Type> StringToTypeConverter<Type> getConverter(Class<Type> type) throws StringToTypeConverterException {
		Class<?> c = primitivesToClass.containsKey(type) ? primitivesToClass.get(type) : type;
		StringToTypeConverter<Type> converter = (StringToTypeConverter<Type>) converters.get(c);
		if (converter == null) {
			throw new StringToTypeConverterException("Can't find any converter for type '" + c + "'");
		}
		return converter;
	}

	public <Type> Type convert(Class<Type> type, String value) throws StringToTypeConverterException {
		StringToTypeConverter<Type> converter = this.getConverter(type);
		Type converted = converter.convert(value);
		return this.cast(type, converted);
	}

	@SuppressWarnings("unchecked")
	private <Type> Type cast(Class<Type> type, Type value) {
		if (!primitivesToClass.containsKey(type)) {
			return value;
		}
		if (type.equals(int.class)) {
			return (Type) (value == null ? defaultInt : value.getClass().cast(((Integer) value).intValue()));
		}
		if (type.equals(double.class)) {
			return (Type) (value == null ? defaultDouble : value.getClass().cast(((Double) value).doubleValue()));
		}
		if (type.equals(boolean.class)) {
			return (Type) (value == null ? defaultBoolean : value.getClass().cast(((Boolean) value).booleanValue()));
		}

		return null;
	}
}
