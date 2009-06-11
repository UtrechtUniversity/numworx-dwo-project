package fi.dwo.server.persistence;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.xmlrpc.applet.XmlRpcException;

import junit.framework.TestCase;

public class DeleteSchoolTest extends TestCase {

	DbAccess dba;
	
	public static void main(String[] args) {
		junit.swingui.TestRunner.run(DeleteSchoolTest.class);
	}

	protected void setUp() throws Exception {
		super.setUp();
		dba = new DbAccess();
	}

	protected void tearDown() throws Exception {
		dba.close();
		super.tearDown();
	}

	/*
	 * Test method for 'fi.dwo.server.persistence.DbAccess.deleteSchool(int)'
	 */
	public void testDeleteSchool() throws IOException, XmlRpcException, SQLException {
		int schoolID = findSchool("schoolwim");
		assertEquals("findschool", 231, schoolID);
		boolean result = dba.deleteSchool(schoolID);
		assertTrue("delete school " , result);
	}

	private int findSchool(String schoollogin) throws SQLException {
		int result;
		Connection c = dba.getConnection();
		Statement s = c.createStatement();
		ResultSet rs = s.executeQuery("SELECT schoolID FROM tblSchool WHERE schoollogin = '" + schoollogin + "'");
		assertTrue(rs.next());
		result = rs.getInt(1);
		assertFalse(rs.next());
		rs.close();
		s.close();
		assertTrue("find school", result > 1);
		return result;
	}

}
