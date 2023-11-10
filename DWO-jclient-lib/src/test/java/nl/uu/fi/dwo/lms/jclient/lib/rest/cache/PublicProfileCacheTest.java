package nl.uu.fi.dwo.lms.jclient.lib.rest.cache;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import nl.uu.fi.dwo.lms.jclient.lib.rest.cache.PublicProfileCache;
import nl.uu.fi.dwo.rest.dom.entities.DomDwoProfileFull;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
import nl.uu.fi.dwo.rest.persistence.PersistenceClassType;
import nl.uu.fi.dwo.rest.persistence.PersistenceId;

public class PublicProfileCacheTest {

	@Before
	public void setUp() throws Exception {
		
		DomDwoProfileFull value = new DomDwoProfileFull();
		value.setId(new PersistenceId("LOCAL;"+PersistenceClassType.PersistentDwoProfile +";"+ 1L));
		PublicProfileCache.cache().put("1", value);
	}

	@After
	public void tearDown() throws Exception {
		PublicProfileCache.cache().clear();
	}

	@Test
	public void testCache() throws Dwo2Exception {
		DomDwoProfileFull result = PublicProfileCache.get(1);
		assertNotNull(result);
	}

}
