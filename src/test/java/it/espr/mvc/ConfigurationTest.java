package it.espr.mvc;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import it.espr.mvc.cache.CacheConfigurator;
import it.espr.mvc.converter.StringToTypeConverterConfigurator;
import it.espr.mvc.model.SimpleModel;
import it.espr.mvc.route.RouteConfig;
import it.espr.mvc.route.RouteConfigurator;
import it.espr.mvc.view.ViewConfigurator;

@RunWith(MockitoJUnitRunner.class)
public class ConfigurationTest {

	@Mock(answer = Answers.RETURNS_DEEP_STUBS)
	RouteConfigurator routeConfigurator;

	@Mock(answer = Answers.RETURNS_DEEP_STUBS)
	ViewConfigurator viewConfigurator;

	@Mock(answer = Answers.RETURNS_DEEP_STUBS)
	StringToTypeConverterConfigurator stringToTypeConverterConfigurator;

	@Mock(answer = Answers.RETURNS_DEEP_STUBS)
	CacheConfigurator cacheConfigurator;

	@Mock
	ConfiguratorFactory configuratorFactory;

	@Before
	public void setUp() {
		when(configuratorFactory.routeConfigurator()).thenReturn(routeConfigurator);
		when(configuratorFactory.viewConfigurator()).thenReturn(viewConfigurator);
		when(configuratorFactory.stringToTypeConverterConfigurator()).thenReturn(stringToTypeConverterConfigurator);
		when(configuratorFactory.cacheConfigurator()).thenReturn(cacheConfigurator);
	}

	@Test
	public void whenConfiguringItCallsConfigurators() {
		MvcConfiguration configuration = new MvcConfiguration(configuratorFactory) {
			@Override
			protected void configureMvc() {
				route().get("/parse/(.*:id)").to(SimpleModel.class, "parse").with("requestParameter");
			}
		};
		configuration.configure();

		verify(routeConfigurator, times(2)).route();
		verify(routeConfigurator.route()).get("/parse/(.*:id)");
		verify(routeConfigurator.route().get("/parse/(.*:id)")).to(SimpleModel.class, "parse");
		verify(routeConfigurator.route().get("/parse/(.*:id)").to(SimpleModel.class, "parse")).with("requestParameter");
		verify(routeConfigurator).configure();
		verify(viewConfigurator).configure(false);
		verify(stringToTypeConverterConfigurator).configure();
		verify(cacheConfigurator).configure();
	}

	@Test
	public void whenConfiguringWeCanCacheRouteAtAnyStage() {
		MvcConfiguration configuration = new MvcConfiguration(configuratorFactory) {
			@Override
			protected void configureMvc() {
				RouteConfig routeConfig1 = route().get("/parse/(.*:id)").to(SimpleModel.class, "parse").config();
				cache(routeConfig1);

				RouteConfig routeConfig2 = route().get("/parse/(.*:id)").to(SimpleModel.class, "parse").with("requestParameter").config();
				cache(routeConfig2);
			}
		};
		configuration.configure();

		verify(cacheConfigurator, times(2)).cache(Mockito.any(RouteConfig.class));
	}

}
