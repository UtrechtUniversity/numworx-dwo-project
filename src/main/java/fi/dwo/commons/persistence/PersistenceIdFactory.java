package fi.dwo.commons.persistence;

import nl.uu.fi.dwo.rest.persistence.PersistenceId;
import fi.dwo.commons.persistence.entities.PersistentSchool;
import fi.dwo.commons.persistence.entities.PersistentRole;
import fi.dwo.commons.persistence.entities.PersistentStudentScoData;
import fi.dwo.commons.persistence.entities.PersistentTeacherOfClass;
import fi.dwo.commons.persistence.entities.PersistentScoContext;
import fi.dwo.commons.persistence.entities.PersistentDwoProfile;
import fi.dwo.commons.persistence.entities.PersistentSchoolClass;
import fi.dwo.commons.persistence.entities.PersistentUser;
import fi.dwo.commons.persistence.entities.PersistentSchoolGroup;
import fi.dwo.commons.persistence.entities.PersistentCourse;
import fi.dwo.commons.persistence.entities.PersistentAppletConfig;
import fi.dwo.commons.persistence.entities.PersistentScoData;
import fi.dwo.commons.persistence.entities.PersistentSamlUser;
import fi.dwo.commons.persistence.entities.PersistentApplet;
import fi.dwo.commons.persistence.entities.PersistentStudentOfClass;
import fi.dwo.commons.persistence.entities.PersistentStudentScoContext;
import fi.dwo.commons.persistence.entities.PersistentHasRole;
import fi.dwo.commons.persistence.entities.PersistentClassCourse;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Current factory only supports MySql.
 *
 * @see MySQLPeristenceId.
 *
 * @author G.A.J. van der Plas
 */
public class PersistenceIdFactory {

    private static final Logger LOG = Logger.getLogger(PersistenceIdFactory.class.getName());

    public enum DatabaseType {
        MySQL
    };

    /**
     *
     * @param o
     * @return a PersistenceId or null in case of an unsupported type.
     */
    public static PersistenceId createPersistenceId(Object o) {
        if (o instanceof PersistentApplet) {
            return ((PersistentApplet) o).buildPersistenceId();
        }
        if (o instanceof PersistentAppletConfig) {
            return ((PersistentAppletConfig) o).buildPersistenceId();
        }
        if (o instanceof PersistentClassCourse) {
            return ((PersistentClassCourse) o).buildPersistenceId();
        }
        if (o instanceof PersistentCourse) {
            return ((PersistentCourse) o).buildPersistenceId();
        }
        if (o instanceof PersistentDwoProfile) {
            return ((PersistentDwoProfile) o).buildPersistenceId();
        }
        if (o instanceof PersistentHasRole) {
            return ((PersistentHasRole) o).buildPersistenceId();
        }
        if (o instanceof PersistentRole) {
            return ((PersistentRole) o).buildPersistenceId();
        }
        if (o instanceof PersistentSamlUser) {
            return ((PersistentSamlUser) o).buildPersistenceId();
        }
        if (o instanceof PersistentSchool) {
            return ((PersistentSchool) o).buildPersistenceId();
        }
        if (o instanceof PersistentSchoolClass) {
            return ((PersistentSchoolClass) o).buildPersistenceId();
        }
        if (o instanceof PersistentSchoolGroup) {
            return ((PersistentSchoolGroup) o).buildPersistenceId();
        }
        if (o instanceof PersistentScoContext) {
            return ((PersistentScoContext) o).buildPersistenceId();
        }
        if (o instanceof PersistentScoData) {
            return ((PersistentScoData) o).buildPersistenceId();
        }
        if (o instanceof PersistentStudentOfClass) {
            return ((PersistentStudentOfClass) o).buildPersistenceId();
        }
        if (o instanceof PersistentStudentScoContext) {
            return ((PersistentStudentScoContext) o).buildPersistenceId();
        }
        if (o instanceof PersistentStudentScoData) {
            return ((PersistentStudentScoData) o).buildPersistenceId();
        }
        if (o instanceof PersistentTeacherOfClass) {
            return ((PersistentTeacherOfClass) o).buildPersistenceId();
        }
        if (o instanceof PersistentUser) {
            return ((PersistentUser) o).buildPersistenceId();
        }
        LOG.log(Level.SEVERE, "Factory was asked to create an unsupported data type.");
        return null;
    }

    // needs a way to extract a persistentObject key for the Object.
    //getPersistentAppletId(PersistenceId id){
    //  if(id's type is PersistentApplet){
    //  return proper type of id
    //  else(id's type is wrong throw error)
    //}
    public static PersistenceId createPersistentId(PersistentApplet o) {
        return o.buildPersistenceId();
    }

    public static PersistenceId createPersistentId(PersistentAppletConfig o) {
        return o.buildPersistenceId();
    }

    public static PersistenceId createPersistentId(PersistentClassCourse o) {
        return o.buildPersistenceId();
    }

    public static PersistenceId createPersistentId(PersistentCourse o) {
        return o.buildPersistenceId();
    }

    public static PersistenceId createPersistentId(PersistentDwoProfile o) {
        return o.buildPersistenceId();
    }

//    public static PersistenceId createPersistenceId(PersistentDwoSystemParameters o) {
//        return (PersistenceId) MySQLPersistenceId.createPersistenceId(o);
//    }
    public static PersistenceId createPersistentId(PersistentHasRole o) {
        return o.buildPersistenceId();
    }

//    public static PersistenceId createPersistenceId(PersistentImage o) {
//        return (PersistenceId) MySQLPersistenceId.createPersistenceId(o);
//    }
//    public static PersistenceId createPersistenceId(PersistentJars o) {
//        return (PersistenceId) MySQLPersistenceId.createPersistenceId(o);
//    }
    public static PersistenceId createPersistentId(PersistentRole o) {
        return o.buildPersistenceId();
    }

    public static PersistenceId createPersistentId(PersistentSamlUser o) {
        return o.buildPersistenceId();
    }

    public static PersistenceId createPersistentId(PersistentSchool o) {
        return o.buildPersistenceId();
    }

    public static PersistenceId createPersistentId(PersistentSchoolClass o) {
        return o.buildPersistenceId();
    }

    public static PersistenceId createPersistentId(PersistentSchoolGroup o) {
        return o.buildPersistenceId();
    }

    public static PersistenceId createPersistentId(PersistentScoContext o) {
        return o.buildPersistenceId();
    }

    public static PersistenceId createPersistentId(PersistentScoData o) {
        return o.buildPersistenceId();
    }

    public static PersistenceId createPersistentId(PersistentStudentOfClass o) {
        return o.buildPersistenceId();
    }

    public static PersistenceId createPersistentId(PersistentStudentScoContext o) {
        return o.buildPersistenceId();
    }

    public static PersistenceId createPersistentId(PersistentStudentScoData o) {
        return o.buildPersistenceId();
    }

    public static PersistenceId createPersistentId(PersistentTeacherOfClass o) {
        return o.buildPersistenceId();
    }

    public static PersistenceId createPersistentId(PersistentUser o) {
        return o.buildPersistenceId();
    }
}
