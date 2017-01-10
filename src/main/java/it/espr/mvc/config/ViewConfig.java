package it.espr.mvc.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import it.espr.mvc.view.View;

public final class ViewConfig {

	private List<String> accept;

	private Class<? extends View> clazz;

	public ViewConfig(String... accept) {
		this.accept = new ArrayList<>(Arrays.asList(accept));
	}

	public void with(Class<? extends View> clazz) {
		this.clazz = clazz;
	}

	public List<String> getAccept() {
		return accept;
	}

	public Class<? extends View> getClazz() {
		return clazz;
	}
}
