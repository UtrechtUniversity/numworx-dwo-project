/*
 * Created on Nov 22, 2006
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package fi.dwo.server.persistence;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.Hashtable;
import java.util.Vector;

import fi.dwo.client.persistence.DbAccessClient;
import fi.dwo.client.persistence.DbAccessIF;

import junit.framework.TestCase;

public class DbAccessTest extends TestCase
{

    protected void setUp() throws Exception
    {
        super.setUp();
    }


    /**
     * test login met empty password.
     * Regressie sinds revision 153.
     * @throws DwoXmlRpcException
     * @throws SQLException
     */
    public void testLogin() throws DwoXmlRpcException, SQLException
    {
        DbAccess db = new DbAccess();
        Hashtable  h = db.login("wimadmin", "");
        assertNotNull(h);

    }

    public void testRemoteLogin() throws Exception
    {
    	URL u = new URL("http://www.fi.uu.nl/servlet/fi.dwo.server.persistence.DbAccessServlet");
	DbAccessClient db = new DbAccessClient(u);
	Hashtable h = db.login("wimadmin", "");
	assertNotNull(h);
    }
    
   public void testRemoteLogin_dev() throws Exception
    {
    	URL u = new URL("http://www-dev.fi.uu.nl/servlet/fi.dwo.server.persistence.DbAccessServlet");
	DbAccessClient db = new DbAccessClient(u);
	Hashtable h = db.login("wimadmin", "");
	assertNotNull(h);
    }

   public void testGetUserResults() throws Exception
   {
	   DbAccess dbAccess = new DbAccess();
//simple test of getUserResults.
   	Vector v = dbAccess.getUserResults(365, 13203);
   	System.out.println(v);
   	assertEquals(4, v.size());
   }
   
   public void testGetUserResultVector() throws Exception
   {
	   DbAccessIF db = new DbAccess();
	   Vector courses = new Vector(1);
	   courses.addElement(new Integer(365));
	   Vector v = db.getUserResults(courses, 13203);
	   System.out.println(v);
	   assertEquals(1, v.size());
	   
   }
   
   public void testGetTable() throws Exception
   {
	   DbAccessIF db = new DbAccess();
	   Vector columnnames = new Vector(); 
	   columnnames.add("sconame");
	   Hashtable wheredef = new Hashtable();
	   wheredef.put("courseID", new Integer(1));
	   Vector result = db.getTable("tblSco", columnnames, wheredef, "sequencenr");
	   
	   System.out.println(result);
	   assertEquals(3, result.size());
	   assertEquals(1, ((Hashtable) result.firstElement()).size());
   }
   public void testGetTable2() throws Exception
   {
	   DbAccessIF db = new DbAccess();
	   Vector columnnames = new Vector(); 
	   columnnames.add("sconame");
	   columnnames.add("sequencenr");
	   columnnames.add("description");
	   Hashtable wheredef = new Hashtable();
	   wheredef.put("courseID", new Integer(1));
	   wheredef.put("appletID", new Integer(1));
	   Vector result = db.getTable("tblSco", columnnames, wheredef, "sequencenr");
	   
	   System.out.println(result);
	   assertEquals(3, result.size());
	   assertEquals(3, ((Hashtable) result.firstElement()).size());
   }

}
