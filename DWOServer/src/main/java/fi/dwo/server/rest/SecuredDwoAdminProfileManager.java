package fi.dwo.server.rest;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.annotation.security.PermitAll;
import javax.persistence.OptimisticLockException;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.SecurityContext;


import fi.dwo.commons.persistence.MySQLPersistenceId;
import fi.dwo.commons.persistence.entities.PersistentDwoProfile;
import fi.dwo.commons.persistence.entities.PersistentHasRole;
import nl.uu.fi.dwo.lms.jclient.lib.rest.cache.PublicProfileCache;
import nl.uu.fi.dwo.rest.dom.entities.DomDwoProfileFull;
import nl.uu.fi.dwo.rest.dom.entities.RoleType;
import nl.uu.fi.dwo.rest.entities.RestContext;
import nl.uu.fi.dwo.rest.entities.RestDwoProfileFull;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
import nl.uu.fi.dwo.rest.exceptions.Dwo2ExceptionCode;
import nl.uu.fi.dwo.rest.exceptions.Dwo2RestException;
import fi.dwo.server.PersistentDataManagers.access.AnonDomainAuthorizer;
import fi.dwo.server.PersistentDataManagers.access.UserDomainAuthorizer.UserState_HR_R_S_SG_U;
import fi.dwo.server.PersistentDataManagers.core.DwoProfileManager;
import fi.dwo.server.PersistentDataManagers.util.HasRoleUtilManager;


@PermitAll
@Path("/secure/dwoadmin/profile")
public class SecuredDwoAdminProfileManager {
    private static final Logger LOG = Logger.getLogger(SecuredDwoAdminProfileManager.class.getName());

    
    @PUT
    @Produces({"application/json"})
    @Path("/getList")
    public List<DomDwoProfileFull> getProfiles(@Context SecurityContext sc, RestContext rest) throws Dwo2Exception {
        UserState_HR_R_S_SG_U state = AnonDomainAuthorizer.build().submitUser(sc).setHasRoleIfType(rest.getRestContext().getDomHasRole(), RoleType.ADMIN);
        state.buildDwoAdmin();
        return DwoProfileManager.findEntities().stream()
        		.map(PersistentDwoProfile::buildDomDwoProfileFull)
        		.collect(Collectors.toList());
    }
    
    /**
     * Registers a new DwoProfile.
     *
     * @param sc
     * @param restDwoProfile
     * @return
     * @throws Dwo2Exception 
     */
    @PUT
    @Produces({"application/json"})
    @Path("/submit")
    public Boolean submitProfile(@Context SecurityContext sc, RestDwoProfileFull restDwoProfile) throws Dwo2Exception {
        if(restDwoProfile==null){
            throw new Dwo2RestException(Dwo2ExceptionCode.Rest_FormatError, "Incorrect formatted REST-request.");
        }
        PersistentHasRole hr = null;
        UserState_HR_R_S_SG_U state = AnonDomainAuthorizer.build().submitUser(sc).setHasRoleIfType(restDwoProfile.getRestContext().getDomHasRole(), RoleType.ADMIN);
        hr = state.getHasRole();
        state.buildDwoAdmin();
        DomDwoProfileFull profile = restDwoProfile.getDomDwoProfile() ;

        if (hr != null) {
            // allowed user role
            PersistentDwoProfile p = new PersistentDwoProfile();
            p.setDwoProfileDescription(profile.getDwoProfileDescription());
            p.setDwoProfileName(profile.getDwoProfileName());
            p.setDwoProfileRights(profile.getDwoProfileRights());
            p.setDwoProfileText(profile.getDwoProfileText());
            String base = profile.getBase();
         // base is /[a-z/]+
            if (base != null && base.startsWith("/") && base.length() > 2) {
            	p.setBase(base);
            }
            if (profile.getLanguage() != null) {
            	p.setLanguage(profile.getLanguage());
            }
            if (profile.getTitle() != null) {
            	p.setTitle(profile.getTitle());
            }
           
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
     * @throws Dwo2Exception 
     */
    @PUT
    @Produces({"application/json"})
    @Path("/update")
    public Boolean updateProfile(@Context SecurityContext sc, RestDwoProfileFull restProfile) throws Dwo2Exception {
        if(restProfile==null){
            throw new Dwo2RestException(Dwo2ExceptionCode.Rest_FormatError, "Incorrect formatted REST-request.");
        }
        PersistentHasRole hr = null;
        UserState_HR_R_S_SG_U state = AnonDomainAuthorizer.build().submitUser(sc).setHasRoleIfType(restProfile.getRestContext().getDomHasRole(), RoleType.ADMIN);
        hr = state.getHasRole();
        state.buildDwoAdmin();
        DomDwoProfileFull profile = restProfile.getDomDwoProfile();
        if (hr != null) {
            try {
                Long id = MySQLPersistenceId.getNativeId(profile);
				PersistentDwoProfile editProfile = DwoProfileManager.findEntity(id);
                //Profile to update.
                editProfile.setDwoProfileDescription(profile.getDwoProfileDescription());
                // als de namen verschillen remove that entry from cache
                if (! editProfile.getDwoProfileName().equals(profile.getDwoProfileName()))
                	PublicProfileCache.clear();
                editProfile.setDwoProfileName(profile.getDwoProfileName());
                editProfile.setDwoProfileRights(profile.getDwoProfileRights());
                editProfile.setDwoProfileText(profile.getDwoProfileText());
                String base = profile.getBase();
// base is /[a-z/]+
                if (base != null && base.startsWith("/")) {
                	if ("/".equals(base)) base = null;
                	editProfile.setBase(base);
                }
                if (profile.getLanguage() != null) {
                	editProfile.setLanguage(profile.getLanguage());
                }
                if (profile.getTitle() != null) {
                	editProfile.setTitle(profile.getTitle());
                }
                if (profile.getOptLock() != null) 
                	editProfile.setOptlock(profile.getOptLock());
                editProfile = DwoProfileManager.edit(editProfile);
                DomDwoProfileFull cache = editProfile.buildDomDwoProfileFull();
				PublicProfileCache.putInCache(
                		editProfile.getDwoProfileName(),
                		cache);
                PublicProfileCache.putInCache(
                		Long.toString(id),
                		cache);
                base = editProfile.getBase();
                if (base != null && base.startsWith("/") && base.length() >2 )
                	PublicProfileCache.putInCache(base, cache);
               return Boolean.TRUE;
            }
            catch (OptimisticLockException e) {
                PublicProfileCache.clear();
            	throw new Dwo2RestException(Dwo2ExceptionCode.Rest_ObjectModified, e.getLocalizedMessage());
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
