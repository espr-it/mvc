package it.espr.mvc.view;

import java.io.File;
import java.nio.file.Files;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.espr.mvc.route.Route;

public class StaticView implements View {

	private static final Logger log = LoggerFactory.getLogger(StaticView.class);

	private static final Set<String> extensions = new HashSet<>();

	static {
		extensions.add(".htm");
		extensions.add(".html");
		extensions.add(".gif");
		extensions.add(".jpg");
		extensions.add(".jpeg");
		extensions.add(".jsp");
		extensions.add(".png");
		extensions.add(".js");
		extensions.add(".css");
	}

	@Override
	public void view(HttpServletRequest request, HttpServletResponse response, Route route, Object data) {
		String path = "";
		try {
			path += "static" + request.getRequestURI();
			if (needsdoAddIndexHtml(path)) {
				// if (!path.endsWith("/")) path += "/";
				path += "index.html";
			}
			
			File file = new File(path);
			
			if (file.exists()) {
				response.setHeader("Content-Type", "blah");
				response.setHeader("Content-Length", String.valueOf(file.length()));
				// response.setHeader("Content-Disposition", "attachment; filename=\"" + file.getName() + "\"");
				Files.copy(file.toPath(), response.getOutputStream());			
			} else {
				ServletOutputStream sos = response.getOutputStream();
				sos.write("404".getBytes());
				sos.close();
			}
		} catch (Exception e) {
			log.error("Problem when forwarding request to static html {}", path, e);
		}
	}

	public boolean needsdoAddIndexHtml(String path) {
		for (String extension : extensions) {
			if (path.endsWith(extension))
				return false;
		}
		return true;
	}
}
