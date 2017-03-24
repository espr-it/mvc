package it.espr.mvc.view;

import static org.fest.assertions.api.Assertions.assertThat;

import java.util.regex.Pattern;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class RegexpFilterTest {

	@InjectMocks
	RegexpFilter filter;
	
	@Test
	public void testRemove() {
		Pattern pattern = Pattern.compile("/resource/(:id)");
		assertThat(filter.filterRegexpFromPattern(pattern)).isEqualTo("/resource");
	}
}
