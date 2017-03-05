package it.espr.mvc.response;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Redirector {

	private static Logger log = LoggerFactory.getLogger(Redirector.class);

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
		} catch (Exception exception) {
			log.error("Problem when redirecting to {}", redirect.url, exception);
		}
	}
}
