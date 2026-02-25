package fi.dwo.server.PersistentDataManagers.core;

import java.util.List;
import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import fi.dwo.commons.persistence.entities.PersistentScoContext;
import fi.dwo.commons.persistence.entities.PersistentScoPage;
import fi.dwo.commons.persistence.entities.PersistentScoPagePK;
import fi.dwo.commons.persistence.entities.PersistentStudentScoContext;
import fi.dwo.server.persistence.DwoEmfFactory;

public class ScoPageManager extends AbstractManager {
    private static final Logger LOG = Logger.getLogger(ScoPageManager.class.getName());
    
    public static PersistentScoPage edit(PersistentScoPage p) throws PersistenceException {
    	EntityManager em = getEntityManager();
    	try {
    		em.getTransaction().begin();
    		p.changeTimestamp();
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

    public static void destroyStudentWork(PersistentScoContext sco) throws PersistenceException {
    	EntityManager em = getEntityManager();
    	try {
    		em.getTransaction().begin();
    		Query q = em.createQuery("DELETE FROM PersistentScoPage p WHERE p.id.scoID = :scoID and p.id.userID <> 0");
    		q.setParameter("scoID", sco.getScoID());
    		q.executeUpdate();
    		em.getTransaction().commit();   		
    	} finally {
    		em.close();
    	}
 	
    }

	public static PersistentScoPage findEntity(PersistentScoPagePK id) {
    	return find(id, PersistentScoPage.class);
	}
    
    
}
