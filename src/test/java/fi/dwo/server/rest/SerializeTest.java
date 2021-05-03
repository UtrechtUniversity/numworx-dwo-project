package fi.dwo.server.rest;

import org.junit.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.owlike.genson.Genson;
import nl.uu.fi.dwo.rest.persistence.PersistenceClassType;
import nl.uu.fi.dwo.rest.persistence.PersistenceId;

public class SerializeTest {

	Genson genson;
	
	@Before
	public void setUp() throws Exception {
		genson = new GensonProvider().getContext(getClass());
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() {
		PersistenceId pid = new PersistenceId("LOCAL;" + PersistenceClassType.PersistentCourse + ";000000000000001");
		Assert.assertEquals(PersistenceClassType.PersistentCourse, pid.getType());
		String output = genson.serialize(pid);
		System.out.println(output);
		Assert.assertFalse("typeless", output.contains("type"));
	}

}
