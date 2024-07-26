package fi.dwo.server.PersistentDataManagers.cache;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import fi.dwo.commons.persistence.entities.PersistentLoginContext;
import nl.uu.fi.dwo.rest.dom.entities.DomDwoProfileFull;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
import nl.uu.fi.dwo.rest.persistence.PersistenceClassType;
import nl.uu.fi.dwo.rest.persistence.PersistenceId;

public class LoginContextCacheTest {

	@Before
	public void setUp() throws Exception {
		
		PersistentLoginContext value = new PersistentLoginContext();
		value.setId(1L);
		LoginContextCache.put(value);
	}

	@After
	public void tearDown() throws Exception {
		LoginContextCache.cache().clear();
	}

	@Test
	public void testCache() throws Dwo2Exception {
		PersistentLoginContext result = LoginContextCache.get(1L);
		assertNotNull(result);
	}

}
