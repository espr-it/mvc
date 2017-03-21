package it.espr.mvc.view;

import java.util.Arrays;
import java.util.List;

public final class ViewConfig {

	List<String> accept;

	Class<? extends View> clazz;

	public ViewConfig(String... accept) {
		super();
		this.accept = Arrays.asList(accept);
	}

	public void with(Class<? extends View> clazz) {
		this.clazz = clazz;
	}
}
