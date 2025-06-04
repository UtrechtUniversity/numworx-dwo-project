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
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.logging.Logger;

class UserMapper {

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
    UserMapper() {

    }

    /**
     * @param oid
     * @param obj
     * @throws java.io.IOException
     * @throws java.sql.SQLException
     *
     */
     void put(int oid, User obj) {
    	objects.put(oid, obj);
    }

    /**
     * @param data
     * @return Object
     * @throws java.io.IOException
     * @throws java.sql.SQLException
     * @throws PersistenceException 
     *
     */
     User getObjectFromReturn(Hashtable data) throws  PersistenceException {
        User u = null;
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
     * @throws java.sql.SQLException
     * @throws PersistenceException 
     */
    public User[] get(Object obj) throws PersistenceException {
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
        throws Dwo2Exception {
      DomSchoolClass dsc = new DomSchoolClass();
      dsc.setId(PersistentSchoolClass.buildPersistenceId( (long) sc.getID()));
      List<DomStudent> v;
      boolean teacher = !DwoHelper.isContact();
      if (teacher) v = SecureTeacherSchoolClassManager.getStudentsInSchoolClass(dsc);
      else v = SecureSchoolAdminSchoolClassManager.getStudentsInSchoolClass(dsc);
      return new Vector<DomStudent>(v);
    }


        /* (non-Javadoc)
         * @see fi.dwo.client.persistence.XmlRpcMapper#createArray(int)
         */
        User[] createArray (int size) {
        return new User[size];
        }

        @SuppressWarnings("rawtypes")
        User[] getObjectFromReturn(Vector data)
            throws PersistenceException {
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

        User get(int oid)
        {   
          return objects.get(oid);
        }

         void removeObject(int key) {
          objects.remove(key);
        }

         void removeAllObjects() {
          objects.clear();
        }

    }
