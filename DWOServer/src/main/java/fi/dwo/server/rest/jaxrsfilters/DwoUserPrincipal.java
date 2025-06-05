package fi.dwo.server.rest.jaxrsfilters;

import java.security.Principal;

import fi.dwo.commons.persistence.entities.PersistentHasRole;
import fi.dwo.commons.persistence.entities.PersistentSchoolGroup;
import fi.dwo.commons.persistence.entities.PersistentUser;
import nl.uu.fi.dwo.rest.dom.entities.RoleType;

public class DwoUserPrincipal implements Principal {

    private final PersistentUser u;
    private final PersistentSchoolGroup sg;
    private final RoleType role;
    private final PersistentHasRole hr;

    DwoUserPrincipal(PersistentUser u) {
        this.u = u;
        this.sg = null;
        this.hr = null;
        this.role = RoleType.NONE;
    }

    DwoUserPrincipal(PersistentUser u, PersistentSchoolGroup sg) {
    	this.u = u;
    	this.sg = sg;
    	this.hr = null;
    	this.role = RoleType.values()[sg.getGroupID()];
    }
    
    DwoUserPrincipal(PersistentHasRole hr) {
    	this.hr = hr;
    	this.sg = hr.getSchoolGroup();
    	this.u  = hr.getUser();
    	this.role = RoleType.values()[sg.getGroupID()];    	
    }

    DwoUserPrincipal(PersistentUser u, PersistentHasRole hr, PersistentSchoolGroup sg) {
    	this.hr = hr;
    	this.sg = sg;
    	this.u  = u;
    	this.role = RoleType.values()[sg.getGroupID()];    	
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

	/**
	 * @return the hr
	 */
	public PersistentHasRole getHr() {
		return hr;
	}
}