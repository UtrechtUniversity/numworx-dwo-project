/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.dwo.server.PersistentEntityManagers;

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
 *
 * @author G.A.J. van der Plas
 */
public class DwoSystemParametersManager implements Serializable {

   // private static final Logger LOG = Logger.getLogger(DwoSystemParametersManager.class.getName());

    private static EntityManager getEntityManager() {
        EntityManager em = DwoEmfFactory.createEntityManager();
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
            if (findDwoSystemParameters(dwoSystemParameters.getName()) != null) {
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
                if (findDwoSystemParameters(id) == null) {
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

    public static List<PersistentDwoSystemParameters> findDwoSystemParametersEntities() {
        return findDwoSystemParametersEntities(true, -1, -1);
    }

    public static List<PersistentDwoSystemParameters> findDwoSystemParametersEntities(int maxResults, int firstResult) {
        return findDwoSystemParametersEntities(false, maxResults, firstResult);
    }

    private static List<PersistentDwoSystemParameters> findDwoSystemParametersEntities(boolean all, int maxResults, int firstResult) {
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

    public static PersistentDwoSystemParameters findDwoSystemParameters(String id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(PersistentDwoSystemParameters.class, id);
        } finally {
            em.close();
        }
    }

    public static int getDwoSystemParametersCount() {
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
     * @param userName
     * @return 
     */
    public static PersistentDwoSystemParameters findByName(String paramName) {
        EntityManager em = DwoEmfFactory.createEntityManager();
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
