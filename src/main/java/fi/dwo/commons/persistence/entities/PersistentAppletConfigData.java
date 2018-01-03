/**
 * 
 */
package fi.dwo.commons.persistence.entities;

import java.io.Serializable;

import nl.uu.fi.dwo.rest.persistence.PersistenceClassType;
import nl.uu.fi.dwo.rest.persistence.PersistenceId;

/**
 * Entity to hold initial launchdata data.
 * @author wim
 * @since 1.5.1
 */
public class PersistentAppletConfigData implements Serializable {
    /**
     * Builds a persistenceId from the parameters given.
     *
     * @param aProfileId
     * @return
     */
    public static PersistenceId buildPersistenceId(Long pid) {
    	if(pid == null) return null;
        PersistenceId id = new PersistenceId();
        id.setIdString(String.format("MYSQL;%s;%020d",
                PersistenceClassType.PersistentAppletConfigData.name(), pid));
        return id;
    }

}
