package fi.dwo.server.PersistentDataManagers.core;

import fi.dwo.commons.persistence.entities.PersistentRole;
import fi.dwo.server.persistence.DwoEmfFactory;
import nl.uu.fi.dwo.rest.dom.entities.RoleType;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

/**
 * Manages roles in the persistent storage. Roles can not not be changed without
 * breaking the database therefore. Role creation, delete and updates should disabled/
 * commented out in the source.
 * 
 * However source code is implemented.
 *
 * @author Gert van der Plas
 */
public class RoleManager {
	
	
    private static final Logger LOG = Logger.getLogger(RoleManager.class.getName());

    public static List<PersistentRole> findEntities() {
        return Arrays.asList(PersistentRole.roles);
    }

    public static PersistentRole findEntity(Long id) throws PersistenceException{
        try {
            return PersistentRole.roles[id.intValue()];
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "The PersistentRole with " + id + " was not found.", e);
            throw new PersistenceException(e.getMessage(), e);
        } 
    }

    public static int getEntityCount() {
    	return PersistentRole.roles.length;
    }
}
