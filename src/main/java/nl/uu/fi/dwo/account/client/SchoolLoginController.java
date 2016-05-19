/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.uu.fi.dwo.account.client;

import fi.dwo.gwt.lib.rest.CallManagers.SecuredUserSchoolLoginManager;
import fi.dwo.gwt.lib.rest.DwoGlobalVars;
import fi.dwo.rest.dom.entities.DomSchool;
import fi.dwo.rest.dom.entities.DomSchoolRoleAndClass;
import fi.dwo.rest.dom.entities.DomSchoolsRolesAndClasses;
import fi.dwo.rest.dom.entities.DomUserFull;
import java.util.logging.Logger;

/**
 *
 * @author Gert van der Plas
 */
public class SchoolLoginController {

    private static final Logger LOG = Logger.getLogger(SchoolLoginController.class.getName());

    private SchoolLoginPanel view = null;
    private DomUserFull currentUser = null;
    private SecuredUserSchoolLoginManager manager = new SecuredUserSchoolLoginManager();
    
    private DomSchoolRoleAndClass selectedSrc;
    private DomSchool nullSchool;
    private DomSchoolsRolesAndClasses srcs;


    public SchoolLoginController(SchoolLoginPanel view, DomUserFull user) {
        this.view = view;
        this.init(user);
    }

    public void init(DomUserFull user) {
        setCurrentUser(user);
        nullSchool = DwoGlobalVars.instance().getNullSchool();
    }

    /**
     * @return the currentUser
     */
    public DomUserFull getCurrentUser() {
        return currentUser;
    }

    /**
     * @param currentUser the currentUser to set
     */
    public void setCurrentUser(DomUserFull currentUser) {
        this.currentUser = currentUser;
    }
}
