package it.espr.mvc.view.binary;

import java.util.Base64;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.espr.mvc.route.Route;
import it.espr.mvc.view.View;

public class BinaryView implements View {

	private static Logger log = LoggerFactory.getLogger(BinaryView.class);

	protected String getContentType() {
		return "application/x-binary";
	}
	
	@Override
	public void view(HttpServletRequest request, HttpServletResponse response, Route route, Object data) {
		if (data == null || !(data instanceof byte[])) {
			return;
		}

		try {
			byte[] bytes = (byte[]) data;
			response.setHeader("Content-Type", getContentType());
			response.setHeader("Content-Length", String.valueOf(bytes.length));

			ServletOutputStream sos = response.getOutputStream();
			sos.write(Base64.getEncoder().encode(bytes));
			sos.close();
		} catch (Exception e) {
			log.error("Couldn't encode bytes into output stream", e);
		}
	}
}
