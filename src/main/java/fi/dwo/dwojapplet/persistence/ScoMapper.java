// Source file:
// N:\\transferzone\\intern\\Afstudeerders_basw_thijsk\\April\\Implementatie\\fi\\dwo\\client\\persistence\\ScoMapper.java

package fi.dwo.dwojapplet.persistence;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.WeakHashMap;
import java.util.zip.GZIPInputStream;

import org.apache.xmlrpc.applet.XmlRpcException;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import fi.beans.base64code.StringCodeObject;
import fi.dwo.client.domain.Course;
import fi.dwo.client.domain.DwoProfile;
import fi.dwo.client.domain.School;
import fi.dwo.client.domain.Sco;

public class ScoMapper extends XmlRpcMapper {

	private static final Vector LAZY_SCO_KEYS = new Vector();

	private static final String TABLENAME = "tblSco";

    private static final String IDCOL = "scoID";

    private static final String ORDERCOL = "sequencenr";

    private static Map cachemap = new HashMap(); // was weakhashmap
    
    class LazySco extends Sco {

		public Hashtable getLaunchdata() {
			if(this.launchdata != null)
				return launchdata;
			Hashtable ht = new Hashtable();
			ht.put(getIDCol(), new Integer(getID()));
//System.out.println("request launchdata for " + getID());
//new Throwable().printStackTrace();
			Vector v = new Vector();
			v.add("launchdata");
			if(hasFeature(JSON_IN))
				v.add("launchdatabytes");
	        DbAccessIF dbAccess = DbAccessCreator.instance();
	        try {
				v = dbAccess.getTable(getTableName(), v, ht, getOrderbyCol());
		        if(v.size() != 0)
		        {
		        	ht = (Hashtable) v.firstElement();
		        	
		        	Object o = ht.get("launchdatabytes");
		        	if(o instanceof byte[]) {
		        		InputStream in = new GZIPInputStream(new ByteArrayInputStream((byte[]) o));
		        		Map map = (Map) JSONValue.parse(new InputStreamReader(in, "UTF-8"));
		        		Hashtable h = new Hashtable();
		        		Set entrySet = map.entrySet();
						for (Iterator iterator = entrySet.iterator(); iterator
								.hasNext();) {
							Map.Entry entry = (Map.Entry) iterator.next();
		        			Object key = entry.getKey();
		        			Object value = entry.getValue().toString();
		        			h.put(key, value);
		        		}
		        		setLaunchdata(h);
		        		setDataChanged(false);
		        	}
		        	
		        	String ld = (String) ht.get("launchdata");
		        	if(ld != null && ld.length()>0)
		        	{
		        		setLaunchdata((Hashtable) StringCodeObject.decodeStringToObject(ld));
		        		setDataChanged(false);
		        	}
		        }
			} catch (IOException e) {
				e.printStackTrace();
			} catch (XmlRpcException e) {
				e.printStackTrace();
			} catch (SQLException e) {
				e.printStackTrace();
			}

			return super.getLaunchdata();
		}
    	
    }
    
    
    
    /**

     */
    public ScoMapper() {

    }

    /**
     * @param oid
     * @param obj

     */
    public void put(int oid, Object obj) throws IOException, SQLException,
            XmlRpcException {
        objects.put(new Integer(oid), obj);
        cachemap.clear();
    }

    /**
     * @param data
     * @return Object

     */
    public Object getObjectFromReturn(Hashtable data) throws IOException, SQLException, XmlRpcException {
        Sco s = null;
        if (data.get("scoID") == null) { //We don't know enough to make a
                                         // scoobject
            return null;
        } else if (objects.containsKey(data.get("scoID"))) { // Did we know the
                                                             // sco?
            s = (Sco) objects.get(data.get("scoID"));
            if(!data.contains("launchdata"))
            	data.put("launchdata", "");
            //System.out.println("reuse " + s + " for " + data.get("scoID"));
        } else {
            if(!data.contains("launchdata"))
            { s = new LazySco();
            	data.put("launchdata","");
            } else {
            	s = new Sco();
            }
            //System.out.println("use " + s + " for " + data.get("scoID"));
        }
        s = (Sco) update(s, data);
        if(!objects.containsKey(new Integer(s.getID()))) {
            objects.put(new Integer(s.getID()), s);
        }
        return s;
    }

    /**
     * Returns all the SCO's with the object as restriction.
     * @param obj The object who specifies the restriction. possible objects are:
     * <ul>
     * <li><code>Course</code>: The sco's of the specified course are returned;
     * </ul>
     * @return The SCO's who satisfies to the restriction. 
     */
    public Object[] get(Object obj) throws IOException, SQLException,
            XmlRpcException {
        Hashtable ht = new Hashtable();
        if(obj instanceof Course) {
            Course c = (Course) obj;
            ht.put("courseID", new Integer(c.getID()));
        } else
        if(obj instanceof Object[])
        {
        	Object[] objs = (Object[]) obj;
        	School school = (School) objs[0];
        	DwoProfile profile = (DwoProfile) objs[1];        
    		Hashtable wheredef = new Hashtable();
    		wheredef.put("schoolID", new Integer(school.getSchoolID()));
    		wheredef.put("dwoprofileid", new Integer(profile.getID()));
    		String tableName = "tblSco left join tblCourse on tblSco.courseid = tblCourse.courseid";
    		String orderBy = "sconame";
    		
    		Vector data = (Vector)cachemap.get(wheredef);
    		if(data == null)
    		{	data = DbAccessCreator.instance().getTable(tableName, LAZY_SCO_KEYS, wheredef , orderBy);
    			cachemap.put(wheredef, data);
    		}
    		else return getObjectFromReturn(data);
    		return fillcache(getObjectFromReturn(data));	
        }
        return cached(ht);
    }

    private Object[] fillcache(Object[] data) {
    	HashMap ht = new HashMap();
    	Sco[] sco = (Sco[])data;
    	for (int i = 0; i < sco.length; i++) {
			Integer course = new Integer(sco[i].getCourse().getID());
			Vector v = (Vector) ht.get(course);
			if(v == null) { v = new Vector(); ht.put(course, v);}
			v.add(sco[i]);
		}
    	Set set = ht.entrySet();
    	Iterator iter = set.iterator();
    	while (iter.hasNext()) {
			Map.Entry entry = (Map.Entry) iter.next();
			Vector v = (Vector) entry.getValue();
			Object key = entry.getKey();
			Collections.sort(v, new Comparator() {

				public int compare(Object o1, Object o2) {
					Sco s1 = (Sco)o1;
					Sco s2 = (Sco)o2;
					int i1 = s1.getSequencenr();
					int i2 = s2.getSequencenr();
					return i1<i2?-1:i1==i2?0:1;
				} } );
			Hashtable h = new Hashtable(); h.put("courseID", key);
			cachemap.put(h, v.toArray(createArray(v.size())));
		}
    	
		return data;
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
    protected Object update(Object obj, Hashtable data) throws IOException, SQLException, XmlRpcException {
        Sco s = (Sco) obj;
        s.setScoID(((Integer) data.get("scoID")).intValue());
        s.setName((String) data.get("sconame"));
        s.setDescription((String) data.get("description"));
        s.setSequencenr(((Integer) data.get("sequencenr")).intValue());
        if(!data.get("appletID").equals("")) {
            s.setAppletID(((Integer) data.get("appletID")).intValue());
        }
        
        if(!data.get("courseID").equals("")) {
            Course c = (Course) MapperCreator.instance(Course.class).get(((Integer) data.get("courseID")).intValue());
		    if (c != null) {
		        s.setCourse(c);
		    }
        }
        if(data.containsKey("showscore"))
        {
        	s.setShowScore(!Boolean.TRUE.equals(data.get("showscore"))); // Reverse logic, 
        }
        
        final Object object = data.get("launchdata");
		if(object != null && !object.equals("")) {
            s.setLaunchdata((Hashtable)new StringCodeObject((String) object).toObject());
            s.setDataChanged(false);
		}
// FIXME als s instanceof Lazy, set launchdata null anders meteen ophalen.
/*		else 
 * 		{ s.setLaunchdata stale...
 * 		}		
 */
		
		
		s.setCourseChanged(false);
        return s;
    }

    /* (non-Javadoc)
     * @see fi.dwo.client.persistence.XmlRpcMapper#createArray(int)
     */
    protected Object[] createArray(int size) {
       return new Sco[size];
    }

    /* (non-Javadoc)
     * @see fi.dwo.client.persistence.XmlRpcMapper#getOrderbyCol()
     */
    protected String getOrderbyCol() {
        return ORDERCOL;
    }

    static {
    	LAZY_SCO_KEYS.add("tblSco.courseID");
    	LAZY_SCO_KEYS.add("appletID");
    	LAZY_SCO_KEYS.add("tblSco.description");
    	LAZY_SCO_KEYS.add("scoID");
    	LAZY_SCO_KEYS.add("sconame");
    	LAZY_SCO_KEYS.add("sequencenr");
    	LAZY_SCO_KEYS.add("showscore");
    }
    public static boolean hasShowScore = true;

    private Object[] cached(Hashtable ht) throws IOException, XmlRpcException,
	SQLException {
    	Object[] result;
    	result = (Object[]) cachemap .get(ht);
    	if(result != null)
    	{
    		//System.out.println("Found in cache " + ht);
    		return result;
    	}
    	result = get(ht);
    	//System.out.println("cache miss for " + ht + " size " + result.length);
    	cachemap.put(ht, result);
    	return result;
}

    
    
	public Object[] get(Hashtable wheredef) throws IOException,
			XmlRpcException, SQLException {
		
		DbAccessIF dbAccess = DbAccessCreator.instance();
        try {
			return getObjectFromReturn(dbAccess.getTable(getTableName(), LAZY_SCO_KEYS, wheredef, getOrderbyCol()));
		} catch (XmlRpcException e) {
			if(hasShowScore)
			{
				hasShowScore = false;
				LAZY_SCO_KEYS.remove("showscore");
				return get(wheredef);
			}
			throw e;
		}

		//return super.get(wheredef);
	}

	/* (non-Javadoc)
	 * @see fi.dwo.client.persistence.XmlRpcMapper#removeAllObjects()
	 */
	public void removeAllObjects() {
		cachemap.clear();
		super.removeAllObjects();
	}

	/* (non-Javadoc)
	 * @see fi.dwo.client.persistence.XmlRpcMapper#removeObject(int)
	 */
	public void removeObject(int key) {
		cachemap.clear();
		//super.removeObject(key);
	}
	
	
}