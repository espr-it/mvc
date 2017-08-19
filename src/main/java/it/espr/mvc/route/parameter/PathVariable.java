package it.espr.mvc.route.parameter;

public class PathVariable extends Parameter {

	public PathVariable(String name, Class<?> cls) {
		super(TYPE.PATH_VARIABLE, name);
		this.cls = cls;
	}
}
