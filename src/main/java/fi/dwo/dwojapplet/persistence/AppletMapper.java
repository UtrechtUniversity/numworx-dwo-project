/*
 * Created on Mar 2, 2005
 *
 */
package fi.dwo.dwojapplet.persistence;

import fi.dwo.dwojapplet.sytem.Loader;
import fi.dwo.dwojapplet.domain.DwoHelper;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Hashtable;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.xmlrpc.applet.XmlRpcException;

/**
 * @author M.J.B. Kupers
 *
 */
public class AppletMapper extends XmlRpcMapper {

    private static final Logger log = Logger.getLogger(AppletMapper.class.getName());

    private static final char CLASSLOADER = 'c';

    private static final String TABLENAME = "tblApplet";

    private static final String IDCOL = "appletID";

    private static final String ORDERCOL = "classname";

    /**
     *
     */
    public AppletMapper() {

    }

    /**
     * @param oid
     * @param obj
     * @throws java.io.IOException
     * @throws java.sql.SQLException
     * @throws org.apache.xmlrpc.applet.XmlRpcException
     *
     */
    @Override
    public void put(int oid, Object obj) throws IOException, SQLException,
            XmlRpcException {
        System.err.println("AppletMapper.put() Not yet implemented!");
    }

    /**
     * @param data
     * @return Object
     * @throws java.io.IOException
     * @throws org.apache.xmlrpc.applet.XmlRpcException
     * @throws java.sql.SQLException
     *
     */
    @Override
    public Object getObjectFromReturn(Hashtable data) throws IOException, SQLException, XmlRpcException {
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
        if (!objects.containsKey(data.get("appletID"))) {
            //objects.put(data.get("appletID"), a);
        }
        return a;
    }

    /**
     * @param obj
     * @return Object[]
     * @throws java.io.IOException
     * @throws org.apache.xmlrpc.applet.XmlRpcException
     * @throws java.sql.SQLException
     *
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
    @SuppressWarnings("UnusedAssignment")
    protected Object update(Object obj, Hashtable data) throws IOException, SQLException, XmlRpcException {
        Class a = null;
        String jarname = (String) data.get("jarname");
        String className = (String) data.get("classname");

        //Try loading class from remote server.
        try {
            if(DwoHelper.getGetResourceURLPathString()!=null) Loader.setPrefix(DwoHelper.getGetResourceURLPathString());
            a = Loader.create(jarname).loadClass(className);
            return a;
        } catch (ClassNotFoundException e1) {
            //try loading the jar locally (might be updated).
            log.log(Level.FINE, "Can't load class {0} in jar {1} from remote server. ", new Object[]{className, jarname});
            if (DwoHelper.isSecure()) {
                try {
                    a = Class.forName(className);
                    return a;
                } catch (ClassNotFoundException e2) {
                    e1 = e2;
                }
            }
            log.log(Level.SEVERE, null, e1);
            throw new XmlRpcException(-1, "Class not found.");
        }
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
        if (object != null) {
            objects.put(oid, object);
        }
        return object;
    }

}
