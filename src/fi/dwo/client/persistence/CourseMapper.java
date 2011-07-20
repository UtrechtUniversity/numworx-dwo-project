// Source file:
// N:\\transferzone\\intern\\Afstudeerders_basw_thijsk\\April\\Implementatie\\fi\\dwo\\client\\persistence\\CourseMapper.java

package fi.dwo.client.persistence;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Hashtable;

import org.apache.xmlrpc.applet.XmlRpcException;

import fi.dwo.client.domain.Course;
import fi.dwo.client.domain.DwoHelper;
import fi.dwo.client.domain.DwoIF;
import fi.dwo.client.domain.School;
import fi.dwo.client.domain.SchoolClass;

public class CourseMapper extends XmlRpcMapper {

    private static final String TABLENAME = "tblCourse";

    private static final String IDCOL = "courseID";

    private static final String ORDERCOL = "name";

    /**

     */
    public CourseMapper() {

    }

    /**
     * @param oid
     * @param obj

     */
    public void put(int oid, Object obj) throws IOException, SQLException,
            XmlRpcException {
        System.err.println("CourseMapper.put() Not yet implemented!");
    }

    /**
     * @param data
     * @return Object

     */
    public Object getObjectFromReturn(Hashtable data) {
        Course c = null;
        if (data.get("courseID") == null) { //We don't know enough to make a
                                            // courseobject
            return null;
        } else if (objects.containsKey(data.get("courseID"))) { // Did we know
                                                                // the course?
            c = (Course) objects.get(data.get("courseID"));
        } else {
            c = new Course();
        }
        c = (Course) update(c, data);
        if(!objects.containsKey(new Integer(c.getID()))) {
            objects.put(new Integer(c.getID()), c);
        }
        return c;
    }

//    /**
//     * @param obj
//     * @return Object[]
//
//     */
//    public Object[] get(Object obj) throws IOException, SQLException,
//            XmlRpcException {
//        return get();
//    }

    ////peter
    /**
     * Returns all the Courses with the object as restriction.
     * @param obj The object who specifies the restriction. possible objects are:
     * <ul>
     * <li><code>SchoolClass</code>: the courses of the class are returned;
     * <li><code>School</code>: the courses of the school are returned;
     * </ul>
     * @return The Courses who satisfy to the restriction. 
     */
    public Object[] get(Object obj) throws IOException, SQLException,
            XmlRpcException {
        Hashtable ht = new Hashtable();
        if(obj instanceof Course)
        {
        	Course course = (Course)obj;
        	ht.put("parentID", new Integer(course.getID()));
        } else
        if(obj instanceof SchoolClass) {
            SchoolClass sc = (SchoolClass) obj;
            ht.put("classID", new Integer(sc.getID()));
        } else if(obj instanceof School)
        {
        	School s = (School) obj;
        	ht.put("schoolID", new Integer(s.getSchoolID()));
            int profileID = ((DwoIF) DwoHelper.getApplet()).getDwoProfile().getID();
            ht.put("dwoProfileID", new Integer(profileID));
        }
        return super.get(ht);
    }
    ////peter

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
    static final Integer EEN = new Integer(1);
    /*
     * (non-Javadoc)
     * 
     * @see fi.dwo.client.persistence.XmlRpcMapper#update(java.lang.Object,
     *      java.util.Hashtable)
     */
    protected Object update(Object obj, Hashtable data) {
        Course c = (Course) obj;
        c.setCourseID(((Integer) data.get("courseID")).intValue());
        c.setName((String) data.get("name"));
        c.setDescription((String) data.get("description"));
        c.setImageUrl((String) data.get("image"));
        c.setDwoProfile(((Integer) data.get("dwoProfileID")).intValue());
        try{
        	c.setSchoolID(((Integer) data.get("schoolID")).intValue());
        }
 
        catch(Exception e){}
        try{
        	c.setParentID(((Integer) data.get("parentID")).intValue());
        }
        catch(Exception e){}
        c.resetParent();
        
        try { c.setImageData((byte[]) data.get("imageData"));
        } catch(Exception e){};
        
        c.setExport(Boolean.TRUE.equals(data.get("export")));
        Object w = data.get("withChildren");
        if(Boolean.TRUE.equals(w) || EEN.equals(w) )
        {
        	try {
				c.setChildren((Course[])get(c));
			} catch (Exception e) {
				c.setChildren(Course.NO_CHILDREN);
			} 
        } else 
        	c.setChildren(null);
        return c;
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