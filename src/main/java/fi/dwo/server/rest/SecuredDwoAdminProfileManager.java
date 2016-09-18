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

import org.eclipse.persistence.oxm.platform.DOMPlatform;

import fi.dwo.commons.persistence.MySQLPersistenceId;
import fi.dwo.commons.persistence.entities.PersistentDwoProfile;
import fi.dwo.commons.persistence.entities.PersistentHasRole;
import fi.dwo.commons.persistence.entities.PersistentSchool;
import fi.dwo.commons.persistence.entities.PersistentSchoolGroup;
import fi.dwo.rest.dom.entities.DomDwoProfile;
import fi.dwo.rest.dom.entities.DomSchoolFull;
import fi.dwo.rest.dom.entities.RoleType;
import fi.dwo.rest.entities.RestDwoProfile;
import fi.dwo.rest.entities.RestSchoolFull;
import fi.dwo.rest.exceptions.Dwo2Exception;
import fi.dwo.rest.exceptions.Dwo2ExceptionCode;
import fi.dwo.rest.exceptions.Dwo2RestException;
import fi.dwo.server.PersistentDataManagers.core.DwoProfileManager;
import fi.dwo.server.PersistentDataManagers.core.SchoolManager;
import fi.dwo.server.PersistentDataManagers.util.HasRoleUtilManager;


@PermitAll
@Path("/secure/dwoadmin/profile")
public class SecuredDwoAdminProfileManager {
    private static final Logger LOG = Logger.getLogger(SecuredDwoAdminProfileManager.class.getName());

    /**
     * Returns the school data to be displayed.
     *
     * @param sc
     * @return 
     */
    @GET
    @Produces({"application/json"})
    @Path("/getList")
    public List<DomDwoProfile> getProfiles(@Context SecurityContext sc) {
        PersistentHasRole hr = null;
        try {
            hr = HasRoleUtilManager.getCurrentHasRole(sc.getUserPrincipal().getName(), RoleType.ADMIN);
        }
        catch (Dwo2Exception ex) {
            LOG.log(Level.SEVERE, "", ex);
            throw new Dwo2RestException(ex);
        }
        if (hr != null) {
            List<PersistentDwoProfile> profiles = null;
            List<DomDwoProfile> domProfiles;
            try {
            	profiles = DwoProfileManager.findEntities();
                LOG.log(Level.FINER, "Fetched all {0} profiles. ", new Object[]{profiles.size()});
                domProfiles = new ArrayList<>(profiles.size());
                for (PersistentDwoProfile p : profiles) {
                	domProfiles.add(p.createDomDwoProfile());
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
    public Boolean submitProfile(@Context SecurityContext sc, RestDwoProfile restDwoProfile) {
        if(restDwoProfile==null){
            throw new Dwo2RestException(Dwo2ExceptionCode.Rest_FormatError, "Incorrect formatted REST-request.");
        }
        PersistentHasRole hr = null;
        DomDwoProfile profile = restDwoProfile.getDomDwoProfile() ;
        try {
            hr = HasRoleUtilManager.getCurrentHasRole(sc.getUserPrincipal().getName(), RoleType.ADMIN);
        }
        catch (Dwo2Exception ex) {
            LOG.log(Level.SEVERE, "", ex);
            throw new Dwo2RestException(ex);
        }

        if (hr != null) {
            // allowed user role
            PersistentDwoProfile p = new PersistentDwoProfile();
            p.setDwoProfileDescription(profile.getDwoProfileDescription());
            p.setDwoProfileName(profile.getDwoProfileName());
            p.setDwoProfileRights(profile.getDwoProfileRights());
            p.setDwoProfileText(profile.getDwoProfileText());
            
            try {
                DwoProfileManager.create(p);
                return Boolean.TRUE;
            }
            catch (Exception e) {
                throw new Dwo2RestException(Dwo2ExceptionCode.Rest_InternalError, "An exception occured while creating school " + profile.getDwoProfileName() + ".");
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
    public Boolean updateProfile(@Context SecurityContext sc, RestDwoProfile restProfile) {
        if(restProfile==null){
            throw new Dwo2RestException(Dwo2ExceptionCode.Rest_FormatError, "Incorrect formatted REST-request.");
        }
        PersistentHasRole hr = null;
        DomDwoProfile profile = restProfile.getDomDwoProfile();
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
				PersistentDwoProfile editProfile = DwoProfileManager.findEntity(id);
                //Profile to update.
                editProfile.setDwoProfileDescription(profile.getDwoProfileDescription());
                editProfile.setDwoProfileName(profile.getDwoProfileName());
                editProfile.setDwoProfileRights(profile.getDwoProfileRights());
                editProfile.setDwoProfileText(profile.getDwoProfileText());
                DwoProfileManager.edit(editProfile);
                return Boolean.TRUE;
            }
            catch (Exception e) {
                LOG.log(Level.SEVERE, "Username " + sc.getUserPrincipal().getName() + ": Unexpected exception", e);
                throw new Dwo2RestException(Dwo2ExceptionCode.Rest_InternalError, "Failed to update profile with name " + profile.getDwoProfileName() + " .");
            }
        } else {
            LOG.log(Level.WARNING, "Username {0}: ILLEGAL USER-OPERATION: Trying to update the profile with name {1}.", new Object[]{sc.getUserPrincipal().getName(), profile.getDwoProfileName()});
            throw new Dwo2RestException(Dwo2ExceptionCode.User_IllegalAction, "You Don't Have Permission to update the profile data.");
        }
    }

}
