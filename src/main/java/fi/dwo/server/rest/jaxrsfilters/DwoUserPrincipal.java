package fi.dwo.server.rest.jaxrsfilters;

import java.security.Principal;

import fi.dwo.commons.persistence.entities.PersistentUser;
import nl.uu.fi.dwo.rest.dom.entities.RoleType;

public class DwoUserPrincipal implements Principal {

    private PersistentUser u;
    private RoleType role;

    DwoUserPrincipal(PersistentUser u) {
        this.u = u;
        this.role = null;
    }

    @Override
    public String getName() {
        return u.getUsername();
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