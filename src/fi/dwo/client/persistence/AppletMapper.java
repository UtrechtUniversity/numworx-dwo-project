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
        Applet a = null;
        if (data.get("appletID") == null) { //We don't know enough to make a
                                            // appletObject
            return null;
        } else if (objects.containsKey(data.get("appletID"))) { // Did we know
                                                                // the applet?
            a = (Applet) objects.get(data.get("appletID"));
        } else {
            a = (Applet) update(a, data);
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
        Applet a = null;
        
        String jarname = (String) data.get("jarname");
        DbAccessCreator.instance().selectJar(DwoHelper.getKey(), jarname);
        
        
        
        Class c;
        
        //if(((String) data.get("classname")).equals("fi.oppervlakte_dwo.Oppervlakte_dwo"))
        //{	a = new fi.oppervlakte_dwo.Oppervlakte_dwo();
        //}
        //else
        //{
	        try {
	            c = Class.forName((String) data.get("classname"));
	            a = (Applet)c.newInstance();
	            
	            
	        } catch (ClassNotFoundException e1) {
	            e1.printStackTrace();
	            throw new XmlRpcException(-1, "Class not found");
	        } catch (InstantiationException e) {
	            throw new XmlRpcException(-1, "Class not found");
	        } catch (IllegalAccessException e) {
	            throw new XmlRpcException(-1, "Class not found");
	        }
	    //}   
	    
	   return a;
    }

    /* (non-Javadoc)
     * @see fi.dwo.client.persistence.XmlRpcMapper#createArray(int)
     */
    protected Object[] createArray(int size) {
        return new Course[size];
    }
    /* (non-Javadoc)
     * @see fi.dwo.client.persistence.XmlRpcMapper#getOrderbyCol()
     */
    protected String getOrderbyCol() {
        return ORDERCOL;
    }
}