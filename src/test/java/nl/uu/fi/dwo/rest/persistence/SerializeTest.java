package nl.uu.fi.dwo.rest.persistence;

import org.junit.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.owlike.genson.Genson;
import com.owlike.genson.GensonBuilder;
import com.owlike.genson.ext.jaxb.JAXBBundle;

public class SerializeTest {

	Genson genson;
	
	@Before
	public void setUp() throws Exception {
		genson = new GensonBuilder()
				.withBundle(new JAXBBundle())
				.create();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() {
		PersistenceId pid = new PersistenceId("LOCAL;" + PersistenceClassType.PersistentCourse + ";000000000000001");
		
		String output = genson.serialize(pid);
		System.out.println(output);
		Assert.assertFalse("typeless", output.contains("type"));
	}

}
