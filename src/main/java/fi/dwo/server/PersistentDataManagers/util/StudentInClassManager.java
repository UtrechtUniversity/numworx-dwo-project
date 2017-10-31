package fi.dwo.server.PersistentDataManagers.util;

import fi.dwo.commons.persistence.entities.PersistentSchoolClass;
import fi.dwo.commons.persistence.entities.PersistentStudentInClass;
import fi.dwo.commons.persistence.entities.PersistentStudentOfClass;
import fi.dwo.commons.persistence.entities.PersistentUser;
import fi.dwo.server.persistence.DwoEmfFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManager;

/**
 * Manages class courses in the persistent storage.
 *
 * @author G.A.J. van der Plas
 */
public class StudentInClassManager {

    private static final Logger LOG = Logger.getLogger(StudentInClassManager.class.getName());

    private static EntityManager getEntityManager() {
        EntityManager em = DwoEmfFactory.getEntityManager();
        return em;
    }
    public static List<PersistentStudentInClass> findEntities(PersistentSchoolClass schoolClass){
         EntityManager em = getEntityManager();
        try {
            javax.persistence.Query q = em.createQuery("SELECT c, u FROM PersistentStudentOfClass c, PersistentUser u where c.persistentStudentOfClassPK.userID = u.userID and c.persistentStudentOfClassPK.classID=:classID");
            q.setParameter("classID", schoolClass.getClassID());
            List<Object[]> list = q.getResultList();
            List<PersistentStudentInClass> result = new ArrayList<PersistentStudentInClass>(list.size());
            for(Object[] o: list){
                result.add(PersistentStudentInClass.build((((PersistentStudentOfClass) o[0])),(PersistentUser) o[1]));
            }
            
            LOG.log(Level.FINE, "PersistentStudentInClassManager retrieved {0} PersistentStudentInClass for classId {1}", new Object[]{list.size(), schoolClass.getClassID()});
            return result;
        } finally {
            em.close();
        }
    }       
    
}
