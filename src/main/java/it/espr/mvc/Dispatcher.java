package it.espr.mvc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

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
import it.espr.mvc.route.parameter.Parameter;

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

		log.debug("Looking up a route for {} {}.", requestType, url);
		Pair<Route, List<String>> pair = this.router.route(uri, requestType);
		log.debug("Found the route for {} {}.", requestType, url);

		if (pair == null) {
			log.debug("Couldn't find a route for {} {}.", requestType, url);
			return;
		}

		Route route = pair.p1;
		List<String> pathVariablesConfig = pair.p2;

		log.debug("Checking cache for {} {} ({})", requestType, url, route);
		Object result = this.cacheFactory.get(requestType, url, route);
		if (result == null) {
			log.debug("Routing request {} {} to {}", requestType, url, route);
			result = this.route(request, response, route, pathVariablesConfig);
			log.debug("Caching result for {} {} ({})", requestType, url, route);
			this.cacheFactory.put(requestType, url, route, result);
			log.debug("Cached result for {} {} ({})", requestType, url, route);
		}

		log.debug("Resolving view for {} {}", requestType, url);
		this.viewResolver.resolve(request, response, route, result);
		log.debug("View resolved for {} {}", requestType, url);
	}

	private Object route(HttpServletRequest request, HttpServletResponse response, Route route, List<String> pathVariablesConfig) {
		log.debug("Creating route instance");
		Object model = injector.get(route.model);
		log.debug("Created route instance");
		List<Object> parameters = null;

		try {
			if (route.parameters.size() > 0) {
				parameters = new ArrayList<>();
				for (int i = 0; i < route.parameters.size(); i++) {
					Parameter parameter = route.parameters.get(0);

					switch (parameter.type) {

					case PATH_VARIABLE:
						parameters.add(this.stringToTypeConverterFactory.convert(parameter.cls, pathVariablesConfig.get(i)));
						break;

					case REQUEST_HEADER:
						parameters.add(this.stringToTypeConverterFactory.convert(parameter.cls, request.getHeader(parameter.name)));
						break;

					case REQUEST_PARAMETER:
						parameters.add(this.stringToTypeConverterFactory.convert(parameter.cls, request.getParameter(parameter.name)));
						break;

					case REQUEST_BODY:
						if (String.class.equals(parameter.cls)) {
							parameters.add(this.readBody(request));
						} else if (InputStream.class.equals(parameter.cls)) {
							parameters.add(request.getInputStream());
						} else {
							parameters.add(this.stringToTypeConverterFactory.convert(parameter.cls, this.readBody(request)));
						}
						break;

					case REQUEST:
						parameters.add(request);
						break;

					case RESPONSE:
						parameters.add(response);
						break;

					default:
						break;
					}
				}
			}
		} catch (Exception e) {
			log.error("Problem when converting/parsing parameters {} from request {}", route.parameters, request, e);
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
