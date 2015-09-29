package fi.servlet.dwomaccess;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.Hashtable;
import java.util.Map;
import java.util.Vector;

import org.apache.xmlrpc.applet.XmlRpcException;

import fi.dwo.client.persistence.DbAccessClient;
import fi.dwo.client.persistence.DbAccessIF;
import junit.framework.TestCase;

public class DbAccessWrapperTest extends TestCase {

	int module = 54747;
	
	DbAccessIF wrapper;
	
	protected void setUp() throws Exception {

		DbAccessIF delegate = new DbAccessClient(new URL("http://ws-dev.fisme.science.uu.nl/servlet/dwodsaccess"));
		wrapper = new DbAccessWrapper(delegate);
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	
	public void testModule() throws Exception {
		
		DbAccessIF access = wrapper;

		accesstest(access);
	}
	public void testModule2() throws Exception {
		
		DbAccessIF access = wrapper;
		access2test(access);
	}

	private void accesstest(DbAccessIF access) throws IOException,
			XmlRpcException, SQLException {
		Hashtable wheredef = new Hashtable();
		wheredef.put("parentID", module);
		String table = "tblCourse";
		String orderby = "name";
		Vector<Map> results = access.getTable(table, wheredef, orderby);
		
		Map first = results.firstElement();
		assertNull(first.get("imageData"));
		assertEquals("HTML5 Test", first.get("name"));
	}
	
	private void access2test(DbAccessIF access) throws IOException,
	XmlRpcException, SQLException {
		String table = "tblDwoProfile";
		String id = "dwoProfileID";
		int value = 77;
			
		Hashtable results = access.getRecord(table, id, value);
		assertEquals("tablet", results.get("dwoProfileName"));
}

	
	public void testRemote() throws Exception { 
		DbAccessIF extern = new DbAccessClient(new URL("http://ws-dev.fisme.science.uu.nl/DWOmAccess/dbaccess"));
		accesstest(extern);
	}
	
	public void testRemote2() throws Exception { 
		DbAccessIF extern = new DbAccessClient(new URL("http://ws-dev.fisme.science.uu.nl/DWOmAccess/dbaccess"));
		access2test(extern);
	}
	
}
