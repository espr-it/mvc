package it.espr.mvc.view;

public final class ViewConfig {

	Object type;

	Class<? extends View> clazz;

	public ViewConfig(Object type, Class<? extends View> clazz) {
		this(type);
		this.with(clazz);
	}
	
	public ViewConfig(Object type) {
		this.type = type;
	}

	public void with(Class<? extends View> clazz) {
		this.clazz = clazz;
	}
}
