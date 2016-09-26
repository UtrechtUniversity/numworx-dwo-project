package fi.dwo.server.rest;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.security.PermitAll;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.SecurityContext;






import fi.dwo.commons.persistence.MySQLPersistenceId;
import fi.dwo.commons.persistence.entities.PersistentAppletConfig;
import fi.dwo.commons.persistence.entities.PersistentDwoProfile;
import fi.dwo.commons.persistence.entities.PersistentHasRole;
import fi.dwo.rest.dom.entities.DomAppletConfig;
import fi.dwo.rest.dom.entities.DomDwoProfile;
import fi.dwo.rest.dom.entities.RoleType;
import fi.dwo.rest.entities.RestAppletConfig;
import fi.dwo.rest.entities.RestDwoProfile;
import fi.dwo.rest.exceptions.Dwo2Exception;
import fi.dwo.rest.exceptions.Dwo2ExceptionCode;
import fi.dwo.rest.exceptions.Dwo2RestException;
import fi.dwo.server.PersistentDataManagers.core.AppletConfigManager;
import fi.dwo.server.PersistentDataManagers.core.DwoProfileManager;
import fi.dwo.server.PersistentDataManagers.util.HasRoleUtilManager;


@PermitAll
@Path("/secure/dwoadmin/config")
public class SecuredDwoAdminConfigManager {
    private static final Logger LOG = Logger.getLogger(SecuredDwoAdminConfigManager.class.getName());

    /**
     * Returns the school data to be displayed.
     *
     * @param sc
     * @return 
     */
    @GET
    @Produces({"application/json"})
    @Path("/getList")
    public List<DomAppletConfig> getConfigurations(@Context SecurityContext sc) {
        PersistentHasRole hr = null;
        try {
            hr = HasRoleUtilManager.getCurrentHasRole(sc.getUserPrincipal().getName(), RoleType.ADMIN);
        }
        catch (Dwo2Exception ex) {
            LOG.log(Level.SEVERE, "", ex);
            throw new Dwo2RestException(ex);
        }
        if (hr != null) {
            List<PersistentAppletConfig> profiles = null;
            List<DomAppletConfig> domProfiles;
            try {
            	profiles = AppletConfigManager.findEntities();
                LOG.log(Level.FINER, "Fetched all {0} profiles. ", new Object[]{profiles.size()});
                domProfiles = new ArrayList<>(profiles.size());
                for (PersistentAppletConfig p : profiles) {
                	domProfiles.add(p.createDomAppletConfig());
                }
            }
            catch (Exception e) {
                LOG.log(Level.WARNING, "Unexpected exception", e);
                throw new Dwo2RestException(Dwo2ExceptionCode.Rest_InternalError, "An exception occured while fetching the schools.");
            }
            return domProfiles;
        } else {
            LOG.log(Level.WARNING, "Username {0}: ILLEGAL USER-OPERATION: Trying to access dwoadmin functionality by user with usercode {0}.", new Object[]{sc.getUserPrincipal().getName()});
            throw new Dwo2RestException(Dwo2ExceptionCode.User_IllegalAction, "You Don't Have Permission to access this using usercode " + sc.getUserPrincipal().getName() + ".");
        }
    }
    
    /**
     * Registers a new DwoProfile.
     *
     * @param sc
     * @param restDwoProfile
     * @return
     */
    @PUT
    @Produces({"application/json"})
    @Path("/submit")
    public Boolean submitAppletConfig(@Context SecurityContext sc, RestAppletConfig restDwoProfile) {
        if(restDwoProfile==null){
            throw new Dwo2RestException(Dwo2ExceptionCode.Rest_FormatError, "Incorrect formatted REST-request.");
        }
        PersistentHasRole hr = null;
        DomAppletConfig profile = restDwoProfile.getDomAppletConfig() ;
        try {
            hr = HasRoleUtilManager.getCurrentHasRole(sc.getUserPrincipal().getName(), RoleType.ADMIN);
        }
        catch (Dwo2Exception ex) {
            LOG.log(Level.SEVERE, "", ex);
            throw new Dwo2RestException(ex);
        }

        if (hr != null) {
            // allowed user role
            PersistentAppletConfig p = new PersistentAppletConfig();
            p.setAppletID(profile.getAppletID());
            p.setLanguage(profile.getLanguage());
            p.setLaunchdata(profile.getLaunchdata());
            p.setName(profile.getName());
            
            try {
                AppletConfigManager.create(p);
                return Boolean.TRUE;
            }
            catch (Exception e) {
                throw new Dwo2RestException(Dwo2ExceptionCode.Rest_InternalError, "An exception occured while creating appletConfig " + profile.getName() + ".");
            }
        } else {
            LOG.log(Level.WARNING, "Username {0}: ILLEGAL USER-OPERATION: Trying to access dwoadmin functionality by user with usercode {0}.", new Object[]{sc.getUserPrincipal().getName()});
            throw new Dwo2RestException(Dwo2ExceptionCode.User_IllegalAction, "You Don't Have Permission to access this using usercode " + sc.getUserPrincipal().getName() + ".");
        }
    }

    /**
     * Updates the DwoProfile.
     *
     * @param sc
     * @param restDwoProfile
     * @return
     */
    @PUT
    @Produces({"application/json"})
    @Path("/update")
    public Boolean updateConfig(@Context SecurityContext sc, RestAppletConfig restProfile) {
        if(restProfile==null){
            throw new Dwo2RestException(Dwo2ExceptionCode.Rest_FormatError, "Incorrect formatted REST-request.");
        }
        PersistentHasRole hr = null;
        DomAppletConfig profile = restProfile.getDomAppletConfig();
        try {
            hr = HasRoleUtilManager.getCurrentHasRole(sc.getUserPrincipal().getName(), RoleType.ADMIN);
        }
        catch (Dwo2Exception ex) {
            Logger.getLogger(SecuredDwoAdminSchoolManager.class.getName()).log(Level.SEVERE, "", ex);
            throw new Dwo2RestException(ex);
        }
        if (hr != null) {
            try {
                long id = MySQLPersistenceId.getId(profile.getId());
				PersistentAppletConfig editProfile = AppletConfigManager.findEntity(id);
                //Profile to update.
                editProfile.setAppletID(profile.getAppletID());
                editProfile.setLanguage(profile.getLanguage());
                editProfile.setLaunchdata(profile.getLaunchdata());
                editProfile.setName(profile.getName());
                AppletConfigManager.edit(editProfile);
                return Boolean.TRUE;
            }
            catch (Exception e) {
                LOG.log(Level.SEVERE, "Username " + sc.getUserPrincipal().getName() + ": Unexpected exception", e);
                throw new Dwo2RestException(Dwo2ExceptionCode.Rest_InternalError, "Failed to update profile with name " + profile.getName() + " .");
            }
        } else {
            LOG.log(Level.WARNING, "Username {0}: ILLEGAL USER-OPERATION: Trying to update the appletconfig with name {1}.", new Object[]{sc.getUserPrincipal().getName(), profile.getName()});
            throw new Dwo2RestException(Dwo2ExceptionCode.User_IllegalAction, "You Don't Have Permission to update the appletconfig data.");
        }
    }

}
