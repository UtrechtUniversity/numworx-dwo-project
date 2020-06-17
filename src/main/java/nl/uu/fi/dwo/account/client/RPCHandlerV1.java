package nl.uu.fi.dwo.account.client;

import nl.uu.fi.dwo.rest.persistence.PersistenceClassType;
import nl.uu.fi.dwo.rest.persistence.PersistenceId;

/**
 * equivalent van de PersistenceFacade voor DWO v1.0
 * 
 * @author velth101
 *
 */
@Deprecated
public abstract class RPCHandlerV1 {

	private final int profile;
	
    /**
     *
     * @param server
     * @param profile
     */
    RPCHandlerV1(int profile) {
		this.profile = profile;
	}
		
	/**
     *
     * @param object
     * @param type
     * @return
     */
    protected static PersistenceId idOf(Object object, PersistenceClassType type) {
		if(object == null || "".equals(object))
				return null;
		PersistenceId id = new PersistenceId();
		id.setIdString("MYSQL;" + type + ";" + object);
		return id;
	}

	
    /**
     *
     * @return
     */
    final int getProfile() {
		return profile;
	}
	
// In Mc2 new String()

    /**
     *
     * @param courseID
     * @return
     */
	protected Object objectToKey(Object courseID) {
		return new Integer(courseID.toString());
	}
	
}
