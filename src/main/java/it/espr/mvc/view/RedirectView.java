package it.espr.mvc.view;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.espr.mvc.route.Route;

public class RedirectView implements View {

	public static class Redirect {

		public int code;

		public String url;

		public int cache;

		public Redirect(int code, String url) {
			this.code = code;
			this.url = url;
		}

		public Redirect(int code, String url, int cache) {
			this.code = code;
			this.url = url;
			this.cache = cache;
		}
	}

	private static Logger log = LoggerFactory.getLogger(RedirectView.class);

	private static final int CACHE_DURATION_IN_SECOND = 60 * 60 * 24 * 1;

	final static long CACHE_DURATION_IN_MS = CACHE_DURATION_IN_SECOND * 1000;

	public void redirect(HttpServletResponse response, Redirect redirect) {
		log.debug("Redirecting to {} with code {}", redirect.url, redirect.code);
		try {
			if (redirect.cache > 0) {
				long now = System.currentTimeMillis();
				response.addHeader("Cache-Control", "max-age=" + redirect.cache);
				response.addHeader("Cache-Control", "must-revalidate");
				response.setDateHeader("Last-Modified", now);
				response.setDateHeader("Expires", now + redirect.cache * 1000);
				response.setStatus(redirect.code);
				response.setHeader("Location", redirect.url);
			} else {
				response.setHeader("Pragma", "No-cache");
				response.setHeader("Cache-Control", "no-cache,no-store,max-age=0");
				response.setDateHeader("Expires", 1);
			}
			response.sendRedirect(redirect.url);
		} catch (Exception exception) {
			log.error("Problem when redirecting to {}", redirect.url, exception);
		}
	}

	@Override
	public void view(HttpServletRequest request, HttpServletResponse response, Route route, Object data) {
		this.redirect(response, (Redirect) data);
	}
}
