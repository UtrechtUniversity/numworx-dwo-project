package fi.dwo.client.persistence;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Hashtable;

import org.apache.xmlrpc.applet.XmlRpcException;

import fi.dwo.client.domain.Course;
import fi.dwo.client.domain.CourseSequence;
import fi.dwo.client.domain.School;
import fi.dwo.client.domain.SchoolClass;
import fi.dwo.client.system.PersistenceException;

public class CourseSequenceMapper extends XmlRpcMapper {
	

	private static final String IDCOL = "coursesequenceID";
	private static final String ORDERBYCOL = "sequencenr";
	private static final String TABLENAME = "tblCourseSequence";
	private static final Integer NUL = new Integer(0);

	protected Object[] createArray(int size) {
		return new CourseSequence[size];
	}

	protected String getIDCol() {
		return IDCOL;
	}

	protected String getOrderbyCol() {
		return ORDERBYCOL;
	}

	protected String getTableName() {
		return TABLENAME;
	}

	protected Object update(Object obj, Hashtable data) {
		CourseSequence cs = (CourseSequence) obj;
		
		int courseSequenceID = ((Number) data.get(IDCOL)).intValue();
		cs.setID(courseSequenceID);
		int courseID = ((Number) data.get("courseID")).intValue();
		Course course;
		try {
			course = (Course) MapperCreator.instance(Course.class).get(courseID);
		} catch (Exception e) {
			return null;
		} 
		cs.setCourse(course);
		int sequencenr = ((Number) data.get("sequencenr")).intValue();
		cs.setSequencenr(sequencenr);
// optional parts
		int schoolID = ((Number) data.get("schoolID")).intValue();
		if(schoolID != 0)
		{
			try {
				cs.setSchool((School) MapperCreator.instance(School.class).get(schoolID));
			} catch (Exception e) {
				return null;
			}
		}
		int classID = ((Number) data.get("classID")).intValue();
		if(classID != 0)
		{
			try {
				cs.setSchoolClass((SchoolClass) MapperCreator.instance(SchoolClass.class).get(classID));
			} catch (Exception e) {
				return null;
			} 
		}
// TODO CourseMap
		int parentID = ((Number) data.get("parent")).intValue();
		cs.setParentID(parentID);	
		return cs;
	}

	public Object[] get(Object obj) throws IOException, SQLException,
			XmlRpcException {
        Hashtable ht = new Hashtable();
        if(obj instanceof SchoolClass) {
            SchoolClass sc = (SchoolClass) obj;
            ht.put("classID", new Integer(sc.getID()));
        } else if(obj instanceof School)
        {
        	School s = (School) obj;
        	ht.put("schoolID", new Integer(s.getSchoolID()));
        } else if(obj == null)
        {
        	ht.put("schoolID", NUL);
        }
        return super.get(ht);
	}

	public Object getObjectFromReturn(Hashtable data) throws IOException,
			SQLException, XmlRpcException {
        CourseSequence c = null;
        if (data.get(IDCOL) == null) { //We don't know enough to make a
                                            // courseobject
            return null;
        } else if (objects.containsKey(data.get(IDCOL))) { // Did we know
                                                                // the course?
            c = (CourseSequence) objects.get(data.get(IDCOL));
        } else {
            c = new CourseSequence();
        }
        c = (CourseSequence) update(c, data);
        if(!objects.containsKey(new Integer(c.getID()))) {
            objects.put(new Integer(c.getID()), c);
        }
        return c;
	}

	public void put(int oid, Object obj) throws IOException, SQLException,
			XmlRpcException {
		System.err.println("CourseSequenceMapper.put() Not yet implemented!");
	}

}
