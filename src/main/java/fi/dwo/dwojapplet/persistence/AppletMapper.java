/*
 * Created on Mar 2, 2005
 *
 */
package fi.dwo.dwojapplet.persistence;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Hashtable;

//import fi.oppervlakte_dwo.*;

import org.apache.xmlrpc.applet.XmlRpcException;

import fi.dwo.commons.system.Loader;
import fi.dwo.dwojapplet.domain.DwoHelper;


/**
 * @author M.J.B. Kupers
 *
 */
public class AppletMapper extends XmlRpcMapper {

    private static final char CLASSLOADER = 'c';

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
    @Override
    public void put(int oid, Object obj) throws IOException, SQLException,
            XmlRpcException {
        System.err.println("AppletMapper.put() Not yet implemented!");
    }

    /**
     * @param data
     * @return Object

     */
    @Override
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
    @Override
    public Object[] get(Object obj) throws IOException, SQLException,
            XmlRpcException {
        return get();
    }

    /*
     * (non-Javadoc)
     * 
     * @see fi.dwo.client.persistence.XmlRpcMapper#getIDCol()
     */
    @Override
    protected String getIDCol() {
        return IDCOL;
    }

    /*
     * (non-Javadoc)
     * 
     * @see fi.dwo.client.persistence.XmlRpcMapper#getTableName()
     */
    @Override
    protected String getTableName() {
        return TABLENAME;
    }

    /*
     * (non-Javadoc)
     * 
     * @see fi.dwo.client.persistence.XmlRpcMapper#update(java.lang.Object,
     *      java.util.Hashtable)
     */
    @Override
    protected Object update(Object obj, Hashtable data) throws IOException, SQLException, XmlRpcException  {
        Class a = null;
        String jarname = (String) data.get("jarname");
        String className = (String) data.get("classname");
        try {
			a = Class.forName(className);
        } catch (ClassNotFoundException e1) {
            String features = (String) data.get("features");
        	if(DwoHelper.isSecure() && features != null && features.indexOf(CLASSLOADER)>=0)
        	try {
				a = Loader.create(jarname).loadClass(className);
				return a;
        	} catch (ClassNotFoundException e) {
        		e1 = e;
			}
			e1.printStackTrace();
            throw new XmlRpcException(-1, "Class not found");
        }
	    
	   return a;
    }

    /* (non-Javadoc)
     * @see fi.dwo.client.persistence.XmlRpcMapper#createArray(int)
     */
    @Override
    protected Object[] createArray(int size) {
        return new Class[size];
    }
    /* (non-Javadoc)
     * @see fi.dwo.client.persistence.XmlRpcMapper#getOrderbyCol()
     */
    @Override
    protected String getOrderbyCol() {
        return ORDERCOL;
    }

	/* (non-Javadoc)
	 * @see fi.dwo.client.persistence.XmlRpcMapper#get(int)
	 */
    @Override
	public Object get(int oid) throws IOException, XmlRpcException,
			SQLException {
		Object object = super.get(oid);
		if(object != null)
			objects.put(new Integer(oid), object);
		return object;
	}
    
    
    
}