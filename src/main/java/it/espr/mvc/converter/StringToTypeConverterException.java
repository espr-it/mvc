package it.espr.mvc.converter;

public class StringToTypeConverterException extends Exception {
	private static final long serialVersionUID = 1L;

	public StringToTypeConverterException(String message) {
		super(message);
	}

	public StringToTypeConverterException(String message, Throwable cause) {
		super(message, cause);
	}
}
