package fi.dwo.commons.persistence;

import fi.dwo.commons.persistence.entities.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Current factory only supports MySql.
 *
 * @see MySQLPeristenceId.
 *
 * @author Gert van der Plas <gertvdplas@gmail.com>
 */
public class PersistenceIdFactory {

    private static final Logger log = Logger.getLogger(PersistenceIdFactory.class.getName());
    
    public enum DatabaseType{ MySQL};

    /**
     *
     * @param o
     * @return a PersistenceId or null in case of an unsupported type.
     */
    public static PersistenceId createPersistenceId(Object o) {
        if (o instanceof PersistentApplet) {
            return createPersistentId((PersistentApplet) o);
        }
        if (o instanceof PersistentAppletConfig) {
            return createPersistentId((PersistentAppletConfig) o);
        }
        if (o instanceof PersistentClassCourse) {
            return createPersistentId((PersistentClassCourse) o);
        }
        if (o instanceof PersistentCourse) {
            return createPersistentId((PersistentCourse) o);
        }
        if (o instanceof PersistentCourseSequence) {
            return createPersistentId((PersistentCourseSequence) o);
        }
        if (o instanceof PersistentDwoProfile) {
            return createPersistentId((PersistentDwoProfile) o);
        }
        if (o instanceof PersistentHasRole) {
            return createPersistentId((PersistentHasRole) o);
        }
        if (o instanceof PersistentRole) {
            return createPersistentId((PersistentRole) o);
        }
        if (o instanceof PersistentSamlUser) {
            return createPersistentId((PersistentSamlUser) o);
        }
        if (o instanceof PersistentSchool) {
            return createPersistentId((PersistentSchool) o);
        }
        if (o instanceof PersistentSchoolClass) {
            return createPersistentId((PersistentSchoolClass) o);
        }
        if (o instanceof PersistentSchoolGroup) {
            return createPersistentId((PersistentSchoolGroup) o);
        }
        if (o instanceof PersistentScoContext) {
            return createPersistentId((PersistentScoContext) o);
        }
        if (o instanceof PersistentScoData) {
            return createPersistentId((PersistentScoData) o);
        }
        if (o instanceof PersistentStudentOfClass) {
            return createPersistentId((PersistentStudentOfClass) o);
        }
        if (o instanceof PersistentStudentScoContext) {
            return createPersistentId((PersistentStudentScoContext) o);
        }
        if (o instanceof PersistentStudentScoData) {
            return createPersistentId((PersistentStudentScoData) o);
        }
        if (o instanceof PersistentTeacherOfClass) {
            return createPersistentId((PersistentTeacherOfClass) o);
        }
        if (o instanceof PersistentUser) {
            return createPersistentId((PersistentUser) o);
        }
        log.log(Level.SEVERE, "Factory was asked to create an unsupported data type.");
        return null;
    }

    public static PersistenceId createPersistentId(PersistentApplet o) {
        return (PersistenceId) MySQLPersistenceId.createPersistentId(o);
    }

    public static PersistenceId createPersistentId(PersistentAppletConfig o) {
        return (PersistenceId) MySQLPersistenceId.createPersistentId(o);
    }

    public static PersistenceId createPersistentId(PersistentClassCourse o) {
        return (PersistenceId) MySQLPersistenceId.createPersistentId(o);
    }

    public static PersistenceId createPersistentId(PersistentCourse o) {
        return (PersistenceId) MySQLPersistenceId.createPersistentId(o);
    }

    public static PersistenceId createPersistentId(PersistentCourseSequence o) {
        return (PersistenceId) MySQLPersistenceId.createPersistentId(o);
    }

    public static PersistenceId createPersistentId(PersistentDwoProfile o) {
        return (PersistenceId) MySQLPersistenceId.createPersistentId(o);
    }

//    public static PersistenceId createPersistenceId(PersistentDwoSystemParameters o) {
//        return (PersistenceId) MySQLPersistenceId.createPersistenceId(o);
//    }
    public static PersistenceId createPersistentId(PersistentHasRole o) {
        return (PersistenceId) MySQLPersistenceId.createPersistentId(o);
    }

//    public static PersistenceId createPersistenceId(PersistentImage o) {
//        return (PersistenceId) MySQLPersistenceId.createPersistenceId(o);
//    }
//    public static PersistenceId createPersistenceId(PersistentJars o) {
//        return (PersistenceId) MySQLPersistenceId.createPersistenceId(o);
//    }
    public static PersistenceId createPersistentId(PersistentRole o) {
        return (PersistenceId) MySQLPersistenceId.createPersistentId(o);
    }

    public static PersistenceId createPersistentId(PersistentSamlUser o) {
        return (PersistenceId) MySQLPersistenceId.createPersistentId(o);
    }

    public static PersistenceId createPersistentId(PersistentSchool o) {
        return (PersistenceId) MySQLPersistenceId.createPersistentId(o);
    }

    public static PersistenceId createPersistentId(PersistentSchoolClass o) {
        return (PersistenceId) MySQLPersistenceId.createPersistentId(o);
    }

    public static PersistenceId createPersistentId(PersistentSchoolGroup o) {
        return (PersistenceId) MySQLPersistenceId.createPersistentId(o);
    }

    public static PersistenceId createPersistentId(PersistentScoContext o) {
        return (PersistenceId) MySQLPersistenceId.createPersistentId(o);
    }

    public static PersistenceId createPersistentId(PersistentScoData o) {
        return (PersistenceId) MySQLPersistenceId.createPersistentId(o);
    }

    public static PersistenceId createPersistentId(PersistentStudentOfClass o) {
        return (PersistenceId) MySQLPersistenceId.createPersistentId(o);
    }

    public static PersistenceId createPersistentId(PersistentStudentScoContext o) {
        return (PersistenceId) MySQLPersistenceId.createPersistentId(o);
    }

    public static PersistenceId createPersistentId(PersistentStudentScoData o) {
        return (PersistenceId) MySQLPersistenceId.createPersistentId(o);
    }

    public static PersistenceId createPersistentId(PersistentTeacherOfClass o) {
        return (PersistenceId) MySQLPersistenceId.createPersistentId(o);
    }

    public static PersistenceId createPersistentId(PersistentUser o) {
        return (PersistenceId) MySQLPersistenceId.createPersistentId(o);
    }
}
