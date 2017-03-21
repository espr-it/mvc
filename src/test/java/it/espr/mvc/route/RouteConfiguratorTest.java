package it.espr.mvc.route;

import static org.fest.assertions.api.Assertions.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class RouteConfiguratorTest {

	@InjectMocks
	RouteConfigurator routeConfigurator;

	@Test
	public void whenRouteIsCalledItReturnsRouteConfig() {
		RouteConfig routeConfig = this.routeConfigurator.route();
		assertThat(routeConfig).isNotNull();
		assertThat(routeConfig.getRequestTypes()).isNull();
		assertThat(routeConfig.getParameters()).isNull();
		assertThat(routeConfig.getClazz()).isNull();
	}
}
