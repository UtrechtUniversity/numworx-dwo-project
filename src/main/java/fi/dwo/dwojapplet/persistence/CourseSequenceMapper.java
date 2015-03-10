package fi.dwo.dwojapplet.persistence;

import fi.dwo.dwojapplet.domain.CourseSequence;
import fi.dwo.dwojapplet.domain.DwoHelper;
import fi.dwo.dwojapplet.domain.DwoIF;
import fi.dwo.dwojapplet.domain.School;
import fi.dwo.dwojapplet.domain.SchoolClass;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import org.apache.xmlrpc.applet.XmlRpcException;

class CourseSequenceMapper extends XmlRpcMapper {

    private static final String IDCOL = "coursesequenceID";
    private static final String ORDERBYCOL = "sequencenr";
    private static final String TABLENAME = "tblCourseSequence";
    private static final Integer NUL = new Integer(0);

    private Map cache = new HashMap();

    @Override
    protected Object[] createArray(int size) {
        return new CourseSequence[size];
    }

    @Override
    protected String getIDCol() {
        return IDCOL;
    }

    @Override
    protected String getOrderbyCol() {
        return ORDERBYCOL;
    }

    @Override
    protected String getTableName() {
        return TABLENAME;
    }

    @Override
    protected Object update(Object obj, Hashtable data) {
        CourseSequence cs = (CourseSequence) obj;

        int courseSequenceID = ((Number) data.get(IDCOL)).intValue();
        cs.setID(courseSequenceID);
        int courseID = ((Number) data.get("courseID")).intValue();

        cs.setCourseID(courseID);
        int sequencenr = ((Number) data.get("sequencenr")).intValue();
        cs.setSequencenr(sequencenr);
// optional parts
        int schoolID = ((Number) data.get("schoolID")).intValue();
        if (schoolID != 0) {
            try {
                cs.setSchool((School) MapperCreator.instance(School.class).get(schoolID));
            } catch (Exception e) {
                return null;
            }
        }
        int classID = ((Number) data.get("classID")).intValue();
        if (classID != 0) {
            try {
                cs.setSchoolClass((SchoolClass) MapperCreator.instance(SchoolClass.class).get(classID));
            } catch (Exception e) {
                return null;
            }
        }
// TODO CourseMap
        int parentID = ((Number) data.get("parent")).intValue();
        cs.setParentID(parentID);
        int profileID = ((Number) data.get("profileID")).intValue();
        cs.setProfileID(profileID);

        return cs;
    }

    /**
     * get sequencearray. Altijd met het profileID!
     * @return 
     * @throws java.io.IOException 
     * @throws org.apache.xmlrpc.applet.XmlRpcException 
     * @throws java.sql.SQLException 
     */
    @Override
    public Object[] get(Object obj) throws IOException, SQLException,
            XmlRpcException {
        Hashtable ht = new Hashtable();
        if (obj instanceof SchoolClass) {
            SchoolClass sc = (SchoolClass) obj;
            ht.put("classID", new Integer(sc.getID()));
        } else if (obj instanceof School) {
            School s = (School) obj;
            ht.put("schoolID", new Integer(s.getSchoolID()));
            ht.put("classID", NUL);
        } else if (obj == null) {
            ht.put("schoolID", NUL);
            ht.put("classID", NUL);
        }
// extra...
        int profileID
                = ((DwoIF) DwoHelper.getApplet()).getDwoProfile().getID();
        ht.put("profileID", new Integer(profileID));

        return cached(ht);
    }

    private Object[] cached(Hashtable ht) throws IOException, XmlRpcException, SQLException {
        Object result = cache.get(ht);
        if (result == null) {
            result = super.get(ht);
            cache.put(ht, result);
        }
        return (Object[]) result;
    }

    @Override
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
        if (!objects.containsKey(new Integer(c.getID()))) {
            objects.put(new Integer(c.getID()), c);
        }
        return c;
    }

    /* (non-Javadoc)
     * @see fi.dwo.client.persistence.XmlRpcMapper#removeAllObjects()
     */
    @Override
    public void removeAllObjects() {
        cache.clear();
        super.removeAllObjects();
    }

    @Override
    public void put(int oid, Object obj) throws IOException, SQLException,
            XmlRpcException {
        System.err.println("CourseSequenceMapper.put() Not yet implemented!");
    }

}
