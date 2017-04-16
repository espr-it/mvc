package it.espr.mvc.converter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StringToTypeConverterFactory {

	private static final Logger log = LoggerFactory.getLogger(StringToTypeConverterFactory.class);

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
		return (StringToTypeConverter<Type>) converters.get(c);
	}

	public <Type> Type convert(Class<Type> type, String value) throws StringToTypeConverterException {
		StringToTypeConverter<Type> converter = this.getConverter(type);
		Type converted = null;
		if (converter != null) {
			log.debug("Converting to {} with {}", type, converter);
			converted = this.cast(type, converter.convert(value));
			log.debug("Converted to {} with {}", type, converter);
		} else {
			// try json converter
			log.debug("Converting to json", type, converter);
			converted = converters.get(Object.class).convert(type, value);
			log.debug("Converted to json", type, converter);
		}
		return converted;
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
