package fi.dwo.server.rest.jaxrsfilters;

import java.security.Principal;

import fi.dwo.commons.persistence.entities.PersistentSchoolGroup;
import fi.dwo.commons.persistence.entities.PersistentUser;
import nl.uu.fi.dwo.rest.dom.entities.RoleType;

public class DwoUserPrincipal implements Principal {

    private final PersistentUser u;
    private final PersistentSchoolGroup sg;
    private final RoleType role;

    DwoUserPrincipal(PersistentUser u) {
        this.u = u;
        this.sg = null;
        this.role = null;
    }
    DwoUserPrincipal(PersistentUser u, PersistentSchoolGroup sg) {
    	this.u = u;
    	this.sg = sg;
    	this.role = RoleType.values()[sg.getRole().getGroupID().intValue()];
    }
    

    @Override
    public String getName() {
        return u.getUsername();
    }

    /**
	 * @return the role
	 */
	public RoleType getRole() {
		return role;
	}
	/**
	 * @return the sg
	 */
	public PersistentSchoolGroup getSg() {
		return sg;
	}
	/**
     * if (principal instanceof DwoUserPrincipal) user principal.getUser();
     *
     * @return persistentuser
     */
    public PersistentUser getUser() {
        return u;
    }

    public String toString() {
        return getName();
    }
}