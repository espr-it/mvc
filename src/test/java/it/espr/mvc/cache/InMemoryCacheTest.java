package it.espr.mvc.cache;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class InMemoryCacheTest {

	@InjectMocks
	InMemoryCache inMemoryCache;

	@Mock
	CacheConfig cacheConfig;

	@Test
	public void test() {
		inMemoryCache.put("get", "/", null, null);
	}
}
