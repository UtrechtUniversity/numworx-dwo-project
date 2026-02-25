package fi.dwo.server.PersistentDataManagers.core;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;

import fi.dwo.commons.persistence.entities.PersistentEntity;
import fi.dwo.server.persistence.DwoEmfFactory;

abstract class AbstractManager {
    private static final Logger LOG = Logger.getLogger(AbstractManager.class.getName());

    static EntityManager getEntityManager() {
        EntityManager em = DwoEmfFactory.getEntityManager();
        return em;
    }

    static <T extends PersistentEntity> T find(Object id, Class<T> cls) {
        EntityManager em = getEntityManager();
        try {
            return em.find(cls, id);
         } catch (PersistenceException e) {
            LOG.log(Level.FINE, "The " + cls.getSimpleName() + " with " + id + " was not found.", e);
            throw e;
       } finally {
            em.close();
        }

    }
    
    
    /**
     * Create.
     *
     * @param classCourse
     */
    public static <T extends PersistentEntity> T create(T classCourse) throws PersistenceException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            classCourse.changeTimestamp();
            em.persist(classCourse);
            em.getTransaction().commit();
            return classCourse;
        } 
        catch (PersistenceException e) {
            LOG.log(Level.SEVERE, "Can't create the PersistentEntity.", e);
        	throw e;
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Can't create the PersistentEntity.", e);
            throw new PersistenceException(e);
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

}
