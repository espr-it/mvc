package it.espr.mvc.route.parameter;

public class RequestHeader extends Parameter {

	RequestHeader(String name) {
		super(TYPE.REQUEST_HEADER, name);
	}
}
