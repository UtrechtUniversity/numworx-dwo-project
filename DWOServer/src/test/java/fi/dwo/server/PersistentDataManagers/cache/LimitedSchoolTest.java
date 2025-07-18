package fi.dwo.server.PersistentDataManagers.cache;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class LimitedSchoolTest {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testIsLimitedSchool() {
		Long profileID = 111L;
		Long schoolID = 385L;
		assertTrue(LimitedSchoolCache.isLimitedSchool(profileID, schoolID));
	}

}
