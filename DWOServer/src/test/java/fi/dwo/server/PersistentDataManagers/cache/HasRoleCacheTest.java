package fi.dwo.server.PersistentDataManagers.cache;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import fi.dwo.commons.persistence.entities.PersistentHasRole;
import fi.dwo.commons.persistence.entities.PersistentHasRolePK;
import fi.dwo.commons.persistence.entities.PersistentLoginContext;
import nl.uu.fi.dwo.rest.dom.entities.DomDwoProfileFull;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
import nl.uu.fi.dwo.rest.persistence.PersistenceClassType;
import nl.uu.fi.dwo.rest.persistence.PersistenceId;

public class HasRoleCacheTest {

	private static final PersistentHasRolePK KEY = new PersistentHasRolePK(1L,2L);
	@Before
	public void setUp() throws Exception {
		
		PersistentHasRole value = new PersistentHasRole(KEY);
		HasRoleCache.put(value);
	}

	@After
	public void tearDown() throws Exception {
		HasRoleCache.cache().clear();
	}

	@Test
	public void testCache() throws Dwo2Exception {
		PersistentHasRole result = HasRoleCache.get(KEY);
		assertNotNull(result);
	}

}
