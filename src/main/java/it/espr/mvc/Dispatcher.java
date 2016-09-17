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
import it.espr.injector.Utils.Pair;

@SuppressWarnings("serial")
public class Dispatcher extends HttpServlet {

	private static final Logger log = LoggerFactory.getLogger(Dispatcher.class);

	private Injector injector;

	private Router router;

	private ViewResolver viewResolver;

	public void init() throws ServletException {
		try {
			Configuration configuration = (Configuration) Class.forName(this.getInitParameter("configuration")).newInstance();
			configuration.configureMvc();
			this.router = new Router(configuration.routes);
			this.viewResolver = new ViewResolver(configuration.views);
			this.injector = Injector.get(configuration);
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

			if ("post".equals(requestType) && route.parameters.size() == 1) { //&& !"application/x-www-form-urlencoded".equals(request.getContentType())) {
				Entry<String, Class<?>> parameter = route.parameters.entrySet().iterator().next();
				if (parameter.getValue().equals(InputStream.class)) {
					try {
						parameters.add(request.getInputStream());
					} catch (IOException e) {
						log.error("Problem when parsing request input stream", e);
					}
				} else {
					parameters.add(this.readBody(request));
				}
			} else {
				for (Entry<String, Class<?>> entry : route.parameters.entrySet()) {
					parameters.add(request.getParameter(entry.getKey()));
				}
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
