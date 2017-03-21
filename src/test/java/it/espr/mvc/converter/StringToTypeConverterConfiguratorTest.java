package it.espr.mvc.converter;

import static org.fest.assertions.api.Assertions.assertThat;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class StringToTypeConverterConfiguratorTest {

	@InjectMocks
	StringToTypeConverterConfigurator stringToTypeConverterConfiguratorTest;
	
	@SuppressWarnings("unchecked")
	@Test
	public void testThatDefaultConfigurationHasAllConverters() {
		List<Class<? extends StringToTypeConverter<?>>> converters = stringToTypeConverterConfiguratorTest.configure();
		assertThat(converters).containsOnly(StringToStringConverter.class, StringToBooleanConverter.class, StringToDoubleConverter.class, StringToIntegerConverter.class, StringToObjectConverter.class);
	}
}
