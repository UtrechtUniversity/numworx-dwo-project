package fi.dwo.server.PersistentDataManagers.cache;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import fi.dwo.commons.persistence.entities.PersistentDwoSystemParameters;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;

public class DwoSystemParametersTest {

	String KEY = "key";
	@Before
	public void setUp() throws Exception {
		
		PersistentDwoSystemParameters value = new PersistentDwoSystemParameters(KEY);
		DwoSystemParametersCache.put(value);
	}

	@After
	public void tearDown() throws Exception {
		DwoSystemParametersCache.cache().clear();
	}

	@Test
	public void testCache() throws Dwo2Exception {
		PersistentDwoSystemParameters result = DwoSystemParametersCache.get(KEY);
		assertNotNull(result);
	}

}
