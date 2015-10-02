package fi.dwo.server.PersistentDataManagers.core;

import fi.dwo.commons.persistence.entities.PersistentDwoSystemParameters;
import fi.dwo.server.persistence.DwoEmfFactory;
import fi.dwo.server.persistence.exceptions.NonexistentEntityException;
import fi.dwo.server.persistence.exceptions.PreexistingEntityException;
import java.io.Serializable;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

/**
 * DwoSystemParametersManager.
 * 
 * @author G.A.J. van der Plas
 */
public class DwoSystemParametersManager implements Serializable {

   // private static final Logger LOG = Logger.getLogger(DwoSystemParametersManager.class.getName());

    private static EntityManager getEntityManager() {
        EntityManager em = DwoEmfFactory.getEntityManager();
        return em;
    }

    public static void create(PersistentDwoSystemParameters dwoSystemParameters) throws PreexistingEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            em.persist(dwoSystemParameters);
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findEntity(dwoSystemParameters.getName()) != null) {
                throw new PreexistingEntityException("DwoSystemParameters " + dwoSystemParameters + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public static void edit(PersistentDwoSystemParameters dwoSystemParameters) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            dwoSystemParameters = em.merge(dwoSystemParameters);
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                String id = dwoSystemParameters.getName();
                if (findEntity(id) == null) {
                    throw new NonexistentEntityException("The dwoSystemParameters with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public static void destroy(String id) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            PersistentDwoSystemParameters dwoSystemParameters;
            try {
                dwoSystemParameters = em.getReference(PersistentDwoSystemParameters.class, id);
                dwoSystemParameters.getName();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The dwoSystemParameters with id " + id + " no longer exists.", enfe);
            }
            em.remove(dwoSystemParameters);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public static List<PersistentDwoSystemParameters> findEntities() {
        return findEntities(true, -1, -1);
    }

    public static List<PersistentDwoSystemParameters> findEntities(int maxResults, int firstResult) {
        return findEntities(false, maxResults, firstResult);
    }

    private static List<PersistentDwoSystemParameters> findEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(PersistentDwoSystemParameters.class));
            Query q = em.createQuery(cq);
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    public static PersistentDwoSystemParameters findEntity(String id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(PersistentDwoSystemParameters.class, id);
        } finally {
            em.close();
        }
    }

    public static int getEntityCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<PersistentDwoSystemParameters> rt = cq.from(PersistentDwoSystemParameters.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    

    /**
     * returns null if no user with that name was found.
     * 
     * @param paramName
     * @return 
     */
    public static PersistentDwoSystemParameters findByName(String paramName) {
        EntityManager em = DwoEmfFactory.getEntityManager();
        PersistentDwoSystemParameters param = null;
        try {
            javax.persistence.Query q = em.createNamedQuery("PersistentDwoSystemParameters.findByName");
            q.setParameter(":name", paramName);
            param = (PersistentDwoSystemParameters) q.getSingleResult();
        }catch(NoResultException e){
            return null;
        }catch(Exception e){
            throw new PersistenceException(e);
        }finally {
            em.close();
        }
        return param;
    }    
    
}
