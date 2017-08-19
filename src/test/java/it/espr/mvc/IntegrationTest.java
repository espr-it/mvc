package it.espr.mvc;

import static it.espr.mvc.route.parameter.Parameter.param;
import static org.mockito.Mockito.when;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import it.espr.mvc.model.SimpleModel;
import it.espr.mvc.route.RouteConfig;

@RunWith(MockitoJUnitRunner.class)
public class IntegrationTest {

	@Mock(answer=Answers.RETURNS_MOCKS)
	private HttpServletRequest request;
	
	@Mock(answer=Answers.RETURNS_MOCKS)
	private HttpServletResponse response;
	
	@Test
	public void testCaching() throws Exception {
		when(request.getMethod().toLowerCase()).thenReturn("get");
		when(request.getRequestURI()).thenReturn("/parse/id1");
		
		MvcConfiguration configuration = new MvcConfiguration() {
			@Override
			protected void configureMvc() {
				RouteConfig route1 = route().get("/parse/(.*:id)").to(SimpleModel.class, "parse").params(param("requestParameter")).config();

				enableCache();
				cache(route1);
			}
		};

		Dispatcher dispatcher = new Dispatcher();
		dispatcher.init(configuration);

		dispatcher.doGet(request, response);
		
		// this should come from cache
		dispatcher.doGet(request, response);
	}
}
