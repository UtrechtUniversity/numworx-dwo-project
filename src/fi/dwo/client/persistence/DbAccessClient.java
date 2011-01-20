// Generated code, do not edit
package fi.dwo.client.persistence;

import java.util.Vector;
import java.net.URL;
import fi.beans.xmlrpc.Client;
import org.apache.xmlrpc.applet.XmlRpcException;
import java.io.IOException;

public class DbAccessClient extends Client implements fi.dwo.client.persistence.DbAccessIF {

	public DbAccessClient(URL u) {
		super(u);
	}

    public java.util.Vector getCoursesForClass(int a) throws IOException, XmlRpcException
    {
        Vector vv = new Vector(1);
        vv.addElement( new Integer(a));
        Object object = invoke("getCoursesForClass", vv);
        return (java.util.Vector)object;
    }

    public boolean selectCoursesForClass(int a, int b) throws IOException, XmlRpcException
    {
        Vector vv = new Vector(2);
        vv.addElement( new Integer(a));
        vv.addElement( new Integer(b));
        Object object = invoke("selectCoursesForClass", vv);
        return ((Boolean)object).booleanValue();
    }

    public boolean deSelectCoursesForClass(int a, int b) throws IOException, XmlRpcException
    {
        Vector vv = new Vector(2);
        vv.addElement( new Integer(a));
        vv.addElement( new Integer(b));
        Object object = invoke("deSelectCoursesForClass", vv);
        return ((Boolean)object).booleanValue();
    }

    public java.util.Vector getCourses(int a) throws IOException, XmlRpcException
    {
        Vector vv = new Vector(1);
        vv.addElement( new Integer(a));
        Object object = invoke("getCourses", vv);
        return (java.util.Vector)object;
    }

    public java.util.Vector getCourses(int a, boolean b) throws IOException, XmlRpcException
    {
        Vector vv = new Vector(2);
        vv.addElement( new Integer(a));
        vv.addElement( new Boolean(b));
        Object object = invoke("getCourses", vv);
        return (java.util.Vector)object;
    }

    public java.util.Vector getEditableCoursesAdmin() throws IOException, XmlRpcException
    {
        Vector vv = new Vector(0);
        Object object = invoke("getEditableCoursesAdmin", vv);
        return (java.util.Vector)object;
    }

    public java.util.Vector getEditableCourses(int a) throws IOException, XmlRpcException
    {
        Vector vv = new Vector(1);
        vv.addElement( new Integer(a));
        Object object = invoke("getEditableCourses", vv);
        return (java.util.Vector)object;
    }

    public java.util.Hashtable getRecord(java.lang.String a, java.lang.String b, int c) throws IOException, XmlRpcException
    {
        Vector vv = new Vector(3);
        vv.addElement(a);
        vv.addElement(b);
        vv.addElement( new Integer(c));
        Object object = invoke("getRecord", vv);
        return (java.util.Hashtable)object;
    }

    public java.util.Vector getTable(java.lang.String a) throws IOException, XmlRpcException
    {
        Vector vv = new Vector(1);
        vv.addElement(a);
        Object object = invoke("getTable", vv);
        return (java.util.Vector)object;
    }

    public java.util.Vector getTable(java.lang.String a, java.lang.String b) throws IOException, XmlRpcException
    {
        Vector vv = new Vector(2);
        vv.addElement(a);
        vv.addElement(b);
        Object object = invoke("getTable", vv);
        return (java.util.Vector)object;
    }

    public java.util.Vector getTable(java.lang.String a, java.util.Hashtable b) throws IOException, XmlRpcException
    {
        Vector vv = new Vector(2);
        vv.addElement(a);
        vv.addElement(b);
        Object object = invoke("getTable", vv);
        return (java.util.Vector)object;
    }

    public java.util.Vector getTable(java.lang.String a, java.util.Hashtable b, java.lang.String c) throws IOException, XmlRpcException
    {
        Vector vv = new Vector(3);
        vv.addElement(a);
        vv.addElement(b);
        vv.addElement(c);
        Object object = invoke("getTable", vv);
        return (java.util.Vector)object;
    }

    public java.util.Vector getTable(java.lang.String a, java.util.Vector b, java.util.Hashtable c, java.lang.String d) throws IOException, XmlRpcException
    {
        Vector vv = new Vector(4);
        vv.addElement(a);
        vv.addElement(b);
        vv.addElement(c);
        vv.addElement(d);
        Object object = invoke("getTable", vv);
        return (java.util.Vector)object;
    }

    public boolean renameClass(int a, java.lang.String b) throws IOException, XmlRpcException
    {
        Vector vv = new Vector(2);
        vv.addElement( new Integer(a));
        vv.addElement(b);
        Object object = invoke("renameClass", vv);
        return ((Boolean)object).booleanValue();
    }

    public boolean reassignClass(int a, int b) throws IOException, XmlRpcException
    {
        Vector vv = new Vector(2);
        vv.addElement( new Integer(a));
        vv.addElement( new Integer(b));
        Object object = invoke("reassignClass", vv);
        return ((Boolean)object).booleanValue();
    }

    public boolean register(java.lang.String a, java.lang.String b, java.lang.String c, java.lang.String d, java.lang.String e, java.lang.String f) throws IOException, XmlRpcException
    {
        Vector vv = new Vector(6);
        vv.addElement(a);
        vv.addElement(b);
        vv.addElement(c);
        vv.addElement(d);
        vv.addElement(e);
        vv.addElement(f);
        Object object = invoke("register", vv);
        return ((Boolean)object).booleanValue();
    }

    public java.lang.String LMSGetValue(int a, int b, java.lang.String c) throws IOException, XmlRpcException
    {
        Vector vv = new Vector(3);
        vv.addElement( new Integer(a));
        vv.addElement( new Integer(b));
        vv.addElement(c);
        Object object = invoke("LMSGetValue", vv);
        return (java.lang.String)object;
    }

    public java.lang.String LMSSetValue(int a, int b, java.lang.String c, java.lang.String d) throws IOException, XmlRpcException
    {
        Vector vv = new Vector(4);
        vv.addElement( new Integer(a));
        vv.addElement( new Integer(b));
        vv.addElement(c);
        vv.addElement(d);
        Object object = invoke("LMSSetValue", vv);
        return (java.lang.String)object;
    }

    public java.lang.String LMSSetValue(int a, int b, java.lang.String c, java.lang.String d, java.lang.String e) throws IOException, XmlRpcException
    {
        Vector vv = new Vector(5);
        vv.addElement( new Integer(a));
        vv.addElement( new Integer(b));
        vv.addElement(c);
        vv.addElement(d);
        vv.addElement(e);
        Object object = invoke("LMSSetValue", vv);
        return (java.lang.String)object;
    }

    public boolean register(java.lang.String a, java.lang.String b, java.lang.String c, java.lang.String d, java.lang.String e, java.lang.String f, java.lang.String g, int h, java.lang.String i) throws IOException, XmlRpcException
    {
        Vector vv = new Vector(9);
        vv.addElement(a);
        vv.addElement(b);
        vv.addElement(c);
        vv.addElement(d);
        vv.addElement(e);
        vv.addElement(f);
        vv.addElement(g);
        vv.addElement( new Integer(h));
        vv.addElement(i);
        Object object = invoke("register", vv);
        return ((Boolean)object).booleanValue();
    }

    public java.util.Hashtable login(java.lang.String a, java.lang.String b) throws IOException, XmlRpcException
    {
        Vector vv = new Vector(2);
        vv.addElement(a);
        vv.addElement(b);
        Object object = invoke("login", vv);
        return (java.util.Hashtable)object;
    }

    public java.util.Hashtable addToSchool(int a, java.lang.String b, int c, java.lang.String d) throws IOException, XmlRpcException
    {
        Vector vv = new Vector(4);
        vv.addElement( new Integer(a));
        vv.addElement(b);
        vv.addElement( new Integer(c));
        vv.addElement(d);
        Object object = invoke("addToSchool", vv);
        return (java.util.Hashtable)object;
    }

    public boolean changeAccount(int a, java.lang.String b, java.lang.String c, java.lang.String d, java.lang.String e, java.lang.String f, java.lang.String g, int h) throws IOException, XmlRpcException
    {
        Vector vv = new Vector(8);
        vv.addElement( new Integer(a));
        vv.addElement(b);
        vv.addElement(c);
        vv.addElement(d);
        vv.addElement(e);
        vv.addElement(f);
        vv.addElement(g);
        vv.addElement( new Integer(h));
        Object object = invoke("changeAccount", vv);
        return ((Boolean)object).booleanValue();
    }

    public boolean changeAccount(int a, java.lang.String b, java.lang.String c, java.lang.String d, java.lang.String e, java.lang.String f, java.lang.String g) throws IOException, XmlRpcException
    {
        Vector vv = new Vector(7);
        vv.addElement( new Integer(a));
        vv.addElement(b);
        vv.addElement(c);
        vv.addElement(d);
        vv.addElement(e);
        vv.addElement(f);
        vv.addElement(g);
        Object object = invoke("changeAccount", vv);
        return ((Boolean)object).booleanValue();
    }

    public java.util.Hashtable addClass(int a, java.lang.String b) throws IOException, XmlRpcException
    {
        Vector vv = new Vector(2);
        vv.addElement( new Integer(a));
        vv.addElement(b);
        Object object = invoke("addClass", vv);
        return (java.util.Hashtable)object;
    }

    public java.util.Hashtable addSchool(java.lang.String a, java.lang.String b, java.lang.String c, java.lang.String d) throws IOException, XmlRpcException
    {
        Vector vv = new Vector(4);
        vv.addElement(a);
        vv.addElement(b);
        vv.addElement(c);
        vv.addElement(d);
        Object object = invoke("addSchool", vv);
        return (java.util.Hashtable)object;
    }

    public java.util.Hashtable addSchool(int a, java.lang.String b, java.lang.String c, java.lang.String d, java.lang.String e) throws IOException, XmlRpcException
    {
        Vector vv = new Vector(5);
        vv.addElement( new Integer(a));
        vv.addElement(b);
        vv.addElement(c);
        vv.addElement(d);
        vv.addElement(e);
        Object object = invoke("addSchool", vv);
        return (java.util.Hashtable)object;
    }

    public java.util.Hashtable editSchool(int a, java.lang.String b, java.lang.String c, java.lang.String d, java.lang.String e) throws IOException, XmlRpcException
    {
        Vector vv = new Vector(5);
        vv.addElement( new Integer(a));
        vv.addElement(b);
        vv.addElement(c);
        vv.addElement(d);
        vv.addElement(e);
        Object object = invoke("editSchool", vv);
        return (java.util.Hashtable)object;
    }

    public boolean deleteUser(int a) throws IOException, XmlRpcException
    {
        Vector vv = new Vector(1);
        vv.addElement( new Integer(a));
        Object object = invoke("deleteUser", vv);
        return ((Boolean)object).booleanValue();
    }

    public boolean deleteClass(int a, boolean b) throws IOException, XmlRpcException
    {
        Vector vv = new Vector(2);
        vv.addElement( new Integer(a));
        vv.addElement( new Boolean(b));
        Object object = invoke("deleteClass", vv);
        return ((Boolean)object).booleanValue();
    }

    public java.util.Vector getResults(java.util.Vector a, int b) throws IOException, XmlRpcException
    {
        Vector vv = new Vector(2);
        vv.addElement(a);
        vv.addElement( new Integer(b));
        Object object = invoke("getResults", vv);
        return (java.util.Vector)object;
    }

    public java.util.Vector getResults(java.util.Vector a, int b, int c) throws IOException, XmlRpcException
    {
        Vector vv = new Vector(3);
        vv.addElement(a);
        vv.addElement( new Integer(b));
        vv.addElement( new Integer(c));
        Object object = invoke("getResults", vv);
        return (java.util.Vector)object;
    }

    public java.util.Vector getResults(int a, int b, int c) throws IOException, XmlRpcException
    {
        Vector vv = new Vector(3);
        vv.addElement( new Integer(a));
        vv.addElement( new Integer(b));
        vv.addElement( new Integer(c));
        Object object = invoke("getResults", vv);
        return (java.util.Vector)object;
    }

    public java.util.Vector getResults(int a, int b) throws IOException, XmlRpcException
    {
        Vector vv = new Vector(2);
        vv.addElement( new Integer(a));
        vv.addElement( new Integer(b));
        Object object = invoke("getResults", vv);
        return (java.util.Vector)object;
    }

    public boolean disconnectFromClass(int a) throws IOException, XmlRpcException
    {
        Vector vv = new Vector(1);
        vv.addElement( new Integer(a));
        Object object = invoke("disconnectFromClass", vv);
        return ((Boolean)object).booleanValue();
    }

    public boolean selectJar(java.lang.String a, java.lang.String b) throws IOException, XmlRpcException
    {
        Vector vv = new Vector(2);
        vv.addElement(a);
        vv.addElement(b);
        Object object = invoke("selectJar", vv);
        return ((Boolean)object).booleanValue();
    }

    public boolean reconnect() throws IOException, XmlRpcException
    {
        Vector vv = new Vector(0);
        Object object = invoke("reconnect", vv);
        return ((Boolean)object).booleanValue();
    }

    public boolean log(java.lang.String a) throws IOException, XmlRpcException
    {
        Vector vv = new Vector(1);
        vv.addElement(a);
        Object object = invoke("log", vv);
        return ((Boolean)object).booleanValue();
    }

    public int addCourse(int a, java.lang.String b, java.lang.String c, int d) throws IOException, XmlRpcException
    {
        Vector vv = new Vector(4);
        vv.addElement( new Integer(a));
        vv.addElement(b);
        vv.addElement(c);
        vv.addElement( new Integer(d));
        Object object = invoke("addCourse", vv);
        return ((Number)object).intValue();
    }

    public boolean changeCourse(int a, java.lang.String b, java.lang.String c) throws IOException, XmlRpcException
    {
        Vector vv = new Vector(3);
        vv.addElement( new Integer(a));
        vv.addElement(b);
        vv.addElement(c);
        Object object = invoke("changeCourse", vv);
        return ((Boolean)object).booleanValue();
    }

    public boolean deleteCourse(int a) throws IOException, XmlRpcException
    {
        Vector vv = new Vector(1);
        vv.addElement( new Integer(a));
        Object object = invoke("deleteCourse", vv);
        return ((Boolean)object).booleanValue();
    }

    public int addSco(int a, java.lang.String b, java.lang.String c, int d, int e) throws IOException, XmlRpcException
    {
        Vector vv = new Vector(5);
        vv.addElement( new Integer(a));
        vv.addElement(b);
        vv.addElement(c);
        vv.addElement( new Integer(d));
        vv.addElement( new Integer(e));
        Object object = invoke("addSco", vv);
        return ((Number)object).intValue();
    }

    public int addSco(int a, java.lang.String b, java.lang.String c, int d, java.lang.String e, int f) throws IOException, XmlRpcException
    {
        Vector vv = new Vector(6);
        vv.addElement( new Integer(a));
        vv.addElement(b);
        vv.addElement(c);
        vv.addElement( new Integer(d));
        vv.addElement(e);
        vv.addElement( new Integer(f));
        Object object = invoke("addSco", vv);
        return ((Number)object).intValue();
    }

    public boolean changeSco(int a, java.lang.String b, java.lang.String c, java.lang.String d) throws IOException, XmlRpcException
    {
        Vector vv = new Vector(4);
        vv.addElement( new Integer(a));
        vv.addElement(b);
        vv.addElement(c);
        vv.addElement(d);
        Object object = invoke("changeSco", vv);
        return ((Boolean)object).booleanValue();
    }

    public boolean changeSco(int a, java.lang.String b, java.lang.String c) throws IOException, XmlRpcException
    {
        Vector vv = new Vector(3);
        vv.addElement( new Integer(a));
        vv.addElement(b);
        vv.addElement(c);
        Object object = invoke("changeSco", vv);
        return ((Boolean)object).booleanValue();
    }

    public boolean changeSco(int a, java.lang.String b, java.lang.String c, boolean d) throws IOException, XmlRpcException
    {
        Vector vv = new Vector(4);
        vv.addElement( new Integer(a));
        vv.addElement(b);
        vv.addElement(c);
        vv.addElement( new Boolean(d));
        Object object = invoke("changeSco", vv);
        return ((Boolean)object).booleanValue();
    }

    public boolean changeSco(int a, java.lang.String b, java.lang.String c, java.lang.String d, boolean e) throws IOException, XmlRpcException
    {
        Vector vv = new Vector(5);
        vv.addElement( new Integer(a));
        vv.addElement(b);
        vv.addElement(c);
        vv.addElement(d);
        vv.addElement( new Boolean(e));
        Object object = invoke("changeSco", vv);
        return ((Boolean)object).booleanValue();
    }

    public boolean changeScoSequenceNr(int a, int b, int c, int d) throws IOException, XmlRpcException
    {
        Vector vv = new Vector(4);
        vv.addElement( new Integer(a));
        vv.addElement( new Integer(b));
        vv.addElement( new Integer(c));
        vv.addElement( new Integer(d));
        Object object = invoke("changeScoSequenceNr", vv);
        return ((Boolean)object).booleanValue();
    }

    public boolean deleteSco(int a) throws IOException, XmlRpcException
    {
        Vector vv = new Vector(1);
        vv.addElement( new Integer(a));
        Object object = invoke("deleteSco", vv);
        return ((Boolean)object).booleanValue();
    }

    public boolean deleteSchool(int a) throws IOException, XmlRpcException
    {
        Vector vv = new Vector(1);
        vv.addElement( new Integer(a));
        Object object = invoke("deleteSchool", vv);
        return ((Boolean)object).booleanValue();
    }

    public java.util.Hashtable getFidentitySchools() throws IOException, XmlRpcException
    {
        Vector vv = new Vector(0);
        Object object = invoke("getFidentitySchools", vv);
        return (java.util.Hashtable)object;
    }

    public java.util.Vector getUserResults(int a, int b) throws IOException, XmlRpcException
    {
        Vector vv = new Vector(2);
        vv.addElement( new Integer(a));
        vv.addElement( new Integer(b));
        Object object = invoke("getUserResults", vv);
        return (java.util.Vector)object;
    }

    public java.util.Vector getUserResults(java.util.Vector a, int b) throws IOException, XmlRpcException
    {
        Vector vv = new Vector(2);
        vv.addElement(a);
        vv.addElement( new Integer(b));
        Object object = invoke("getUserResults", vv);
        return (java.util.Vector)object;
    }

    public boolean setLogo(int a, byte[] b) throws IOException, XmlRpcException
    {
        Vector vv = new Vector(2);
        vv.addElement( new Integer(a));
        vv.addElement(b);
        Object object = invoke("setLogo", vv);
        return ((Boolean)object).booleanValue();
    }

    public boolean changeCourse(int a, java.lang.String b, java.lang.String c, boolean d) throws IOException, XmlRpcException
    {
        Vector vv = new Vector(4);
        vv.addElement( new Integer(a));
        vv.addElement(b);
        vv.addElement(c);
        vv.addElement( new Boolean(d));
        Object object = invoke("changeCourse", vv);
        return ((Boolean)object).booleanValue();
    }

    public boolean editSchool(int a, boolean b) throws IOException, XmlRpcException
    {
        Vector vv = new Vector(2);
        vv.addElement( new Integer(a));
        vv.addElement( new Boolean(b));
        Object object = invoke("editSchool", vv);
        return ((Boolean)object).booleanValue();
    }

    public java.util.Vector getImportCourses(int a, int b, int c) throws IOException, XmlRpcException
    {
        Vector vv = new Vector(3);
        vv.addElement( new Integer(a));
        vv.addElement( new Integer(b));
        vv.addElement( new Integer(c));
        Object object = invoke("getImportCourses", vv);
        return (java.util.Vector)object;
    }

    public java.util.Hashtable editSchool(int a, java.lang.String b, java.lang.String c, java.util.Hashtable d) throws IOException, XmlRpcException
    {
        Vector vv = new Vector(4);
        vv.addElement( new Integer(a));
        vv.addElement(b);
        vv.addElement(c);
        vv.addElement(d);
        Object object = invoke("editSchool", vv);
        return (java.util.Hashtable)object;
    }

    public java.util.Hashtable addSchool(int a, java.lang.String b, java.lang.String c, java.util.Hashtable d) throws IOException, XmlRpcException
    {
        Vector vv = new Vector(4);
        vv.addElement( new Integer(a));
        vv.addElement(b);
        vv.addElement(c);
        vv.addElement(d);
        Object object = invoke("addSchool", vv);
        return (java.util.Hashtable)object;
    }

    public boolean deleteUserFromSchool(int a, int b) throws IOException, XmlRpcException
    {
        Vector vv = new Vector(2);
        vv.addElement( new Integer(a));
        vv.addElement( new Integer(b));
        Object object = invoke("deleteUserFromSchool", vv);
        return ((Boolean)object).booleanValue();
    }

    public boolean updateSchoolTo(int a, java.util.Vector b) throws IOException, XmlRpcException
    {
        Vector vv = new Vector(2);
        vv.addElement( new Integer(a));
        vv.addElement(b);
        Object object = invoke("updateSchoolTo", vv);
        return ((Boolean)object).booleanValue();
    }

    public boolean deleteCourseDataFromClass(int a, int b) throws IOException, XmlRpcException
    {
        Vector vv = new Vector(2);
        vv.addElement( new Integer(a));
        vv.addElement( new Integer(b));
        Object object = invoke("deleteCourseDataFromClass", vv);
        return ((Boolean)object).booleanValue();
    }

    public java.util.Vector getResultCount(int a, int b) throws IOException, XmlRpcException
    {
        Vector vv = new Vector(2);
        vv.addElement( new Integer(a));
        vv.addElement( new Integer(b));
        Object object = invoke("getResultCount", vv);
        return (java.util.Vector)object;
    }

}
