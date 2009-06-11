/*
 * Created on Nov 21, 2006
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package fi.dwo.server.persistence;

import java.sql.SQLException;
import java.util.Hashtable;
import java.util.Vector;

import junit.framework.TestCase;

public class DbAccessLdapTest extends TestCase
{

    private DbAccessLdap dbAccess;

    protected void setUp() throws Exception
    {
        dbAccess = new DbAccessLdap();
    }

    public void testChangeAccountIntStringStringStringStringStringStringInt() throws SQLException, DwoXmlRpcException
    {
        boolean r;
        try
        {
            r = dbAccess.changeAccount(2259, "wrong", "", "Wim", "van", "Velthoven", "wim@fi.uu.nl",1);
            fail("wrong password");
        } catch (DwoXmlRpcException e)
        {
        }        
        r = dbAccess.changeAccount(2259, "5ffdcc9c92c301adb064a3ffd9bd264c", "", "Wim", "van", "Velthoven", "wim@fi.uu.nl",1 );
        assertTrue("good password", r);
        assertTrue(dbAccess.disconnectFromClass(2259));
    }

    public void testChangeAccountIntStringStringStringStringStringString() throws DwoXmlRpcException, SQLException
    {
        boolean r;
        try
        {
            r = dbAccess.changeAccount(2259, "wrong", "", "Wim", "van", "Velthoven", "wim@fi.uu.nl");
            fail("wrong password");
        } catch (DwoXmlRpcException e)
        {
        }        
        r = dbAccess.changeAccount(2259, "5ffdcc9c92c301adb064a3ffd9bd264c", "", "Wim", "van", "Velthoven", "wim@fi.uu.nl");
        assertTrue("good password", r);
    }

    public void testGetUser()
    {
        assertEquals("wimvv is 2259", "wimvv", dbAccess.getUser(2259));
    }

    public void testChangePassword() throws Exception
    {
    	boolean r;
    	r = dbAccess.changeAccount(2259, "5ffdcc9c92c301adb064a3ffd9bd264c", "88c3315191aa317b37907a06ee879ea9", "Wim", "van", "Velthoven", "wim@fi.uu.nl");
        assertTrue("good password changed to klad", r);
    	try { 
    		r = dbAccess.changeAccount(2259, "5ffdcc9c92c301adb064a3ffd9bd264c", "88c3315191aa317b37907a06ee879ea9", "Wim", "van", "Velthoven", "wim@fi.uu.nl");
    		fail("changed password");
    	} catch (DwoXmlRpcException e)
    	{
    		
    	}
		r = dbAccess.changeAccount(2259,  "88c3315191aa317b37907a06ee879ea9", "5ffdcc9c92c301adb064a3ffd9bd264c","Wim", "van", "Velthoven", "wim@fi.uu.nl");
        assertTrue("good password changed back to pauline", r);
    }
    
    public void testLogin() throws Exception
    {
    	Hashtable r; 
    	r = dbAccess.login("meesterwim", "5ffdcc9c92c301adb064a3ffd9bd264c");
    	assertNotNull(r);
    	assertEquals("Empty password", "", r.get("passwd"));
    	System.out.println(r);
    }
    
    public void testChangeLDAPAccount() throws Exception
    {
    	Hashtable rr;
    	rr = dbAccess.login("meesterwim", "");
    	int uid = Integer.parseInt(rr.get("userID").toString());
    	assertEquals(13203, uid);
    	boolean r;
        r = dbAccess.changeAccount(uid, "5ffdcc9c92c301adb064a3ffd9bd264c", "5ffdcc9c92c301adb064a3ffd9bd264c", "Meester", "", "Wim", "wim@fi.uu.nl");
        assertTrue(r);
        rr = dbAccess.login("meesterwim", "");
        assertEquals("password empty", "", rr.get("passwd"));
    }
    
    public void testAddToSchool() throws Exception 
    {
    	Hashtable rr;
    	rr = dbAccess.login("meesterwim", "");
    	int uid = Integer.parseInt(rr.get("userID").toString());
    	assertEquals(13203, uid);
    	rr = dbAccess.addToSchool(uid, "L12507X7846", DbAccessLdap.DIGICODE, null);
    	assertEquals(new Integer(322), rr.get("schoolID"));
    }
    
    public void testRegisterDigicode() throws Exception
    {
    	final boolean r = dbAccess.register("testuser12345", "5ffdcc9c92c301adb064a3ffd9bd264c", "test", "test", "test", "test@test", "L12507X7846", DbAccessLdap.DIGICODE, null);
    	assertEquals(true,r);
    	Hashtable rr = dbAccess.login("testuser12345", "5ffdcc9c92c301adb064a3ffd9bd264c");
    	int uid = Integer.parseInt(rr.get("userID").toString());
        dbAccess.deleteUser(uid);	
    }
    
}
