/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.dwo.server.PersistentEntityManagers;

import fi.dwo.commons.persistence.entities.PersistentApplet;
import fi.dwo.server.persistence.DwoEmfFactory;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

/**
 * Manages applets in the persistent storage. 
 *
 * @author G.A.J. van der Plas
 */
public class AppletManager {

    private static final Logger LOG = Logger.getLogger(AppletManager.class.getName());

    private static EntityManager getEntityManager() {
        EntityManager em = DwoEmfFactory.getEntityManager();
        return em;
    }

    /**
     * Create.
     *
     * @param persistentApplet
     */
    public static void create(PersistentApplet persistentApplet) throws PersistenceException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            em.persist(persistentApplet);
            em.getTransaction().commit();
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Can't create the PersistentApplet.", e);
            throw new PersistenceException(e);
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    /**
     * Update
     *
     * @param persistentApplet
     * @throws Exception
     */
    public static void edit(PersistentApplet persistentApplet) throws PersistenceException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            persistentApplet = em.merge(persistentApplet);
            em.getTransaction().commit();
        } catch (Exception e) {
            String msg = e.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = persistentApplet.getAppletID();
                if (findEntity(id) == null) {
                    LOG.log(Level.FINE, "The PersistentApplet with " + id + " no longer exists.", e);
                    throw new PersistenceException(e);
                }
            }
            throw new PersistenceException(e);
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    /**
     * Removes a user from the persistent store.
     *
     * @param id
     */
    public static void destroy(Integer id) throws PersistenceException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            PersistentApplet persistentApplet = null;
            try {
                persistentApplet = em.getReference(PersistentApplet.class, id);
                persistentApplet.getAppletID();
            } catch (EntityNotFoundException e) {
                LOG.log(Level.FINE, "The PersistentApplet with " + id + " no longer exists.", e);
                throw new PersistenceException(e);
            }
            em.remove(persistentApplet);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public static List<PersistentApplet> findEntities() {
        return findEntities(true, -1, -1);
    }

    public static List<PersistentApplet> findEntities(int maxResults, int firstResult) {
        return findEntities(false, maxResults, firstResult);
    }

    private static List<PersistentApplet> findEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(PersistentApplet.class));
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

    public static PersistentApplet findEntity(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(PersistentApplet.class, id);
        } finally {
            em.close();
        }
    }

    public static int getEntityCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<PersistentApplet> rt = cq.from(PersistentApplet.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }

    /**
     * returns null if no applet with that name was found.
     * 
     * @param appletName
     * @return 
     */
    public static PersistentApplet findByAppletName(String appletName) {
        EntityManager em = DwoEmfFactory.getEntityManager();
        PersistentApplet applet = null;
        try {
            javax.persistence.Query q = em.createNamedQuery("PersistentApplet.findByAppletName");
            q.setParameter("appletName", appletName);
            applet = (PersistentApplet) q.getSingleResult();
            LOG.log(Level.FINE, "PersistentApplet-manager retrieved user with applet name {0}", new Object[]{applet.getAppletName()});
        }catch(NoResultException e){
            return null;
        }catch(Exception e){
            throw new PersistenceException(e);
        }finally {
            em.close();
        }
        return applet;
    }

}
