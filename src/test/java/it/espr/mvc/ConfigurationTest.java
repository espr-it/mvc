package it.espr.mvc;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.fest.assertions.api.Assertions.entry;

import org.junit.Test;

import it.espr.mvc.model.SimpleModel;

public class ConfigurationTest {

	@Test
	public void testSimpleModel() {
		Configuration configuration = new Configuration() {
			@Override
			protected void configureMvc() {
				route().get("/parse/(.*:id)").to(SimpleModel.class, "parse").with("requestParameter");
			}
		};
		configuration.configure();

		assertThat(configuration.routes).hasSize(1);
		Route route = configuration.routes.values().iterator().next();
		assertThat(route.path.toString()).isEqualTo("/parse/(.*)(?:$|\\?.*)");
	}

	@Test
	public void testSimpleModelAllRequestTypes() {
		Configuration configuration = new Configuration() {
			@Override
			protected void configureMvc() {
				route().all("/parse/([0-9]+:id)/(:type)").to(SimpleModel.class, "parseAllRequests").with("requestParameter");
			}
		};
		
		configuration.configure();

		assertThat(configuration.routes).hasSize(2);
		Route route = configuration.routes.values().iterator().next();
		assertThat(route.path.toString()).isEqualTo("/parse/([0-9]+)/([-_0-9a-zA-Z]+)(?:$|\\?.*)");
		assertThat(route.pathVariables).hasSize(2);
		assertThat(route.pathVariables.get(0).p1).isEqualTo("id");
		assertThat(route.pathVariables.get(0).p2.equals(String.class)).isTrue();
		assertThat(route.pathVariables.get(1).p1).isEqualTo("type");
		assertThat(route.pathVariables.get(0).p2.equals(String.class)).isTrue();
		assertThat(route.parameters).hasSize(1);
		assertThat(route.parameters).contains(entry("requestParameter", String.class));
	}

}
