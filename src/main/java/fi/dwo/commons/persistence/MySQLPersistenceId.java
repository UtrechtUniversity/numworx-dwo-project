package fi.dwo.commons.persistence;

import fi.dwo.commons.rest.RestClassType;
import fi.dwo.commons.persistence.entities.*;

/**
 *
 * @author Gert van der Plas <gertvdplas@gmail.com>
 */
public class MySQLPersistenceId implements PersistenceId {

    // the two variables that define the id.
    private long id;
    private RestClassType type;

    private MySQLPersistenceId(long aId, RestClassType aType) {
        id = aId;
        type = aType;
    }

    @Override
    public RestClassType getType() {
        return type;
    }

    /**
     * Compare ordered state.
     *
     * @param aId
     * @return
     */
    @Override
    public int compareTo(PersistenceId aId) {

        final int BEFORE = -1;
        final int EQUAL = 0;
        final int AFTER = 1;

        if (aId.getType().ordinal() == type.ordinal()
                && id == ((MySQLPersistenceId) aId).getId()) {
            return EQUAL;
        } else if (aId.getType().ordinal() < type.ordinal()
                || (aId.getType().ordinal() == type.ordinal()
                && id < ((MySQLPersistenceId) aId).getId())) {
            return BEFORE;
        } else {
            return AFTER;
        }
    }

    /**
     * Define equality of state.
     *
     * @param aId
     * @return
     */
    public boolean equals(PersistenceId aId) {
        if (aId.getType().ordinal() == type.ordinal()
                && id == ((MySQLPersistenceId) aId).getId()) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * A class that overrides equals must override hashCode for HasMaps and such
     * to work properly. 
     */
    @Override
    public int hashCode() {
        //TODO Improve the hash function
        long r = type.ordinal();
        r = r * id;
        return (int) r % Integer.MAX_VALUE;
    }

    /**
     * @return the id
     */
    protected long getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    protected void setId(int id) {
        this.id = id;
    }

    /**
     * @param type the type to set
     */
    public void setType(RestClassType type) {
        this.type = type;
    }

    public static MySQLPersistenceId createPersistenceId(long id, RestClassType t) {
                return new MySQLPersistenceId(id,t);
    }
    
    
    public static MySQLPersistenceId createPersistentId(PersistentApplet o) {
                return new MySQLPersistenceId(o.getAppletID(), RestClassType.valueOf(o.getClass().getSimpleName()));
    }

    public static MySQLPersistenceId createPersistentId(PersistentAppletConfig o) {
                return new MySQLPersistenceId(o.getAppletConfigID(), RestClassType.valueOf(o.getClass().getSimpleName()));
    }

    public static MySQLPersistenceId createPersistentId(PersistentClassCourse o) {
                return new MySQLPersistenceId(o.getClassCourseID(), RestClassType.valueOf(o.getClass().getSimpleName()));
    }

    public static MySQLPersistenceId createPersistentId(PersistentCourse o) {
                return new MySQLPersistenceId(o.getCourseID(), RestClassType.valueOf(o.getClass().getSimpleName()));
    }

    public static MySQLPersistenceId createPersistentId(PersistentCourseSequence o) {
                return new MySQLPersistenceId(o.getCoursesequenceID(), RestClassType.valueOf(o.getClass().getSimpleName()));
    }

    public static MySQLPersistenceId createPersistentId(PersistentDwoProfile o) {
                return new MySQLPersistenceId(o.getDwoProfileID(), RestClassType.valueOf(o.getClass().getSimpleName()));
    }

// Don't need this cached.    
//    public static MySQLPersistenceId createPersistenceId(PersistentDwoSystemParameters o) {
//                return new MySQLPersistenceId(o.getUserID(), RestClassType.valueOf(o.getClass().getSimpleName()));
//    }


    public static MySQLPersistenceId createPersistentId(PersistentHasRole o) {
                return new MySQLPersistenceId(o.getPersistentHasRolePK().getId(), RestClassType.valueOf(o.getClass().getSimpleName()));
    }

//    public static MySQLPersistenceId createPersistenceId(PersistentImage o) {
//                return new MySQLPersistenceId(o.(), RestClassType.valueOf(o.getClass().getSimpleName()));
//    }

//    public static MySQLPersistenceId createPersistenceId(PersistentJars o) {
//                return new MySQLPersistenceId(o.getKey(), RestClassType.valueOf(o.getClass().getSimpleName()));
//    }

    public static MySQLPersistenceId createPersistentId(PersistentRole o) {
                return new MySQLPersistenceId(o.getGroupID(), RestClassType.valueOf(o.getClass().getSimpleName()));
    }

    public static MySQLPersistenceId createPersistentId(PersistentSamlUser o) {
                return new MySQLPersistenceId(o.getId(), RestClassType.valueOf(o.getClass().getSimpleName()));
    }

    public static MySQLPersistenceId createPersistentId(PersistentSchool o) {
                return new MySQLPersistenceId(o.getSchoolID(), RestClassType.valueOf(o.getClass().getSimpleName()));
    }

    public static MySQLPersistenceId createPersistentId(PersistentSchoolClass o) {
                return new MySQLPersistenceId(o.getClassID(), RestClassType.valueOf(o.getClass().getSimpleName()));
    }

    public static MySQLPersistenceId createPersistentId(PersistentSchoolGroup o) {
                return new MySQLPersistenceId(o.getSchoolGroupID(), RestClassType.valueOf(o.getClass().getSimpleName()));
    }

    public static MySQLPersistenceId createPersistentId(PersistentScoContext o) {
                return new MySQLPersistenceId(o.getScoID(), RestClassType.valueOf(o.getClass().getSimpleName()));
    }

    public static MySQLPersistenceId createPersistentId(PersistentScoData o) {
                return new MySQLPersistenceId(o.getScoID(), RestClassType.valueOf(o.getClass().getSimpleName()));
    }

    public static MySQLPersistenceId createPersistentId(PersistentStudentOfClass o) {
                return new MySQLPersistenceId(o.getPersistentStudentOfClassPK().getId(), RestClassType.valueOf(o.getClass().getSimpleName()));
    }

    public static MySQLPersistenceId createPersistentId(PersistentStudentScoContext o) {
                return new MySQLPersistenceId(o.getStudentSco(), RestClassType.valueOf(o.getClass().getSimpleName()));
    }

    public static MySQLPersistenceId createPersistentId(PersistentStudentScoData o) {
                return new MySQLPersistenceId(o.getStudentSco(), RestClassType.valueOf(o.getClass().getSimpleName()));
    }

    public static MySQLPersistenceId createPersistentId(PersistentTeacherOfClass o) {
                return new MySQLPersistenceId(o.getPersistentTeacherOfClassPK().getId(), RestClassType.valueOf(o.getClass().getSimpleName()));
    }

    public static MySQLPersistenceId createPersistentId(PersistentUser o) {
                return new MySQLPersistenceId(o.getUserID(), RestClassType.valueOf(o.getClass().getSimpleName()));
    }

}
