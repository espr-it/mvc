package it.espr.mvc.converter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.espr.mvc.json.Json;

public class StringToObjectConverter extends AStringToCastingConverter<Object> {

	private static final Logger log = LoggerFactory.getLogger(StringToObjectConverter.class);

	private Json json;

	public StringToObjectConverter(Json json) {
		this.json = json;
	}

	@Override
	public Class<Object> getType() {
		return Object.class;
	}

	@Override
	public Object convert(String value) throws StringToTypeConverterException {
		throw new UnsupportedOperationException("Not supported");
	}

	@Override
	public <Cast> Cast convert(Class<Cast> c, String value) throws StringToTypeConverterException {
		if (json != null) {
			try {
				return this.json.deserialise(c, value);
			} catch (Exception e) {
				log.error("Problem when deseriliasing {} to {}", value, c, e);
				throw new StringToTypeConverterException("Problem when converting from json", e);
			}
		} else {
			throw new UnsupportedOperationException("No json converter registered");
		}
	}
}
