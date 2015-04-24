package fi.dwo.commons.persistence;

import fi.dwo.commons.persistence.entities.*;

/**
 *
 * @author Gert van der Plas <gertvdplas@gmail.com>
 */
public class MySQLPersistenceId implements PersistenceId {

    // the two variables that define the id.
    private long id;
    private PersistenceClassType type;

    private MySQLPersistenceId(long aId, PersistenceClassType aType) {
        id = aId;
        type = aType;
    }

    @Override
    public PersistenceClassType getType() {
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
    public void setType(PersistenceClassType type) {
        this.type = type;
    }

    public static MySQLPersistenceId createPersistentId(PersistentApplet o) {
                return new MySQLPersistenceId(o.getAppletID(), PersistenceClassType.valueOf(o.getClass().getSimpleName()));
    }

    public static MySQLPersistenceId createPersistentId(PersistentAppletConfig o) {
                return new MySQLPersistenceId(o.getAppletConfigID(), PersistenceClassType.valueOf(o.getClass().getSimpleName()));
    }

    public static MySQLPersistenceId createPersistentId(PersistentClassCourse o) {
                return new MySQLPersistenceId(o.getClassCourseID(), PersistenceClassType.valueOf(o.getClass().getSimpleName()));
    }

    public static MySQLPersistenceId createPersistentId(PersistentCourse o) {
                return new MySQLPersistenceId(o.getCourseID(), PersistenceClassType.valueOf(o.getClass().getSimpleName()));
    }

    public static MySQLPersistenceId createPersistentId(PersistentCourseSequence o) {
                return new MySQLPersistenceId(o.getCoursesequenceID(), PersistenceClassType.valueOf(o.getClass().getSimpleName()));
    }

    public static MySQLPersistenceId createPersistentId(PersistentDwoProfile o) {
                return new MySQLPersistenceId(o.getDwoProfileID(), PersistenceClassType.valueOf(o.getClass().getSimpleName()));
    }

// Don't need this cached.    
//    public static MySQLPersistenceId createPersistentId(PersistentDwoSystemParameters o) {
//                return new MySQLPersistenceId(o.getUserID(), PersistenceClassType.valueOf(o.getClass().getSimpleName()));
//    }


    public static MySQLPersistenceId createPersistentId(PersistentHasRole o) {
                return new MySQLPersistenceId(o.getPersistentHasRolePK().getId(), PersistenceClassType.valueOf(o.getClass().getSimpleName()));
    }

//    public static MySQLPersistenceId createPersistentId(PersistentImage o) {
//                return new MySQLPersistenceId(o.(), PersistenceClassType.valueOf(o.getClass().getSimpleName()));
//    }

//    public static MySQLPersistenceId createPersistentId(PersistentJars o) {
//                return new MySQLPersistenceId(o.getKey(), PersistenceClassType.valueOf(o.getClass().getSimpleName()));
//    }

    public static MySQLPersistenceId createPersistentId(PersistentRole o) {
                return new MySQLPersistenceId(o.getGroupID(), PersistenceClassType.valueOf(o.getClass().getSimpleName()));
    }

    public static MySQLPersistenceId createPersistentId(PersistentSamlUser o) {
                return new MySQLPersistenceId(o.getId(), PersistenceClassType.valueOf(o.getClass().getSimpleName()));
    }

    public static MySQLPersistenceId createPersistentId(PersistentSchool o) {
                return new MySQLPersistenceId(o.getSchoolID(), PersistenceClassType.valueOf(o.getClass().getSimpleName()));
    }

    public static MySQLPersistenceId createPersistentId(PersistentSchoolClass o) {
                return new MySQLPersistenceId(o.getClassID(), PersistenceClassType.valueOf(o.getClass().getSimpleName()));
    }

    public static MySQLPersistenceId createPersistentId(PersistentSchoolGroup o) {
                return new MySQLPersistenceId(o.getSchoolGroupID(), PersistenceClassType.valueOf(o.getClass().getSimpleName()));
    }

    public static MySQLPersistenceId createPersistentId(PersistentScoContext o) {
                return new MySQLPersistenceId(o.getScoID(), PersistenceClassType.valueOf(o.getClass().getSimpleName()));
    }

    public static MySQLPersistenceId createPersistentId(PersistentScoData o) {
                return new MySQLPersistenceId(o.getScoID(), PersistenceClassType.valueOf(o.getClass().getSimpleName()));
    }

    public static MySQLPersistenceId createPersistentId(PersistentStudentOfClass o) {
                return new MySQLPersistenceId(o.getPersistentStudentOfClassPK().getId(), PersistenceClassType.valueOf(o.getClass().getSimpleName()));
    }

    public static MySQLPersistenceId createPersistentId(PersistentStudentScoContext o) {
                return new MySQLPersistenceId(o.getStudentSco(), PersistenceClassType.valueOf(o.getClass().getSimpleName()));
    }

    public static MySQLPersistenceId createPersistentId(PersistentStudentScoData o) {
                return new MySQLPersistenceId(o.getStudentSco(), PersistenceClassType.valueOf(o.getClass().getSimpleName()));
    }

    public static MySQLPersistenceId createPersistentId(PersistentTeacherOfClass o) {
                return new MySQLPersistenceId(o.getPersistentTeacherOfClassPK().getId(), PersistenceClassType.valueOf(o.getClass().getSimpleName()));
    }

    public static MySQLPersistenceId createPersistentId(PersistentUser o) {
                return new MySQLPersistenceId(o.getUserID(), PersistenceClassType.valueOf(o.getClass().getSimpleName()));
    }

}
