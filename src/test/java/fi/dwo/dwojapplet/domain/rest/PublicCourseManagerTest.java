package fi.dwo.dwojapplet.domain.rest;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import nl.uu.fi.dwo.rest.dom.entities.DomDwoProfile;


/* 
 * Integration test 
 * 
 */
@SuppressWarnings("unused")
public class PublicCourseManagerTest {

	PublicCourseManager manager;
	
	@Before
	public void setUp() throws Exception {
		DomDwoProfile profile = null;
		manager = new PublicCourseManager(profile);
	}

	@Test
	public void test() {
		
	}

}
