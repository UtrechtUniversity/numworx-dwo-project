// Source file:
// N:\\transferzone\\intern\\Afstudeerders_basw_thijsk\\April\\Implementatie\\fi\\dwo\\client\\persistence\\UserMapper.java
package fi.dwo.dwojapplet.persistence;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Hashtable;

import org.apache.xmlrpc.applet.XmlRpcException;

import fi.dwo.commons.system.TextMapper;
import fi.dwo.dwojapplet.domain.Admin;
import fi.dwo.dwojapplet.domain.ContactDocent;
import fi.dwo.dwojapplet.domain.DwoHelper;
import fi.dwo.dwojapplet.domain.School;
import fi.dwo.dwojapplet.domain.SchoolClass;
import fi.dwo.dwojapplet.domain.SchoolGroup;
import fi.dwo.dwojapplet.domain.Teacher;
import fi.dwo.dwojapplet.domain.User;

public class UserMapper extends XmlRpcMapper {

    private static final String TABLENAME = "tblUser left join tblSchoolGroup on tblUser.schoolGroupID = tblSchoolGroup.schoolGroupID left join tblGroup on tblSchoolGroup.groupID = tblGroup.groupID left join tblSchool on tblSchoolGroup.schoolID = tblSchool.schoolID";
    //private static final String TABLENAME = "tbluser";
    private static final String IDCOL = "userID";

    private static final String ORDERCOL = "lastname";
    private static final Object NUL = new Integer(0);

    /**
     *
     */
    public UserMapper() {

    }

    /**
     * @param oid
     * @param obj
     * @throws org.apache.xmlrpc.applet.XmlRpcException
     * @throws java.sql.SQLException
     *
     */
    @Override
    public void put(int oid, Object obj) throws IOException, SQLException,
            XmlRpcException {
        System.err.println("UserMapper.put() Not yet implemented!");
    }

    /**
     * @param data
     * @return Object
     * @throws org.apache.xmlrpc.applet.XmlRpcException
     * @throws java.sql.SQLException
     *
     */
    @Override
    public Object getObjectFromReturn(Hashtable data) throws IOException, SQLException, XmlRpcException {
        User u = null;
        if (data.get("userID") == null) { //We don't know enough to make a
            // userobject
            return null;
        } else if (objects.containsKey(data.get("userID"))) { // Did we know the
            // user?
            u = (User) objects.get(data.get("userID"));
        } else {
            /* Is the user a teacher? */
            Object groupName = data.get("groupname");
            if (groupName != null) {
                if (TextMapper.GUIR_OPT_TEACHER.equals(groupName)) {
                    if (DwoHelper.isContact()) {
                        u = new ContactDocent();
                    } else {
                        u = new Teacher();
                    }
                } else if (TextMapper.GUIR_OPT_ADMIN.equals(groupName)) {
                    u = new Admin();
                } else if (TextMapper.GUIR_OPT_SCHOOLADMIN.equals(groupName)) {
                    u = new ContactDocent();
                    DwoHelper.setContact(true);
                }
            }
        }

        if (u == null) {
            u = new User();
        }
        u = (User) update(u, data);
        if (!objects.containsKey(new Integer(u.getID()))) {
            objects.put(new Integer(u.getID()), u);
        }
        return u;
    }

    /**
     * Returns all the Users with the object as restriction.
     *
     * @param obj The object who specifies the restriction. possible objects
     * are:
     * <ul>
     * <li><code>SchoolClass</code>: The users of the specified SchoolClass are
     * returned.
     * </ul>
     * @return The Users who satisfies the restriction.
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
        } else if (obj instanceof SchoolGroup) {
            SchoolGroup sg = (SchoolGroup) obj;
            ht.put("tblSchoolGroup." + "schoolGroupID", new Integer(sg.getSchoolGroupID()));
        } else if (obj instanceof School) {
            School school = (School) obj;
            ht.put("tblSchoolGroup.schoolid", new Integer(school.getSchoolID()));
        }
        return super.get(ht);
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
     *      java.util.Hashtable)
     */
    @Override
    protected Object update(Object obj, Hashtable data) throws IOException, SQLException, XmlRpcException {
        User u = (User) obj;
        u.setEmail((String) data.get("email"));
        u.setFirstname((String) data.get("firstname"));
        u.setLastName((String) data.get("lastname"));
        u.setMiddleName((String) data.get("middlename"));
        u.setUserID(((Integer) data.get("userID")).intValue());
        u.setUsername((String) data.get("username"));
        u.setRights((String) data.get("rights"));
        /* Maybe we've got some information about the school */
        School s = (School) MapperCreator.instance(School.class)
                .getObjectFromReturn(data);
        if (s != null) {
            u.setSchool(s);
            String rights = s.getRights();
            for (int i = 0; i < rights.length(); i++) {
                u.addRight(rights.charAt(i));
            }
        }
        String lastLogin = (String) data.get("timestamp"); // lastLogin is al in gebruik, maar dan een Date
        try {
            u.setLastLogin(Long.parseLong(lastLogin));
        } catch (Exception e) {
        }

        Object classID = data.get("classID");
        if (!classID.equals("") && !NUL.equals(classID)) {
            try {
                SchoolClass c = (SchoolClass) MapperCreator.instance(SchoolClass.class).get(((Integer) data.get("classID")).intValue());
                if (c != null) {
                    u.setInClass(c);
                }
            } catch (Exception e) {
                System.err.println("User: " + data);
                e.printStackTrace();
            }
        }

        if (u instanceof Teacher) {
            Object[] o = MapperCreator.instance(SchoolClass.class).get(u);
            ((Teacher) u).setClasses((SchoolClass[]) o);
        }
        /*if(u instanceof Admin) {
         Object[] o = MapperCreator.instance(SchoolClass.class).get(u);
         ((Admin) u).setClasses((SchoolClass[]) o);
         }*/

        return u;
    }

    /* (non-Javadoc)
     * @see fi.dwo.client.persistence.XmlRpcMapper#createArray(int)
     */
    @Override
    protected Object[] createArray(int size) {
        return new User[size];
    }

    /* (non-Javadoc)
     * @see fi.dwo.client.persistence.XmlRpcMapper#getOrderbyCol()
     */
    @Override
    protected String getOrderbyCol() {
        return ORDERCOL;
    }
}
