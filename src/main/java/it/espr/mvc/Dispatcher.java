package it.espr.mvc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.espr.injector.Injector;
import it.espr.mvc.converter.StringToTypeConverterFactory;
import it.espr.mvc.json.Json;

@SuppressWarnings("serial")
public class Dispatcher extends HttpServlet {

	private static final Logger log = LoggerFactory.getLogger(Dispatcher.class);

	private Injector injector;

	private Router router;

	private ViewResolver viewResolver;

	private Json json;

	private StringToTypeConverterFactory stringToTypeConverterFactory;

	public void init() throws ServletException {
		try {
			Configuration configuration = (Configuration) Class.forName(this.getInitParameter("configuration")).newInstance();
			this.injector = Injector.injector(configuration);

			this.router = this.injector.get(Router.class);
			this.viewResolver = this.injector.get(ViewResolver.class);
			this.stringToTypeConverterFactory = this.injector.get(StringToTypeConverterFactory.class);
			this.json = this.injector.get(Json.class);

		} catch (Exception e) {
			log.error("Problem when loading configuration for mvc dispatcher", e);
			throw new ServletException("Can't start app.", e);
		}
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		this.dispatch(request, response);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		this.dispatch(request, response);
	}

	private void dispatch(HttpServletRequest request, HttpServletResponse response) {
		String uri = request.getRequestURI();
		String requestType = request.getMethod().toLowerCase();

		Pair<Route, Map<String, Object>> pair = this.router.route(uri, requestType);

		if (pair == null) {
			return;
		}

		Route route = pair.p1;
		Object model = injector.get(route.model);
		List<Object> parameters = null;
		if ((pair.p2 != null && pair.p2.size() > 0) || (route.parameters != null && route.parameters.size() > 0)) {
			parameters = new ArrayList<>();

			if (pair.p2 != null && pair.p2.size() > 0) {
				for (Entry<String, Object> entry : pair.p2.entrySet()) {
					parameters.add(entry.getValue());
				}
			}

			try {
				for (Entry<String, Class<?>> parameter : route.parameters.entrySet()) {
					if (parameter.getKey().startsWith("header")) {
						parameters.add(this.stringToTypeConverterFactory.convert(parameter.getValue(), request.getHeader(parameter.getKey())));
					} else if ("post".equals(requestType)) {
						if (parameter.getValue().equals(InputStream.class)) {
							parameters.add(request.getInputStream());
						} else if (parameter.getValue().equals(String.class)) {
							parameters.add(this.readBody(request));
						} else {
							parameters.add(this.stringToTypeConverterFactory.convert(parameter.getValue(), this.readBody(request)));
						}
					} else {
						parameters.add(this.stringToTypeConverterFactory.convert(parameter.getValue(), request.getParameter(parameter.getKey())));
					}
				}
			} catch (Exception e) {
				log.error("Problem when converting/parsing parameters {} from request {}", route.parameters, request, e);
			}
		}

		Object result = null;
		try {
			if (parameters == null) {
				result = route.method.invoke(model);
			} else {
				result = route.method.invoke(model, parameters.toArray());
			}
		} catch (Exception e) {
			log.error("Problem when calling model {}", model, e);
		}

		this.viewResolver.resolve(request, response, result);
	}

	private String readBody(HttpServletRequest request) {
		StringBuilder buffer = new StringBuilder();
		try {
			BufferedReader reader = request.getReader();
			String line;
			while ((line = reader.readLine()) != null) {
				buffer.append(line);
			}
		} catch (Exception e) {
			log.error("Problem when reading request body", e);
		}
		return buffer.toString();
	}
}
