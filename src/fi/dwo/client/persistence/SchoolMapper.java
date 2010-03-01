// Source file:
// N:\\transferzone\\intern\\Afstudeerders_basw_thijsk\\April\\Implementatie\\fi\\dwo\\client\\persistence\\SchoolMapper.java

package fi.dwo.client.persistence;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Hashtable;

import org.apache.xmlrpc.applet.XmlRpcException;

import fi.dwo.client.domain.School;
import fi.dwo.client.domain.SchoolGroup;
import fi.dwo.client.domain.SchoolClass;

public class SchoolMapper extends XmlRpcMapper {

	// lazy evaluation.
	// DIT STAAT NU AAN!
	
	static class LazySchool extends School
	{

		/* (non-Javadoc)
		 * @see fi.dwo.client.domain.School#getPasswd(int)
		 */
		public String getPasswd(int groupID) {
			getSchoolGroupList();
			return super.getPasswd(groupID);
		}

		/* (non-Javadoc)
		 * @see fi.dwo.client.domain.School#getClassList()
		 */
		public SchoolClass[] getClassList() {
			SchoolClass[] classes = super.getClassList();
			if(classes != null)
				return classes;
            try {
				setClassList((SchoolClass[]) MapperCreator.instance(SchoolClass.class).get(this));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return new SchoolClass[0];  // FIXME fatal, non fatal, retryable?
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return new SchoolClass[0]; // FIXME
			} catch (XmlRpcException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return new SchoolClass[0]; // FIXME
			}
			
			return super.getClassList();
		}

		/* (non-Javadoc)
		 * @see fi.dwo.client.domain.School#getSchoolGroupList()
		 */
		public SchoolGroup[] getSchoolGroupList() {
			if(super.getSchoolGroupList() == null)
				try {
					setSchoolGroupList((SchoolGroup[]) MapperCreator.instance(SchoolGroup.class).get(this));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (XmlRpcException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
        	return super.getSchoolGroupList();
		}

		/**
		 * assert classList != null.
		 */
		public void addClass(SchoolClass c) {
			//getClassList();
			//super.addClass(c);
			super.setClassList(null);
		}
		/**
		 * assert classList != null.
		 */
		public void deleteClass(SchoolClass schoolClass) {
			//getClassList();
			//super.deleteClass(schoolClass);
			super.setClassList(null); 
		}
		
		
		
	}
	
	
	
	
	private static final String TABLENAME = "tblSchool";

    private static final String IDCOL = "schoolID";

    private static final String ORDERCOL = "schoolName";

    /**

     */
    public SchoolMapper() {

    }

    /**
     * @param oid
     * @param obj

     */
    public void put(int oid, Object obj) throws IOException, SQLException,
            XmlRpcException {
        System.err.println("SchoolMapper.put() Not yet implemented!");

    }

    /**
     * @param data
     * @return Object

     */
    public Object getObjectFromReturn(Hashtable data) throws IOException, SQLException, XmlRpcException {
        School s = null;
        if (data.get("schoolID") == null) { //We don't know enough to make a
                                            // schoolobject
            return null;
        } else if (data.get("schoolID") instanceof String) { //If it is a string, it was null
            return null;
        } else if (objects.containsKey(data.get("schoolID"))) { // Did we knew
                                                                // the school?
            s = (School) objects.get(data.get("schoolID"));
        } else {
            s = new LazySchool();
        }
        s = (School) update(s, data);
        if(!objects.containsKey(new Integer(s.getSchoolID()))) {
            objects.put(new Integer(s.getSchoolID()), s);
        }
        return s;
    }

    /**
     * @param obj
     * @return Object[]

     */
    public Object[] get(Object obj) throws IOException, SQLException,
            XmlRpcException {
    	if(Boolean.TRUE.equals(obj))
    	{
    		Hashtable h = new Hashtable();
    		h.put("export", obj);
    		return super.get(h);
    	}
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
    protected Object update(Object obj, Hashtable data) throws IOException, SQLException, XmlRpcException {
        School s = (School) obj;
        s.setSchoolID(((Integer) data.get("schoolID")).intValue());
        s.setName((String) data.get("schoolName"));
        s.setSchoolLogin((String) data.get("schoollogin"));
        if(!(s instanceof LazySchool))
        	s.setSchoolGroupList((SchoolGroup[]) MapperCreator.instance(SchoolGroup.class).get(s));
        else 
        	s.setSchoolGroupList(null);
        if(data.contains("image") && (!data.get("image").equals(""))) {
            s.setImage((String) data.get("image"));
        }
        if( !(s instanceof LazySchool) && s.getClassList() == null  ) {
            s.setClassList((SchoolClass[]) MapperCreator.instance(SchoolClass.class).get(s));
        } else
        	if(s instanceof LazySchool)
        		s.setClassList(null);
        
        //if(s.getClassList() == null) {
        //    s.setClassList((SchoolClass[]) MapperCreator.instance(SchoolClass.class).get(s));
        //}

        s.setExport(Boolean.TRUE.equals(data.get("export")));
        
        return s;
    }

    /* (non-Javadoc)
     * @see fi.dwo.client.persistence.XmlRpcMapper#createArray(int)
     */
    protected Object[] createArray(int size) {
        return new School[size];
    }

    /* (non-Javadoc)
     * @see fi.dwo.client.persistence.XmlRpcMapper#getOrderbyCol()
     */
    protected String getOrderbyCol() {
        return ORDERCOL;
    }
}