package it.espr.mvc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Iterator;
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
import it.espr.injector.Utils;
import it.espr.mvc.cache.CacheFactory;
import it.espr.mvc.converter.StringToTypeConverterFactory;
import it.espr.mvc.route.Route;
import it.espr.mvc.route.Router;

@SuppressWarnings("serial")
public class Dispatcher extends HttpServlet {

	private static final Logger log = LoggerFactory.getLogger(Dispatcher.class);

	private Injector injector;

	private Router router;

	private ViewResolver viewResolver;

	private StringToTypeConverterFactory stringToTypeConverterFactory;

	private CacheFactory cacheFactory;

	public void init() throws ServletException {
		try {
			MvcConfiguration configuration = (MvcConfiguration) Class.forName(this.getInitParameter("configuration")).newInstance();
			this.init(configuration);
		} catch (Exception e) {
			log.error("Problem when loading configuration for mvc dispatcher", e);
			throw new ServletException("Can't start app.", e);
		}
	}

	void init(MvcConfiguration configuration) throws ServletException {
		this.injector = Injector.injector(configuration);

		this.router = this.injector.get(Router.class);
		this.viewResolver = this.injector.get(ViewResolver.class);
		this.stringToTypeConverterFactory = this.injector.get(StringToTypeConverterFactory.class);
		this.cacheFactory = this.injector.get(CacheFactory.class);
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		log.debug("Received GET request.");
		this.dispatch(request, response);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		log.debug("Received POST request.");
		this.dispatch(request, response);
	}

	private void dispatch(HttpServletRequest request, HttpServletResponse response) throws IOException {
		log.debug("Dispatching request.");
		String requestType = request.getMethod().toLowerCase();
		String uri = URLDecoder.decode(request.getRequestURI(), "UTF-8");
		String url = uri + (Utils.isEmpty(request.getQueryString()) ? "" : "?" + request.getQueryString()); 

		log.debug("Routing {} {}.", requestType, url);
		Pair<Route, Map<String, Object>> pair = this.router.route(uri, requestType);

		if (pair == null) {
			log.debug("Couldn't find a route for {} {}.", requestType, url);
			return;
		}

		Route route = pair.p1;
		Map<String, Object> pathVariablesConfig = pair.p2;

		log.debug("Checking cache for {} {} ({})", requestType, url, route);
		Object result = this.cacheFactory.get(requestType, url, route);
		if (result == null) {
			log.debug("Routing request {} {} to {}", requestType, url, route);
			result = this.route(request, route, pathVariablesConfig);
			log.debug("Caching result for {} {} ({})", requestType, url, route);
			this.cacheFactory.put(requestType, url, route, result);
		}

		log.debug("Resolving view for {} {}", requestType, url);
		this.viewResolver.resolve(request, response, route, result);
	}

	private Object route(HttpServletRequest request, Route route, Map<String, Object> pathVariablesConfig) {
		String requestType = request.getMethod().toLowerCase();
		Object model = injector.get(route.model);
		List<Object> parameters = null;
		if ((pathVariablesConfig != null && pathVariablesConfig.size() > 0) || (route.parameters != null && route.parameters.size() > 0)) {
			parameters = new ArrayList<>();
			List<String> pathVariables = new ArrayList<>();
			if (pathVariablesConfig != null && pathVariablesConfig.size() > 0) {
				for (Entry<String, Object> entry : pathVariablesConfig.entrySet()) {
					pathVariables.add((String) entry.getValue());
				}
			}

			try {
				Iterator<String> pathVariable = pathVariables.iterator();
				for (Entry<String, Class<?>> parameter : route.parameters.entrySet()) {
					if (pathVariable.hasNext()) {
						parameters.add(this.stringToTypeConverterFactory.convert(parameter.getValue(), pathVariable.next()));
					} else if (parameter.getKey().startsWith("header-")) {
						parameters.add(this.stringToTypeConverterFactory.convert(parameter.getValue(), request.getHeader(parameter.getKey().substring("header-".length()))));
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
		return result;
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
