/*
 * Created on Mar 2, 2005
 *
 */
package fi.dwo.client.persistence;

import java.applet.Applet;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Hashtable;
import java.util.Hashtable;
import java.lang.ref.SoftReference;

//import fi.oppervlakte_dwo.*;

import org.apache.xmlrpc.applet.XmlRpcException;

import fi.dwo.client.domain.Course;
import fi.dwo.client.domain.DwoHelper;
import fi.dwo.client.system.Loader;


/**
 * @author M.J.B. Kupers
 *
 */
public class AppletMapper extends XmlRpcMapper {

    private static final String TABLENAME = "tblApplet";

    private static final String IDCOL = "appletID";

    private static final String ORDERCOL = "classname";

    /**

     */
    public AppletMapper() {

    }

    /**
     * @param oid
     * @param obj

     */
    public void put(int oid, Object obj) throws IOException, SQLException,
            XmlRpcException {
        System.err.println("AppletMapper.put() Not yet implemented!");
    }

    /**
     * @param data
     * @return Object

     */
    public Object getObjectFromReturn(Hashtable data) throws IOException, SQLException, XmlRpcException  {
        Class a = null;
        if (data.get("appletID") == null) { //We don't know enough to make a
                                            // appletObject
            return null;
        } else if (objects.containsKey(data.get("appletID"))) { // Did we know
                                                                // the applet?
            a = (Class) objects.get(data.get("appletID"));
        } else {
            a = (Class) update(a, data);
        }
        if(!objects.containsKey(data.get("appletID"))) {
           	//objects.put(data.get("appletID"), a);
        }
        return a;
    }

    /**
     * @param obj
     * @return Object[]

     */
    public Object[] get(Object obj) throws IOException, SQLException,
            XmlRpcException {
        return get();
    }

    /*
     * (non-Javadoc)
     * 
     * @see fi.dwo.client.persistence.XmlRpcMapper#getIDCol()
     */
    protected String getIDCol() {
        return IDCOL;
    }

    /*
     * (non-Javadoc)
     * 
     * @see fi.dwo.client.persistence.XmlRpcMapper#getTableName()
     */
    protected String getTableName() {
        return TABLENAME;
    }

    /*
     * (non-Javadoc)
     * 
     * @see fi.dwo.client.persistence.XmlRpcMapper#update(java.lang.Object,
     *      java.util.Hashtable)
     */
    protected Object update(Object obj, Hashtable data) throws IOException, SQLException, XmlRpcException  {
        Class a = null;
        String jarname = (String) data.get("jarname");
        String className = (String) data.get("classname");
        
        try {
			a = Class.forName(className);
        } catch (ClassNotFoundException e1) {
        	try {
				a = Loader.create(jarname).loadClass(className);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
	            throw new XmlRpcException(-1, "Class not found");
			}
        }
	    
	   return a;
    }

    /* (non-Javadoc)
     * @see fi.dwo.client.persistence.XmlRpcMapper#createArray(int)
     */
    protected Object[] createArray(int size) {
        return new Class[size];
    }
    /* (non-Javadoc)
     * @see fi.dwo.client.persistence.XmlRpcMapper#getOrderbyCol()
     */
    protected String getOrderbyCol() {
        return ORDERCOL;
    }

	/* (non-Javadoc)
	 * @see fi.dwo.client.persistence.XmlRpcMapper#get(int)
	 */
	public Object get(int oid) throws IOException, XmlRpcException,
			SQLException {
		Object object = super.get(oid);
		if(object != null)
			objects.put(new Integer(oid), object);
		return object;
	}
    
    
    
}