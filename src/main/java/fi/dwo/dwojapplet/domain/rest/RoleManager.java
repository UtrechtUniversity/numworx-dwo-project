/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.dwo.dwojapplet.domain.rest;

import fi.dwo.commons.persistence.entities.PersistentRole;
import fi.dwo.dwojapplet.domain.DwoHelper;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;

/**
 * Returns a list of existing Roles.
 * 
 * It sole purpose is to be called from the DwoHelper. Otherwise it should not be called.
 *
 * @author G.A.J. van der Plas
 */
public class RoleManager {

    private static final Logger log = Logger.getLogger(RoleManager.class.getName());

    /**
     * Returns the user data if properly logged in. The information is extracted
     * from the security context.
     *
     * @param sc
     * @return Returns null if there was an error.
     */
    public static List<PersistentRole> getRoles() {
        //login to rest service
        List<PersistentRole> roles;
        WebTarget target = DwoHelper.getWebTargetRest();
            GenericType<ArrayList<PersistentRole>> dwoParamType = new GenericType<ArrayList<PersistentRole>>() {
        };
        Response response = target
                .path("/rest/public/roles/get/json")
                .request().get();
        if (response.getStatus() != 200) {
            // failed login
            System.out.println("Code: " + response.getStatus() + ". Reason: " + response.getStatusInfo().getReasonPhrase());
            return null;
        } else {
            roles = response.readEntity(dwoParamType);
//            Set return value
//            roles = (List<PersistentRole>) target.path("/rest/public/roles/get/json").request().accept(MediaType.APPLICATION_JSON).get(dwoParamType);
            log.log(Level.FINER, "Fetched {0} roles.", new Object[]{roles.size()});
        }
        return roles;
    }        
}
