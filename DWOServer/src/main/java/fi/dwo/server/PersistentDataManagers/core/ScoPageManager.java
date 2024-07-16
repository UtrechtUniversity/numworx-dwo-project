package fi.dwo.server.PersistentDataManagers.core;

import java.util.List;
import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import javax.persistence.TypedQuery;

import fi.dwo.commons.persistence.entities.PersistentScoContext;
import fi.dwo.commons.persistence.entities.PersistentScoPage;
import fi.dwo.commons.persistence.entities.PersistentStudentScoContext;
import fi.dwo.server.persistence.DwoEmfFactory;

public class ScoPageManager {
    private static final Logger LOG = Logger.getLogger(ScoPageManager.class.getName());

    private static EntityManager getEntityManager() {
        EntityManager em = DwoEmfFactory.getEntityManager();
        return em;
    }

    public static void create(PersistentScoPage p) throws PersistenceException {
    	EntityManager em = getEntityManager();
    	try {
    		em.getTransaction().begin();
    		em.persist(p);
    		em.getTransaction().commit();
    	} finally {
    		em.close();
    	}
    }
    
    public static PersistentScoPage edit(PersistentScoPage p) throws PersistenceException {
    	EntityManager em = getEntityManager();
    	try {
    		em.getTransaction().begin();
    		p = em.merge(p);
    		em.getTransaction().commit();
    	} finally {
    		em.close();
    	}
    	return p;
    }
    
    public static void destroy(PersistentScoPage p ) throws PersistenceException {
    	EntityManager em = getEntityManager();
    	try {
    		em.getTransaction().begin();
    		p = em.getReference(PersistentScoPage.class, p.getId());
    		em.remove(p);
    		em.getTransaction().commit();
    	} finally {
    		em.close();
    	}
    }
    
    
    public static List<PersistentScoPage> find(PersistentScoContext sco) throws PersistenceException
    {
    	EntityManager em = getEntityManager();
    	try {
    		em.getTransaction().begin();
    		TypedQuery<PersistentScoPage> q;
    		q = em.createNamedQuery("PersistentScoPage.bySco", PersistentScoPage.class);
    		q.setParameter("scoID", sco.getScoID());
    		return q.getResultList();
    	} finally {
    		em.close();
    	}
    }

    public static List<PersistentScoPage> find(PersistentStudentScoContext ssc) throws PersistenceException
    {
    	EntityManager em = getEntityManager();
    	try {
    		em.getTransaction().begin();
    		TypedQuery<PersistentScoPage> q;
    		q = em.createNamedQuery("PersistentScoPage.byStudentSco", PersistentScoPage.class);
    		q.setParameter("scoID", ssc.getScoID());
    		//q.setParameter("hasRolePK", ssc.getPersistentHasRolePK());
    		q.setParameter("userID", ssc.getPersistentHasRolePK().getUserID());
    		q.setParameter("schoolGroupID", ssc.getPersistentHasRolePK().getSchoolGroupID());
    		return q.getResultList();
    	} finally {
    		em.close();
    	}
    }


}
