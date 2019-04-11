// Source file:
// N:\\transferzone\\intern\\Afstudeerders_basw_thijsk\\April\\Implementatie\\fi\\dwo\\client\\persistence\\ClassMapper.java
package fi.dwo.dwojapplet.persistence;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Hashtable;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.xmlrpc.applet.XmlRpcException;

import fi.dwo.commons.exceptions.PersistenceException;
import fi.dwo.commons.persistence.MySQLPersistenceId;
import fi.dwo.dwojapplet.domain.DwoHelper;
import fi.dwo.dwojapplet.domain.School;
import fi.dwo.dwojapplet.domain.SchoolClass;
import fi.dwo.dwojapplet.domain.Teacher;
import nl.uu.fi.dwo.lms.jclient.lib.rest.managers.SecureSchoolAdminSchoolClassManager;
import nl.uu.fi.dwo.lms.jclient.lib.rest.managers.SecureTeacherSchoolClassManager;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.RoleType;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;

class ClassMapper extends XmlRpcMapper<SchoolClass> {

    private static final String TABLENAME = "tblClass";

    private static final String IDCOL = "classID";

    private static final String ORDERCOL = "class";

    /**
     *
     */
    public ClassMapper() {

    }

    /**
     * @param oid
     * @param obj
     * @throws java.io.IOException
     * @throws org.apache.xmlrpc.applet.XmlRpcException
     * @throws java.sql.SQLException
     *
     */
    @Override
    public void put(int oid, SchoolClass obj) {
        objects.put(oid, obj);

    }

    /**
     * @param data
     * @return Object
     *
     */
    @Override
    public SchoolClass getObjectFromReturn(Hashtable data) {
        SchoolClass c = null;
        if (data == null || data.get("classID") == null) { // We don't know enough to make a
            // classobject
            return null;
        } else if (data.get("classID") instanceof String) { // If it is a string, it was null
            return null;
        } else if (objects.containsKey(data.get("classID"))) { // Did we know
            // the class?
            c = (SchoolClass) objects.get(data.get("classID"));
        } else {
            c = new SchoolClass();
        }
        c = update(c, data);
        if (!objects.containsKey(new Integer(c.getID()))) {
            objects.put(new Integer(c.getID()), c);
        }
        return c;
    }

    /**
     * Returns all the SchoolClasses whith the object as restriction.
     *
     * @param obj The object who specifies the restriction. possible objects
     * are:
     * <ul>
     * <li><code>Teacher</code>: The classes of the teacher are returned;
     * <li><code>School</code>: The classes of the school are returned;
     * </ul>
     * @return The SchoolClasses who satisfy to the restriction.
     * @throws java.io.IOException
     * @throws org.apache.xmlrpc.applet.XmlRpcException
     * @throws java.sql.SQLException
     */
    @Override
    public SchoolClass[] get(Object obj) throws IOException, SQLException, XmlRpcException {
        if (obj instanceof Teacher) {
            // Teacher t = (Teacher) obj;
            // DbAccessIF dbAccess = DbAccessCreator.instance();
            // Vector<Object> vList = null;
            // try {
            // long schoolId =
            // MySQLPersistenceId.getNativeId(DwoHelper.getSchoolLogins().getActiveSchoolRoleAndClass().getSchool());
            // vList = dbAccess.getClassesOfTeacher(t.getUserID(), (int) schoolId);
            // }
            // catch (DwoXmlRpcException ex) {
            // Logger.getLogger(ClassMapper.class.getName()).log(Level.SEVERE, null, ex);
            // } catch (Dwo2Exception ex) {
            // Logger.getLogger(ClassMapper.class.getName()).log(Level.SEVERE, null, ex);
            // }
            // return getObjectFromReturn(vList);
            try {
                // assert obj is to be DwoHelper.getCurrentFacadeUser();
                List<DomSchoolClass> list = SecureTeacherSchoolClassManager.getTeachersSchoolClasses();
                return toSchoolClasses(list);

            } catch (Dwo2Exception ex) {
                Logger.getLogger(ClassMapper.class.getName()).log(Level.SEVERE, "get", ex);
            }
        } else if (obj instanceof School) {
            // Hashtable ht = new Hashtable();
            // School s = (School) obj;
            // ht.put("schoolID", new Integer(s.getSchoolID()));
            // return super.get(ht);
            // assert obj is to be DwoHelper.getCurrentFacadeUser().getSchool();
            if (DwoHelper.getSchoolLogins().getActiveSchoolRoleAndClass().getRole().getRoleName().equals(RoleType.SCHOOLADMIN)) {
                try {
                    List<DomSchoolClass> list = SecureSchoolAdminSchoolClassManager.getSchoolClasses();
                    return toSchoolClasses(list);
                } catch (Dwo2Exception ex) {
                    Logger.getLogger(ClassMapper.class.getName()).log(Level.SEVERE, "get", ex);
                }
            } else if (DwoHelper.getSchoolLogins().getActiveSchoolRoleAndClass().getRole().getRoleName().equals(RoleType.TEACHER)) {
                try {
                    List<DomSchoolClass> list = SecureTeacherSchoolClassManager.getTeachersSchoolClasses();
                    return toSchoolClasses(list);
                } catch (Dwo2Exception ex) {
                    Logger.getLogger(ClassMapper.class.getName()).log(Level.SEVERE, "get", ex);
                }
            }
        }
        return null;
    }

    private SchoolClass[] toSchoolClasses(List<DomSchoolClass> list) throws Dwo2Exception {
        SchoolClass[] array = createArray(list.size());
        for (int i = 0; i < array.length; i++) {
            DomSchoolClass item = list.get(i);
            int id = MySQLPersistenceId.getNativeId(item).intValue();
            SchoolClass cls = objects.get(id);
            if (cls == null) {
                cls = new SchoolClass();
            }
            cls.setDomSchoolClass(item);
            objects.put(id, cls);
            array[i] = cls;
        }
        return array;
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
	 * java.util.Hashtable)
     */
    @Override
    protected SchoolClass update(SchoolClass obj, Hashtable data) {
        SchoolClass c = (SchoolClass) obj;
        c.setClassID(((Integer) data.get("classID")).intValue());
        c.setClassName((String) data.get("class"));
        c.setIconizer(Boolean.TRUE.equals(data.get("iconizer")));
        return c;
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see fi.dwo.client.persistence.XmlRpcMapper#createArray(int)
     */
    @Override
    protected SchoolClass[] createArray(int size) {
        return new SchoolClass[size];
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see fi.dwo.client.persistence.XmlRpcMapper#getOrderbyCol()
     */
    @Override
    protected String getOrderbyCol() {
        return ORDERCOL;
    }

    @Override
    public SchoolClass get(int oid) throws IOException, XmlRpcException, SQLException, PersistenceException {
        // TODO Auto-generated method stub
        SchoolClass schoolClass = super.get(oid);
        if (schoolClass != null) {
            objects.put(schoolClass.getID(), schoolClass); // into cache
        }
        return schoolClass;
    }

}
