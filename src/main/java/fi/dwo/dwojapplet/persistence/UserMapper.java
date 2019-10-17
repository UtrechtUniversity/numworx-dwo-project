// Source file:
// N:\\transferzone\\intern\\Afstudeerders_basw_thijsk\\April\\Implementatie\\fi\\dwo\\client\\persistence\\UserMapper.java
package fi.dwo.dwojapplet.persistence;

import fi.dwo.commons.exceptions.PersistenceException;
import fi.dwo.commons.persistence.MySQLPersistenceId;
import fi.dwo.commons.persistence.entities.PersistentSchoolClass;
import fi.dwo.dwojapplet.domain.DwoHelper;
import fi.dwo.dwojapplet.domain.SchoolClass;
import fi.dwo.dwojapplet.domain.User;
import nl.uu.fi.dwo.lms.jclient.lib.rest.managers.SecureSchoolAdminSchoolClassManager;
import nl.uu.fi.dwo.lms.jclient.lib.rest.managers.SecureTeacherSchoolClassManager;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomStudent;
import nl.uu.fi.dwo.rest.dom.entities.DomUser;
import nl.uu.fi.dwo.rest.dom.entities.DomUserFull;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
import nl.uu.fi.dwo.rest.persistence.PersistenceId;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.logging.Logger;

import org.apache.xmlrpc.applet.XmlRpcException;

class UserMapper implements MapperIF<User> {

    private static final Logger LOG = Logger.getLogger(UserMapper.class.getName());

    //private static final String TABLENAME = "tblUser left join tblSchoolGroup on tblUser.schoolGroupID = tblSchoolGroup.schoolGroupID left join tblGroup on tblSchoolGroup.groupID = tblGroup.groupID left join tblSchool on tblSchoolGroup.schoolID = tblSchool.schoolID";
    //private static final String TABLENAME = "tbluser";
    //private static final String IDCOL = "userID";

    //private static final String ORDERCOL = "lastname";
    //private static final Object NUL = 0;

    private static final PersistenceId UNKNOWN = new PersistenceId("LOCAL;PersistentSchoolGroup;00000000000000000000");

    //protected Hashtable hasRoleObjects = new Hashtable();
    protected Map<Integer, User> objects = new Hashtable<>();

    /**
     *
     */
    public UserMapper() {

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
    public void put(int oid, User obj) {
    	objects.put(oid, obj);
    }

    /**
     * @param data
     * @return Object
     * @throws java.io.IOException
     * @throws org.apache.xmlrpc.applet.XmlRpcException
     * @throws java.sql.SQLException
     * @throws PersistenceException 
     *
     */
    @Override
    public User getObjectFromReturn(Hashtable data) throws IOException, SQLException, XmlRpcException, PersistenceException {
        User u = null;
//        if (data.get("userID") == null) { //We don't know enough to make a
//            // userobject
//            return null;
//        } else if (objects.containsKey(data.get("userID"))) { // Did we know the
//            // user?
//            u = (User) objects.get(data.get("userID"));
//            //TODO NOW
//            if (PersistenceFacade.idOf(u.getSchoolGroupID()) != ((Integer) (data.get("schoolGroupID"))).intValue()) {
//                u = new User();
//                u = (User) update(u, data);
//                return u;
//            }
//        } else {
//            /* Is the user a teacher? */
//            Object groupName = data.get("groupname");
//            if (groupName != null) {
//                if (TextMapper.GUIR_OPT_TEACHER.equals(groupName)) {
//                    if (DwoHelper.isContact()) {
//                        u = new SchoolAdmin();
//                    } else {
//                        u = new Teacher();
//                    }
//                } else if (TextMapper.GUIR_OPT_ADMIN.equals(groupName)) {
//                    u = new Admin();
//                } else if (TextMapper.GUIR_OPT_SCHOOLADMIN.equals(groupName)) {
//                    u = new SchoolAdmin();
//                    DwoHelper.setContact(true);
//                }
//            }
//        }
//
//        if (u == null) {
//            u = new User();
//        }
//        u = (User) update(u, data);
//        if (!objects.containsKey(new Integer(u.getID()))) {
//            objects.put(new Integer(u.getID()), u);
//        }
        LOG.severe("Illegal access");
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
     * @throws java.io.IOException
     * @throws org.apache.xmlrpc.applet.XmlRpcException
     * @throws java.sql.SQLException
     * @throws PersistenceException 
     */
    public User[] get(Object obj) throws IOException, SQLException,
            XmlRpcException, PersistenceException {
        if (obj instanceof SchoolClass) {
            SchoolClass sc = (SchoolClass) obj;
            Vector<?> vList;
            try {
              vList = studentsOfClass(sc);
              return getObjectFromReturn(vList);
          } catch (Dwo2Exception e) {
              throw new PersistenceException(PersistenceException.EX_UNKNOWN_ERROR, e);
          }

        } 
        throw new Error("Not implemented");
//        else if (obj instanceof SchoolGroup) {
//            SchoolGroup sg = (SchoolGroup) obj;
//            ht.put("tblSchoolGroup." + "schoolGroupID", new Integer(sg.getSchoolGroupID()));
//        } else if (obj instanceof School) {
//            School school = (School) obj;
//            ht.put("tblSchoolGroup.schoolid", new Integer(school.getSchoolID()));
//        }
//        return super.get(ht);
    }

    Vector<DomStudent> studentsOfClass(SchoolClass sc)
        throws IOException, XmlRpcException, SQLException, Dwo2Exception {
      DomSchoolClass dsc = new DomSchoolClass();
      dsc.setId(PersistentSchoolClass.buildPersistenceId( (long) sc.getID()));
      List<DomStudent> v;
      boolean teacher = !DwoHelper.isContact();
      if (teacher) v = SecureTeacherSchoolClassManager.getStudentsInSchoolClass(dsc);
      else v = SecureSchoolAdminSchoolClassManager.getStudentsInSchoolClass(dsc);
      return new Vector<DomStudent>(v);
//      DbAccessIF dbAccess = DbAccessCreator.instance();
//      Vector<Object> vList = null;
//      try {
//          int schoolClassID = sc.getID();
//          vList = dbAccess.getStudentsOfClass(schoolClassID);
//      } catch (DwoXmlRpcException ex) {
//          Logger.getLogger(ClassMapper.class.getName()).log(Level.SEVERE, null, ex);
//      }
//      return vList;
    }

//    @Override
//    public Object get(int uid, Integer sgid) {
//        String key = Integer.toString(uid).concat("-").concat(sgid.toString());
//        if (hasRoleObjects.containsKey(key)) {
//            return hasRoleObjects.get(key);
//        } else {
//            if(objects.containsKey(uid)){
//                User u = (User) objects.get(uid);
//                if(u!=null && PersistenceFacade.idOf(u.getSchoolGroupID())==sgid.intValue()){
//                    return u;
//                }
//            }
//            Vector v = new Vector();
//            DbAccessIF dbAccess = DbAccessCreator.instance();
//            try {
//                v =  dbAccess.getHasRoleUser(uid, sgid.intValue());
//                if (!v.isEmpty()) {                    
//                    return getObjectFromReturn((Hashtable) v.get(0));
//                }
//
//            } catch (Exception ex) {
//                Logger.getLogger(ClassMapper.class.getName()).log(Level.SEVERE, null, ex);
//            }
//            return null;
//        }
//    }
        /*
//     * (non-Javadoc)
//     * 
//     * @see fi.dwo.client.persistence.XmlRpcMapper#getIDCol()
//         */
//        @Override
//        protected String getIDCol() {
//        return IDCOL;
//        }

//        /*
//     * (non-Javadoc)
//     * 
//     * @see fi.dwo.client.persistence.XmlRpcMapper#getTableName()
//         */
//        @Override
//        protected String getTableName() {
//        return TABLENAME;
//        }

//        /*
//     * (non-Javadoc)
//     * 
//     * @see fi.dwo.client.persistence.XmlRpcMapper#update(java.lang.Object,
//     *      java.util.Hashtable)
//         */
//        @Override
//        protected User update
//        (User obj, Hashtable data) throws IOException, SQLException, XmlRpcException, PersistenceException {
//            User u = (User) obj;
//            u.setEmail((String) data.get("email"));
//            u.setFirstname((String) data.get("firstname"));
//            u.setLastName((String) data.get("lastname"));
//            u.setMiddleName((String) data.get("middlename"));
//            u.setUserID(((Integer) data.get("userID")).intValue());
//            u.setUsername((String) data.get("username"));
//            u.setRights((String) data.get("rights"));
//            Number integer = (Number) data.get("schoolGroupID");
//			u.setSchoolGroupID(PersistentSchoolGroup.buildPersistenceId(integer.longValue()));
//            /* Maybe we've got some information about the school */
//            School s = (School) MapperCreator.instance(School.class)
//                    .getObjectFromReturn(data);
//            if (s != null) {
//                u.setSchool(s);
//                String rights = s.getRights();
//                for (int i = 0; i < rights.length(); i++) {
//                    u.addRight(rights.charAt(i));
//                }
//            }
//            String lastLogin = (String) data.get("timestamp"); // lastLogin is al in gebruik, maar dan een Date
//            try {
//                u.setLastLogin(Long.parseLong(lastLogin));
//            } catch (Exception e) {
//            }
//
//            Object classID = data.get("classID");
//            if (classID != null && !classID.equals("") && !NUL.equals(classID)) {
//                try {
//                    SchoolClass c = (SchoolClass) MapperCreator.instance(SchoolClass.class).get(((Integer) data.get("classID")).intValue());
//                    if (c != null) {
//                        u.setInClass(c);
//                    }
//                } catch (Exception e) {
//                    System.err.println("User: " + data);
//                    LOG.log(Level.SEVERE, null, e);
//                }
//            }
//
//            if (u instanceof Teacher) {
//                Object[] o = MapperCreator.instance(SchoolClass.class).get(u);
//                if (o != null) {
//                    SchoolClass[] slist = (SchoolClass[]) o;
//                    ((Teacher) u).setClasses(slist);
//                } else {
//                    ((Teacher) u).setClasses(null);
//                }
//            }
//            /*if(u instanceof Admin) {
//         Object[] o = MapperCreator.instance(SchoolClass.class).get(u);
//         ((Admin) u).setClasses((SchoolClass[]) o);
//         }*/
//
//            return u;
//        }

        /* (non-Javadoc)
         * @see fi.dwo.client.persistence.XmlRpcMapper#createArray(int)
         */
        protected User[] createArray (int size) {
        return new User[size];
        }

//        /* (non-Javadoc)
//     * @see fi.dwo.client.persistence.XmlRpcMapper#getOrderbyCol()
//         */
//        @Override
//        protected String getOrderbyCol() {
//        return ORDERCOL;
//        }

        @SuppressWarnings("rawtypes")
        @Override
        public User[] getObjectFromReturn(Vector data)
            throws IOException, SQLException, XmlRpcException, PersistenceException {
          int i;
          User[] oa = createArray(data.size());
          for (i = 0; i < data.size(); i++) {
              Object element =  data.elementAt(i);
              if (element instanceof DomUserFull) {
                try {
                  oa[i] = getObjectFromUserFull( (DomUserFull) element);
                } catch (Dwo2Exception e) {
                  throw new PersistenceException(PersistenceException.EX_UNKNOWN_ERROR);
                }
              } else if (element instanceof DomUser) {
                try {
                  oa[i] = getObjectFromUser( (DomUser) element);
                } catch (Dwo2Exception e) {
                  throw new PersistenceException(PersistenceException.EX_UNKNOWN_ERROR);
                }
               
              } else
                oa[i] = getObjectFromReturn((Hashtable)element);
          }
          
          return oa;
        }

        private User getObjectFromUser(DomUser element) throws Dwo2Exception {
          Long id = MySQLPersistenceId.getNativeId(element);
          User u = objects.get(id.intValue());
          if (u == null) { 
            u = new User(); 
            u.setEmail("");
            u.setSchoolGroupID(UNKNOWN);
            u.setSchool(DwoHelper.getCurrentFacadeUser().getSchool());
          }
          u.setFirstname(element.getGivenName());
          u.setUserID(id.intValue());
          u.setMiddleName(element.getInsertion());
          u.setLastName(element.getFamilyName());
          u.setUsername(element.getUserName());
          objects.putIfAbsent(id.intValue(), u);
          return u;
        }

        private User getObjectFromUserFull(DomUserFull element) throws Dwo2Exception {
          Long id = MySQLPersistenceId.getNativeId(element);
          User u = objects.get(id.intValue());
          if (u == null) { 
            u = new User();
            u.setSchoolGroupID(UNKNOWN);
            u.setSchool(DwoHelper.getCurrentFacadeUser().getSchool());
          }
          u.setDomUserFull(element);
          objects.putIfAbsent(id.intValue(), u);
          return u;
        }

        @Override
        public User get(int oid)
        {   
          return objects.get(oid);
        }

        @Override
        public void removeObject(int key) {
          objects.remove(key);
        }

        @Override
        public void removeAllObjects() {
          objects.clear();
        }

    }
