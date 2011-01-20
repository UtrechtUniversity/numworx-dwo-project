// Source file:
// N:\\transferzone\\intern\\Afstudeerders_basw_thijsk\\April\\Implementatie\\fi\\dwo\\client\\persistence\\ScoMapper.java

package fi.dwo.client.persistence;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Hashtable;
import java.util.Vector;

import org.apache.xmlrpc.applet.XmlRpcException;

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

    
    class LazySco extends Sco {

		public Hashtable getLaunchdata() {
			if(this.launchdata != null)
				return launchdata;
			Hashtable ht = new Hashtable();
			ht.put(getIDCol(), new Integer(getID()));
//System.out.println("request launchdata for " + getID());
//new Throwable().printStackTrace();
			Vector v = LAZY_SCO_KEYS;
			v.add("launchdata");
	        DbAccessIF dbAccess = DbAccessCreator.instance();
	        try {
				v = dbAccess.getTable(getTableName(), v, ht, getOrderbyCol());
		        if(v.size() != 0)
		        {
		        	ht = (Hashtable) v.firstElement();
		        	String ld = ht.get("launchdata").toString();
		        	if(ld.length()>0)
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
        System.err.println("ScoMapper.put() Not yet implemented!");
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
    		String tableName = "tblsco left join tblcourse on tblsco.courseid = tblcourse.courseid";
    		String orderBy = "sconame";
    		Vector data = DbAccessCreator.instance().getTable(tableName, LAZY_SCO_KEYS, wheredef , orderBy);
    		return getObjectFromReturn(data);	
        }
        
        
        return get(ht);
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
    	LAZY_SCO_KEYS.add("tblsco.courseID");
    	LAZY_SCO_KEYS.add("appletID");
    	LAZY_SCO_KEYS.add("tblsco.description");
    	LAZY_SCO_KEYS.add("scoID");
    	LAZY_SCO_KEYS.add("sconame");
    	LAZY_SCO_KEYS.add("sequencenr");
    	LAZY_SCO_KEYS.add("showscore");
    }
    public static boolean hasShowScore = true;
    
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
}