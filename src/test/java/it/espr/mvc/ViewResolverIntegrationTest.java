package it.espr.mvc;

import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.fest.assertions.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import it.espr.mvc.view.JsonView;
import it.espr.mvc.view.StaticView;
import it.espr.mvc.view.StringView;

@RunWith(MockitoJUnitRunner.class)
public class ViewResolverIntegrationTest {

	public static class Data {
		public String id;
		public Map<String, Object> map;
	}
	
	public static class Controller {

		public String returnString() {
			return "string";
		};
		
		public Object returnObject() {
			return data;
		};

		public Object throwException() throws Exception {
			throw new Exception("error");
		};
	}

	@Mock(answer = Answers.RETURNS_MOCKS)
	private HttpServletRequest request;

	@Mock(answer = Answers.RETURNS_MOCKS)
	private HttpServletResponse response;

	private StringWriter stringWriter;
	
	private PrintWriter writer;
	
	private static Data data;
	
	@Before
	public void setup() throws IOException {
        stringWriter = new StringWriter();
        writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);
        
        data = new Data();
        data.id = "123";
        data.map = new HashMap<String, Object>();
        data.map.put("a", 1);
        data.map.put("b", new ArrayList<String>());
	}
	
	@Test
	public void testDefaultView() throws Exception {
		when(request.getMethod().toLowerCase()).thenReturn("get");
		when(request.getRequestURI()).thenReturn("/");

		MvcConfiguration configuration = new MvcConfiguration() {
			@Override
			protected void configureMvc() {
				route().get("/").to(Controller.class, "returnString");
			}
		};

		Dispatcher dispatcher = new Dispatcher();
		dispatcher.init(configuration);

		dispatcher.doGet(request, response);
		
		assertOutput("string");
	}

	@Test
	public void testTextViewWithObject() throws Exception {
		when(request.getMethod().toLowerCase()).thenReturn("get");
		when(request.getRequestURI()).thenReturn("/");

		MvcConfiguration configuration = new MvcConfiguration() {
			@Override
			protected void configureMvc() {
				defaultView(StringView.class);
				route().get("/").to(Controller.class, "returnObject");
			}
		};

		Dispatcher dispatcher = new Dispatcher();
		dispatcher.init(configuration);

		dispatcher.doGet(request, response);
		
		assertOutput(data.toString());
	}

	@Test
	public void testJsonView() throws Exception {
		when(request.getMethod().toLowerCase()).thenReturn("get");
		when(request.getRequestURI()).thenReturn("/");

		MvcConfiguration configuration = new MvcConfiguration() {
			@Override
			protected void configureMvc() {
				defaultView(JsonView.class);
				route().get("/").to(Controller.class, "returnObject");
			}
		};

		Dispatcher dispatcher = new Dispatcher();
		dispatcher.init(configuration);

		dispatcher.doGet(request, response);
		
		assertOutput("{\"id\":\"123\",\"map\":{\"a\":1,\"b\":[]}}");
	}

	@Test
	public void testJsonViewWhenDefaultIsText() throws Exception {
		when(request.getMethod().toLowerCase()).thenReturn("get");
		when(request.getRequestURI()).thenReturn("/");
		when(request.getHeader("accept")).thenReturn("application/json");
		
		MvcConfiguration configuration = new MvcConfiguration() {
			@Override
			protected void configureMvc() {
				defaultView(StringView.class);
				view("application/json").with(JsonView.class);
				
				route().get("/").to(Controller.class, "returnObject");
			}
		};

		Dispatcher dispatcher = new Dispatcher();
		dispatcher.init(configuration);

		dispatcher.doGet(request, response);
		
		assertOutput("{\"id\":\"123\",\"map\":{\"a\":1,\"b\":[]}}");
	}

	@Test
	public void testErrorView() throws Exception {
		when(request.getMethod().toLowerCase()).thenReturn("get");
		when(request.getRequestURI()).thenReturn("/");
		
		MvcConfiguration configuration = new MvcConfiguration() {
			@Override
			protected void configureMvc() {
				route().get("/").to(Controller.class, "throwException");
			}
		};

		Dispatcher dispatcher = new Dispatcher();
		dispatcher.init(configuration);

		dispatcher.doGet(request, response);
		
		Mockito.verify(response).setStatus(400);
		assertOutput("");
	}

	@Test
	public void testHtmlView() throws Exception {
		when(request.getMethod().toLowerCase()).thenReturn("get");
		when(request.getRequestURI()).thenReturn("/");
		
		MvcConfiguration configuration = new MvcConfiguration() {
			@Override
			protected void configureMvc() {
				defaultView(StaticView.class);
				route().get("/").to(Controller.class, "returnObject");
			}
		};

		Dispatcher dispatcher = new Dispatcher();
		dispatcher.init(configuration);

		dispatcher.doGet(request, response);
		
		// Mockito.verify(response).setStatus(400);
		// assertOutput("");
	}
	
	private void assertOutput(String expected) {
		writer.flush();
		Assertions.assertThat(stringWriter.toString()).isEqualTo(expected);
	}
}
