/*
 * Created on Jan 15, 2007
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package fi.dwo.server.persistence;

import java.net.URL;
import java.util.Enumeration;
import java.util.Hashtable;

import fi.dwo.client.persistence.DbAccessIF;
import junit.framework.TestCase;

public class DbAccessLdapServletTest extends TestCase
{

    private DbAccessIF dbAccess;

    private URL url;

    protected void setUp() throws Exception
    {
        url = new URL(
                "http://www-dev.fi.uu.nl/servlet/fi.dwo.server.persistence.DbAccessLdapServlet");
        dbAccess = new fi.dwo.client.persistence.DbAccessClient(url);
    }

    public void testLogin() throws Exception
    {
        Hashtable h, h2;

        h = dbAccess.login("wimadmin", "");
        assertNotNull("login zonder ww", h);
        System.out.println(h.get("passwd"));
        h2 = dbAccess.login("wimadmin", "eb7a91f0182fda64863a2d0e1d5f95f8");
        assertNotNull("login met ww", h2);
        assertEquals("login", h, h2);
    }

    public void testLogin2() throws Exception
    {
        try
        {
            dbAccess.login("wimadmin", "eb7a91f0182fda64863a2d0e1d5f95f9");
            fail("login fout wachtwoord");
        } catch (org.apache.xmlrpc.applet.XmlRpcException e)
        {
            assertEquals("fi.dwo.client.system.LoginException", e.getMessage());
        }

    }
    
    public void testGetFidentitySchools() throws Exception
    {
    	DbAccessIF dba = new DbAccessLdap();
    	Hashtable result2 = dba.getFidentitySchools();
    	System.out.println(result2);
    	Enumeration e;
    	e = result2.keys();
    	while(e.hasMoreElements())
    		System.out.println(e.nextElement().getClass());
    	e = result2.elements();
    	while(e.hasMoreElements())
    		System.out.println(e.nextElement().getClass());
    	Hashtable result = dbAccess.getFidentitySchools();
    	System.out.println(result);
    	assertEquals(result, result2);
    }
    
    public void testRegisterMetDigicode() throws Exception
    {
    	dbAccess = new DbAccessLdap();
    	String username = "wim23";
    	String password = "5ffdcc9c92c301adb064a3ffd9bd264c";
    	String schoolLogin = "C12507X8632";
    	boolean result = dbAccess.register(username, password, "Wim", "van", "Velthoven", "wim@fi.uu.nl", schoolLogin, DbAccessLdap.DIGICODE, null);
    	
    }
    
    
}
