// Source file:
// N:\\transferzone\\intern\\Afstudeerders_basw_thijsk\\April\\Implementatie\\fi\\dwo\\client\\persistence\\CourseMapper.java

package fi.dwo.client.persistence;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.WeakHashMap;

import org.apache.xmlrpc.applet.XmlRpcException;

import fi.dwo.client.domain.ClassCourse;
import fi.dwo.client.domain.Course;
import fi.dwo.client.domain.CourseMap;
import fi.dwo.client.domain.DwoHelper;
import fi.dwo.client.domain.DwoIF;
import fi.dwo.client.domain.School;
import fi.dwo.client.domain.SchoolClass;

public class CourseMapper extends XmlRpcMapper {

    private static final String TABLENAME = "tblCourse";

    private static final String IDCOL = "courseID";

    private static final String ORDERCOL = "name";

	private static Map cachemap = new HashMap(); // not weak

	private Object key;

    /**

     */
    public CourseMapper() {

    }

    /**
     * Put a new object into the cache...
     * NOT into the DATABASE
     * @param oid
     * @param obj

     */
    public void put(int oid, Object obj) throws IOException, SQLException,
            XmlRpcException {
        objects.put(new Integer(oid), obj);
        cachemap.clear();
    }

    
    class LazyCourse extends Course {
    	private boolean loaded;
    	
    	public void setChildren(Course[] children) {
    		loaded = children != NO_CHILDREN;
    		super.setChildren(children);
    	}

		/* (non-Javadoc)
		 * @see fi.dwo.client.domain.Course#getChildren()
		 */
		public CourseMap[] getChildren() {
			if(!loaded)
			{
				try {
					setChildren(PersistenceFacade.instance().sequence((Course[]) get(this)));
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			return super.getChildren();
		}
    	    	
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
            c = new LazyCourse();
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
        	ht.put("parentID", new Integer(0));
            int profileID = ((DwoIF) DwoHelper.getApplet()).getDwoProfile().getID();
            ht.put("dwoProfileID", new Integer(profileID));
        }
        return cached(ht); // was super.get(ht);
    }

    
    
    
    
/* (non-Javadoc)
	 * @see fi.dwo.client.persistence.XmlRpcMapper#get(int)
	 */
	public Object get(int oid) throws IOException, XmlRpcException,
			SQLException {
		Integer Oid = new Integer(oid);
		Object result = objects.get(Oid);
		if(result != null)
			return result;
		// TODO find course in cache.....
		System.out.println("request Course " + oid);
		Iterator v = cachemap.values().iterator();
		while (v.hasNext()) {
			Vector vv = (Vector) v.next();
			Iterator vvv = vv.iterator();
			while (vvv.hasNext()) {
				Hashtable map = (Hashtable) vvv.next();
				if (Oid.equals( map.get(IDCOL)))
				{
					return getObjectFromReturn(map);
				}
			}
			
		}
		return super.get(oid);
	}

/**
 * @param ht
 * @return
 * @throws IOException
 * @throws XmlRpcException
 * @throws SQLException
 */
private Object[] cached(Hashtable ht) throws IOException, XmlRpcException,
		SQLException {
	Object[] result;
	Vector v;
	v = (Vector) cachemap.get(ht);
// easy found in cache, return objects
	if(v != null)
	{
		//System.out.println("Found in cache " + ht);
		return super.getObjectFromReturn(v);
	}
	//System.out.println("cache miss for " + ht);
// not found in cache, perhaps school+parentid=0
	Object parent = ht.remove("parentID");
	DbAccessIF dbAccess = DbAccessCreator.instance();
	if(parent != null)
	{
// ht contains a parent + school or single parent.
		if(!ht.isEmpty())
		{
// get all courses from a school
			v = (Vector)cachemap.get(ht);
			if(v == null)
			{	v = dbAccess.getTable(getTableName(), ht, getOrderbyCol());
				cachemap.put(ht, v);
			}
// v is all courses from school, filter parent = 0, fill cachemap with parent != 0
			v = filterParent(v, (Integer)parent);
// put parent back, v is filtered
			ht.put("parentID", parent);
		}
		else 
		{
// not from cache, get with parent from database.
			ht.put("parentID", parent);
			v = dbAccess.getTable(getTableName(), ht, getOrderbyCol());
		}
	} else
// not in cache, no parent, use database
		v = dbAccess.getTable(getTableName(), ht, getOrderbyCol());
	
//System.out.println("put " + v.size() + " for  " + ht);
	cachemap.put(ht, v);
	return super.getObjectFromReturn(v);
}

	private Vector filterParent(Vector result, Integer parent) {
	Vector v = new Vector();
	Enumeration en = result.elements();
	while (en.hasMoreElements()) {
		Hashtable ht = (Hashtable) en.nextElement();
		//System.out.println("HT: " + ht);
		Object hp = ht.get("parentID");
		if(parent.equals(hp))
			v.add(ht);
		else
		{
// this is the tricky part!!!!!
// prepare cache with other parents.
			Hashtable htt = new Hashtable();
			htt.put("parentID", hp);
			Vector v2 = (Vector) cachemap.get(htt);
			if(v2 == null)
			{
				//System.out.println("priming for p-" + hp);
				v2 = new Vector(); cachemap.put(htt, v2);
			}
			v2.remove(ht); // v2 is a sorted set? FIXME a real SET?
			v2.add(ht);
		}
		Object wc = ht.get("withChildren");
		if(Boolean.TRUE.equals(wc))
		{
			Object id = ht.get("courseID");
			//System.out.println("priming for i-" + id);
			Hashtable htt = new Hashtable();
			htt.put("parentID", id);
			if(!cachemap.containsKey(htt))
				cachemap.put(htt, new Vector());
		}
	}
	
	return v;
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
        	if(c instanceof LazyCourse)
        	{
        		c.setChildren(Course.NO_CHILDREN);
        	} else
         	try {
				c.setChildren(PersistenceFacade.instance().sequence((Course[])get(c))); // Not Lazy, .... jammer dan.
			} catch (Exception e) {
				c.setChildren(Course.NO_CHILDREN);
			} 
        } else 
        	c.setChildren(null);
        if(data.containsKey("ClassCourseID"))
        {
        	try {
				c.link = (ClassCourse) MapperCreator.instance(ClassCourse.class).getObjectFromReturn(data);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
        } else 
        {	if(c.link != null)
        	{
        		System.err.println("erase link for " + c);
        	}
        	//c.link = null; // FIXME is dit correct?
        }
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

	/* (non-Javadoc)
	 * @see fi.dwo.client.persistence.XmlRpcMapper#removeAllObjects()
	 */
	public void removeAllObjects() {
//System.out.println("cachemap clear all");
		cachemap.clear();
		super.removeAllObjects();
	}

	/* (non-Javadoc)
	 * @see fi.dwo.client.persistence.XmlRpcMapper#removeObject(int)
	 */
	public void removeObject(int key) {
//System.out.println("cachemap clear key");
		cachemap.clear();
		super.removeObject(key);
	}

	/* (non-Javadoc)
	 * @see fi.dwo.client.persistence.XmlRpcMapper#getObjectFromReturn(java.util.Vector)
	 */
	public Object[] getObjectFromReturn(Vector data) throws IOException,
			SQLException, XmlRpcException {
		cachemap.put(key, data);
		return super.getObjectFromReturn(data);
	}
}