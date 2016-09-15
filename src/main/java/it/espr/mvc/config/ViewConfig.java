package it.espr.mvc.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import it.espr.mvc.view.View;

public final class ViewConfig {

	private List<String> accept;
	
	private View clazz;
	
	public ViewConfig(String ... accept) {
		this.accept = new ArrayList<>(Arrays.asList(accept));
	}
	
	public void with(View clazz) {
		this.clazz = clazz;
	}

	public List<String> getAccept() {
		return accept;
	}

	public View getClazz() {
		return clazz;
	}
}
