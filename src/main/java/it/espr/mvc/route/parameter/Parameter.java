package it.espr.mvc.route.parameter;

public abstract class Parameter {

	public final TYPE type;

	public final String name;

	public Class<?> cls;

	protected Parameter(TYPE type, String name) {
		this.type = type;
		this.name = name;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((cls == null) ? 0 : cls.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Parameter other = (Parameter) obj;
		if (cls == null) {
			if (other.cls != null)
				return false;
		} else if (!cls.equals(other.cls))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (type != other.type)
			return false;
		return true;
	}

	public static Request request() {
		return new Request();
	}

	public static Response response() {
		return new Response();
	}

	public static RequestHeader header(String name) {
		return new RequestHeader(name);
	}

	public static RequestParameter param(String name) {
		return new RequestParameter(name);
	}
	
	public static RequestBody body() {
		return new RequestBody();
	}


}
