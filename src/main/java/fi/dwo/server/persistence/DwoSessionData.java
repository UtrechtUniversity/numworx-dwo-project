/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.dwo.server.persistence;

import fi.dwo.commons.persistence.PersistentHasRole;
import fi.dwo.commons.persistence.PersistentUser;

/**
 * An object to store all relevant DWO SessionData. 
 * 
 * @author plas0006
 */
class DwoSessionData {
    private PersistentUser loginUser;
    private PersistentHasRole loginRole;

    /**
     * @return the loginUser
     */
    protected PersistentUser getLoginUser() {
        return loginUser;
    }

    /**
     * @param loginUser the loginUser to set
     */
    protected void setLoginUser(PersistentUser loginUser) {
        this.loginUser = loginUser;
    }

    /**
     * @return the loginRole
     */
    protected PersistentHasRole getLoginRole() {
        return loginRole;
    }

    /**
     * @param loginRole the loginRole to set
     */
    protected void setLoginRole(PersistentHasRole loginRole) {
        this.loginRole = loginRole;
    }
}
