// Source file:
// N:\\transferzone\\intern\\Afstudeerders_basw_thijsk\\April\\Implementatie\\fi\\dwo\\client\\persistence\\MapperIF.java
package fi.dwo.dwojapplet.persistence;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Hashtable;
import java.util.Vector;

import org.apache.xmlrpc.applet.XmlRpcException;

/**
 * A mapper which can map database-hashtables on objects. e.g. the data out the
 * coursetable are read out of the database and put in a hashtable (on the
 * serverside). These hashtable is converted in a Course object.
 * <img src="doc-files/MapperCreator-1.gif" alt="diagram of the converting of a hashtable to a Course-object">
 *
 * @author M.J.B. Kupers
 *
 */
public interface MapperIF {

    /**
     * Returns a object of the class, with the specified objectID.<br>
     * e.g. if this is a CourseMapper and the objectID is 1, a Course object
     * representing cours nr 1 (the ID field in the database) is returned.
     *
     * @param oid The ID of the object to get.
     * @return Object The object representing the specified objectID and class.
     * @throws java.sql.SQLException
     *
     */
    public Object get(int oid) throws IOException, XmlRpcException,
            SQLException;

    /**
     * This method saves an object in the database.<br>
     * NOT IMPLEMENTED!!
     *
     * @param oid
     * @param obj
     * @throws org.apache.xmlrpc.applet.XmlRpcException
     */
    public void put(int oid, Object obj) throws IOException, SQLException,
            XmlRpcException;

    /**
     * Returns all the objects of this mapper.<br>
     * e.g. if this is the CourseMapper, all the Course objects representing the
     * courses in the database are returned.
     *
     * @return The objects representing the specified class.
     * @throws org.apache.xmlrpc.applet.XmlRpcException
     */
    public Object[] get() throws IOException, SQLException, XmlRpcException;

    /**
     * Creates an object out of the Hashtable. If this is the CourseMapper, a
     * Course-Object will be returned.
     *
     * @param data The Hashtable with the data.
     * @return The object from the mapper.
     * @throws IOException
     * @throws SQLException
     * @throws XmlRpcException
     */
    public Object getObjectFromReturn(Hashtable data) throws IOException, SQLException, XmlRpcException;

    /**
     * Creates all the objects out of the Vector. If this is the CourseMapper,
     * Course-Objects will be returned.
     *
     * @param data The Vector containing hashtables to convert.
     * @return An array of objects, representing this mapper.
     * @throws IOException
     * @throws SQLException
     * @throws XmlRpcException
     */
    public Object[] getObjectFromReturn(Vector data) throws IOException, SQLException, XmlRpcException;

    /**
     * Returns all the objects whith the object as restriction. If this is the
     * CourseMapper, Course-Objects will be returned. A restriction could be a
     * school. Only the courses for the specific are returned. Which objects can
     * be specified as a restriction are definied at the Mapper-Specific
     * documentation.
     *
     * @param obj The object who specifies the restriction.
     * @return The objects who satisfy to the restriction.
     * @throws org.apache.xmlrpc.applet.XmlRpcException
     */
    public Object[] get(Object obj) throws IOException, SQLException,
            XmlRpcException;

    /**
     * Removes the object with the specified ID from the cache.
     *
     * @param key The key of the object to remove.
     */
    public void removeObject(int key);

    /**
     * Removes all the objects from the cache.
     *
     */
    public void removeAllObjects();
}
