package fi.dwo.dwojapplet.persistence;

import fi.dwo.dwojapplet.domain.ClassCourse;
import fi.dwo.dwojapplet.domain.SchoolClass;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;
import java.util.Hashtable;
import org.apache.xmlrpc.applet.XmlRpcException;

class ClassCourseMapper extends XmlRpcMapper {

    ClassCourseMapper() {
    }

    @Override
    public void put(int oid, Object obj) throws IOException, SQLException,
            XmlRpcException {

    }

    @Override
    public Object getObjectFromReturn(Hashtable data) throws IOException,
            SQLException, XmlRpcException {
        ClassCourse c = null;
        if (data == null || data.get("ClassCourseID") == null) { //We don't know enough to make a
            // classobject
            return null;
        } else if (data.get("ClassCourseID") instanceof String) { //If it is a string, it was null
            return null;
        } else if (objects.containsKey(data.get("ClassCourseID"))) { // Did we know
            // the class?
            c = (ClassCourse) objects.get(data.get("ClassCourseID"));
        } else {
            c = new ClassCourse();
        }
        c = (ClassCourse) update(c, data);
        if (!objects.containsKey(new Integer(c.getID()))) {
            objects.put(new Integer(c.getID()), c);
        }
        return c;
    }

    @Override
    public Object[] get(Object obj) throws IOException, SQLException,
            XmlRpcException {
        Hashtable ht = new Hashtable();
        if (obj instanceof SchoolClass) {
            ht.put("ClassID", new Integer(((SchoolClass) obj).getID()));
        } else if (obj instanceof Object[]) // schoolclass, type
        {
            Object[] array = (Object[]) obj;
            obj = array[0];
            ht.put("ClassID", new Integer(((SchoolClass) obj).getID()));
            ht.put("type", array[1]);
        }

        return super.get(ht);
    }

    @Override
    protected Object update(Object obj, Hashtable data) throws IOException,
            SQLException, XmlRpcException {
        ClassCourse cc = (ClassCourse) obj;
        cc.setClassCourseID(((Integer) data.get("ClassCourseID")).intValue());
        cc.setClassID(((Integer) data.get("ClassID")).intValue());
        cc.setCourseID(((Integer) data.get("CourseID")).intValue());
        cc.setType(((Integer) data.get("type")).intValue());
        Object o = data.get("notAfter");
        if (o instanceof Date) // string or null 
        {
            cc.setNotAfter((Date) o);
        } else {
            cc.setNotAfter(null);
        }
        o = data.get("notBefore");
        if (o instanceof Date) // string or null 
        {
            cc.setNotBefore((Date) o);
        } else {
            cc.setNotBefore(null);
        }
        return cc;
    }

    @Override
    protected Object[] createArray(int size) {
        return new ClassCourse[size];
    }

    @Override
    protected String getIDCol() {
        return "ClassCourseID";
    }

    @Override
    protected String getTableName() {

        return "tblClassCourse";
    }

    @Override
    protected String getOrderbyCol() {
        return "ClassCourseID";
    }

}
