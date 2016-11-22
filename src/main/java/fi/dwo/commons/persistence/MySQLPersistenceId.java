/* Copyrighted 2015. */
package fi.dwo.commons.persistence;

import nl.uu.fi.dwo.rest.persistence.PersistenceClassType;
import nl.uu.fi.dwo.rest.persistence.PersistenceId;
import fi.dwo.commons.persistence.entities.PersistentApplet;
import fi.dwo.commons.persistence.entities.PersistentAppletConfig;
import fi.dwo.commons.persistence.entities.PersistentClassCourse;
import fi.dwo.commons.persistence.entities.PersistentCourse;
import fi.dwo.commons.persistence.entities.PersistentCourseSequence;
import fi.dwo.commons.persistence.entities.PersistentDwoProfile;
import fi.dwo.commons.persistence.entities.PersistentHasRole;
import fi.dwo.commons.persistence.entities.PersistentHasRolePK;
import fi.dwo.commons.persistence.entities.PersistentLoginContext;
import fi.dwo.commons.persistence.entities.PersistentRole;
import fi.dwo.commons.persistence.entities.PersistentSamlUser;
import fi.dwo.commons.persistence.entities.PersistentSchool;
import fi.dwo.commons.persistence.entities.PersistentSchoolClass;
import fi.dwo.commons.persistence.entities.PersistentSchoolGroup;
import fi.dwo.commons.persistence.entities.PersistentScoContext;
import fi.dwo.commons.persistence.entities.PersistentScoData;
import fi.dwo.commons.persistence.entities.PersistentStudentOfClass;
import fi.dwo.commons.persistence.entities.PersistentStudentScoContext;
import fi.dwo.commons.persistence.entities.PersistentStudentScoData;
import fi.dwo.commons.persistence.entities.PersistentTeacherOfClass;
import fi.dwo.commons.persistence.entities.PersistentUser;
import java.util.logging.Level;
import java.util.logging.Logger;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
import nl.uu.fi.dwo.rest.exceptions.Dwo2ExceptionCode;

/**
 * PersistenceId for the MySQL database.
 *
 * @see PersistenceId
 *
 * @author G.A.J. van der Plas
 */
public class MySQLPersistenceId extends PersistenceId implements Comparable<PersistenceId> {
    
    private static final Logger LOG = Logger.getLogger(MySQLPersistenceId.class.getName());

    // the two variables that define the id.
    //private long id;
    
    public MySQLPersistenceId() {
        super.setIdString(null);
//        super.setType(PersistenceClassType.none);
    }

    /**
     * This constructor is only public because of jax-rs. Use a factory to
     * generate id's.
     *
     * @param pid
     */
    public MySQLPersistenceId(PersistenceId pid) {
        String[] strList = pid.getIdString().split(";");
        if (strList[0].equals("MYSQL")) {
            super.setIdString(pid.getIdString());
//            super.setType(PersistenceClassType.valueOf(strList[1]));
            //id = Long.parseLong(strList[2]);
            LOG.log(Level.FINE, "Set MySQLPersistenceId string {0} and type {1}", new Object[]{super.getIdString(), super.getType()});
        } else {
//            super.setType(PersistenceClassType.none);
            LOG.log(Level.SEVERE, "Failed to convert IdString to id and type as it is for DB-type {0}", new Object[]{strList[0]});
        }
    }

//    /**
//     * This constructor is only public because of jax-rs. Use a factory to
//     * generate id's.
//     *
//     * @param aId
//     * @param aType
//     */
//    private MySQLPersistenceId(long aId, PersistenceClassType aType) {
////        super.setType(aType);
//        String s = String.format("MYSQL;%s;%020d", super.getType().name(), id);
//        super.setIdString(s);
//    }
    
    @Override
    public String getIdString() {
        return super.getIdString();
    }
    
    @Override
    public void setIdString(String idString) {
//        String[] strList = idString.split(";");
//        if (strList[0].equals("MYSQL")) {
            super.setIdString(idString);
//            super.setType(PersistenceClassType.valueOf(strList[1]));
//            LOG.log(Level.FINE, "Set MySQLPersistenceId string {0} and type {1}", new Object[]{super.getIdString(), super.getType()});
//        } else {
//            super.setType(PersistenceClassType.none);
//            LOG.log(Level.SEVERE, "Failed to convert IdString to id and type as it is for DB {0}", new Object[]{strList[0]});
//        }
    }
//
//    /**
//     * This functionality is only public because of jax-rs.
//     *
//     * @return the id.
//     */
//    public long getId() {
//        return id;
//    }
//
//    /**
//     * This functionality is only public because of jax-rs.
//     *
//     * @param id the id to set
//     */
//    public void setId(int id) {
//        this.id = id;
//    }
    
    @Override
    public PersistenceClassType getType() {
        return super.getType();
    }

//    /**
//     * This functionality is only public because of jax-rs.
//     *
//     * @param type the type to set
//     */
//    @Override
//    public void setType(PersistenceClassType type) {
//        super.setType(type);
//    }
//
//    /**
//     * Compare ordered state.
//     *
//     * @param aId
//     * @return
//     */
//    @Override
//    public int compareTo(PersistenceId aId) {
//        
//        final int BEFORE = -1;
//        final int EQUAL = 0;
//        final int AFTER = 1;
//        
//        if (aId.getType().ordinal() == super.getType().ordinal()
//                && id == ((MySQLPersistenceId) aId).getId()) {
//            return EQUAL;
//        } else if (aId.getType().ordinal() < super.getType().ordinal()
//                || (aId.getType().ordinal() == super.getType().ordinal()
//                && id < ((MySQLPersistenceId) aId).getId())) {
//            return BEFORE;
//        } else {
//            return AFTER;
//        }
//    }
//
//    /**
//     * Define equality of state.
//     *
//     * @param aId
//     * @return
//     */
//    @Override
//    public boolean equals(PersistenceId aId) {
//        if (aId.getType().ordinal() == super.getType().ordinal()
//                && id == ((MySQLPersistenceId) aId).getId()) {
//            return true;
//        } else {
//            return false;
//        }
//    }
//
//    /**
//     * A class that overrides equals must override hashCode for HasMaps and such
//     * to work properly.
//     */
//    @Override
//    public int hashCode() {
//        //TODO Improve the hash function
//        long r = super.getType().ordinal();
//        r = r * id;
//        return (int) r % Integer.MAX_VALUE;
//    }
//    
//    public static long getId(PersistenceId aId) {
//        String[] strList = aId.getIdString().split(";");
//        if (strList[0].equals("MYSQL")) {
//            return Long.parseLong(strList[2]);
//        } else {
//            LOG.log(Level.SEVERE, "Failed to convert IdString {0}", new Object[]{aId.getIdString()});
//            return -1;
//        }
//    }
//    
//    public static MySQLPersistenceId createPersistenceId(long id, PersistenceClassType t) {
//        return new MySQLPersistenceId(id, t);
//    }
//    
//    public static MySQLPersistenceId createPersistentId(PersistentApplet o) {
//        return new MySQLPersistenceId(o.getAppletID(), PersistenceClassType.valueOf(o.getClass().getSimpleName()));
//    }
//    
//    public static MySQLPersistenceId createPersistentId(PersistentAppletConfig o) {
//        return new MySQLPersistenceId(o.getAppletConfigID(), PersistenceClassType.valueOf(o.getClass().getSimpleName()));
//    }
//    
//    public static MySQLPersistenceId createPersistentId(PersistentClassCourse o) {
//        return new MySQLPersistenceId(o.getClassCourseID(), PersistenceClassType.valueOf(o.getClass().getSimpleName()));
//    }
//    
//    public static MySQLPersistenceId createPersistentId(PersistentCourse o) {
//        return new MySQLPersistenceId(o.getCourseID(), PersistenceClassType.valueOf(o.getClass().getSimpleName()));
//    }
//    
//    public static MySQLPersistenceId createPersistentId(PersistentCourseSequence o) {
//        return new MySQLPersistenceId(o.getCoursesequenceID(), PersistenceClassType.valueOf(o.getClass().getSimpleName()));
//    }
//    
//    public static MySQLPersistenceId createPersistentId(PersistentDwoProfile o) {
//        return new MySQLPersistenceId(o.getDwoProfileID(), PersistenceClassType.valueOf(o.getClass().getSimpleName()));
//    }
//
//// Don't need this cached.    
////    public static MySQLPersistenceId createPersistenceId(PersistentDwoSystemParameters o) {
////                return new MySQLPersistenceId(o.getId(), PersistenceClassType.valueOf(o.getClass().getSimpleName()));
////    }
//    public static MySQLPersistenceId createPersistentId(PersistentHasRole o) {
//        return new MySQLPersistenceId(o.getPersistentHasRolePK().getId(), PersistenceClassType.valueOf(o.getClass().getSimpleName()));
//    }
//    
//    public static MySQLPersistenceId createPersistentId(PersistentLoginContext o) {
//        return new MySQLPersistenceId(o.getId(), PersistenceClassType.valueOf(o.getClass().getSimpleName()));
//    }
//
////    public static MySQLPersistenceId createPersistenceId(PersistentImage o) {
////                return new MySQLPersistenceId(o.(), PersistenceClassType.valueOf(o.getClass().getSimpleName()));
////    }
////    public static MySQLPersistenceId createPersistenceId(PersistentJars o) {
////                return new MySQLPersistenceId(o.getKey(), PersistenceClassType.valueOf(o.getClass().getSimpleName()));
////    }
//    public static MySQLPersistenceId createPersistentId(PersistentRole o) {
//        return new MySQLPersistenceId(o.getGroupID(), PersistenceClassType.valueOf(o.getClass().getSimpleName()));
//    }
//    
//    public static MySQLPersistenceId createPersistentId(PersistentSamlUser o) {
//        return new MySQLPersistenceId(o.getId(), PersistenceClassType.valueOf(o.getClass().getSimpleName()));
//    }
//    
//    public static MySQLPersistenceId createPersistentId(PersistentSchool o) {
//        return new MySQLPersistenceId(o.getSchoolID(), PersistenceClassType.valueOf(o.getClass().getSimpleName()));
//    }
//    
//    public static MySQLPersistenceId createPersistentId(PersistentSchoolClass o) {
//        return new MySQLPersistenceId(o.getClassID(), PersistenceClassType.valueOf(o.getClass().getSimpleName()));
//    }
//    
//    public static MySQLPersistenceId createPersistentId(PersistentSchoolGroup o) {
//        return new MySQLPersistenceId(o.getSchoolGroupID(), PersistenceClassType.valueOf(o.getClass().getSimpleName()));
//    }
//    
//    public static MySQLPersistenceId createPersistentId(PersistentScoContext o) {
//        return new MySQLPersistenceId(o.getScoID(), PersistenceClassType.valueOf(o.getClass().getSimpleName()));
//    }
//    
//    public static MySQLPersistenceId createPersistentId(PersistentScoData o) {
//        return new MySQLPersistenceId(o.getScoID(), PersistenceClassType.valueOf(o.getClass().getSimpleName()));
//    }
//    
//    public static MySQLPersistenceId createPersistentId(PersistentStudentOfClass o) {
//        MySQLPersistenceId mId = new MySQLPersistenceId();
//        String idString = String.format("MYSQL;%s;%020d;%020d;%020d", o.getClass().getSimpleName(),
//                o.getPersistentStudentOfClassPK().getUserID().longValue(),
//                o.getPersistentStudentOfClassPK().getSchoolGroupID().longValue(),
//                o.getPersistentStudentOfClassPK().getClassID().longValue());
//        
//        mId.setIdString(idString);
//        return mId;
//    }
//    
//    public static MySQLPersistenceId createPersistentId(PersistentStudentScoContext o) {
//        return new MySQLPersistenceId(o.getStudentSco(), PersistenceClassType.valueOf(o.getClass().getSimpleName()));
//    }
//    
//    public static MySQLPersistenceId createPersistentId(PersistentStudentScoData o) {
//        return new MySQLPersistenceId(o.getStudentSco(), PersistenceClassType.valueOf(o.getClass().getSimpleName()));
//    }
//    
//    public static MySQLPersistenceId createPersistentId(PersistentTeacherOfClass o) {
//        return new MySQLPersistenceId(o.getPersistentTeacherOfClassPK().getId(), PersistenceClassType.valueOf(o.getClass().getSimpleName()));
//    }
//    
//    public static MySQLPersistenceId createPersistentId(PersistentUser o) {
//        return new MySQLPersistenceId(o.getId(), PersistenceClassType.valueOf(o.getClass().getSimpleName()));
//    }    
//    
//    public static PersistentHasRolePK extractHasRoleKey(PersistenceId aId) throws Dwo2Exception {
//        MySQLPersistenceId id = new MySQLPersistenceId(aId);
//        if (id.getType() == PersistenceClassType.PersistentHasRole) {
//            return new PersistentHasRolePK(id.getId());
//        } else {
//            //illegal type
//            throw new Dwo2Exception(Dwo2ExceptionCode.PersistentId_ConversionError, "Given PersistenceId is not of HasRolePK type.");
//        }
//    }    
}
