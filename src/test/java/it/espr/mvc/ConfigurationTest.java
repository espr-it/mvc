package it.espr.mvc;

import static org.fest.assertions.api.Assertions.*;
import org.junit.Test;

import it.espr.mvc.model.SimpleModel;

public class ConfigurationTest {

	private Configuration configuration = new Configuration() {

		@Override
		protected void configureRoutes() {
			this.addRoute("/parse/(.*:id)", "get", SimpleModel.class, "parse", new String[] { "requestParameter" });
		}
	};

	@Test
	public void testSimpleModel() {
		configuration.configure();
		assertThat(configuration.routes).hasSize(1);
		Route route = configuration.routes.values().iterator().next();
		assertThat(route.path.toString()).isEqualTo("/parse/(.*)(?:$|\\?.*)");
	}
}
