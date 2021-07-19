package fi.dwo.server.rest;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.annotation.security.PermitAll;
import javax.persistence.PersistenceException;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.SecurityContext;

import fi.dwo.commons.persistence.MySQLPersistenceId;
import fi.dwo.commons.persistence.entities.PersistentAppletConfig;
import fi.dwo.commons.persistence.entities.PersistentHasRole;
import nl.uu.fi.dwo.rest.dom.entities.DomAppletConfig;
import nl.uu.fi.dwo.rest.dom.entities.RoleType;
import nl.uu.fi.dwo.rest.entities.RestAppletConfig;
import nl.uu.fi.dwo.rest.entities.RestContext;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
import nl.uu.fi.dwo.rest.exceptions.Dwo2ExceptionCode;
import nl.uu.fi.dwo.rest.exceptions.Dwo2RestException;
import fi.dwo.server.PersistentDataManagers.access.AnonDomainAuthorizer;
import fi.dwo.server.PersistentDataManagers.access.DwoAdminDomainAuthorizer.DwoAdminState_HR_R_S_SG_U;
import fi.dwo.server.PersistentDataManagers.access.UserDomainAuthorizer.UserState_HR_R_S_SG_U;
import fi.dwo.server.PersistentDataManagers.core.AppletConfigManager;
import fi.dwo.server.PersistentDataManagers.util.HasRoleUtilManager;


@PermitAll
@Path("/secure/dwoadmin/config")
public class SecuredDwoAdminConfigManager {
  private static final Logger LOG = Logger.getLogger(SecuredDwoAdminConfigManager.class.getName());

  @PUT
  @Produces({"application/json"})
  @Path("/getList")
  public List<DomAppletConfig> getConfigurations(@Context SecurityContext sc, RestContext rest) throws Dwo2Exception {
      UserState_HR_R_S_SG_U state = AnonDomainAuthorizer.build().submitUser(sc).setHasRoleIfType(rest.getRestContext().getDomHasRole(), RoleType.ADMIN);
      state.buildDwoAdmin();
  
      return AppletConfigManager.findEntities().stream()
    		  .map(PersistentAppletConfig::buildDomAppletConfig)
    		  .collect(Collectors.toList());
  }
  
  /**
   * Returns the applet configs to be displayed.
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
    } catch (Dwo2Exception ex) {
      LOG.log(Level.SEVERE, "", ex);
      throw new Dwo2RestException(ex);
    }
    if (hr != null) {
      List<PersistentAppletConfig> profiles = null;
      List<DomAppletConfig> domProfiles;
      try {
        profiles = AppletConfigManager.findEntities();
        LOG.log(Level.FINER, "Fetched all {0} profiles. ", new Object[] {profiles.size()});
        domProfiles = new ArrayList<>(profiles.size());
        for (PersistentAppletConfig p : profiles) {
          domProfiles.add(p.buildDomAppletConfig());
        }
      } catch (Exception e) {
        LOG.log(Level.WARNING, "Unexpected exception", e);
        throw new Dwo2RestException(Dwo2ExceptionCode.Rest_InternalError,
            "An exception occured while fetching the schools.");
      }
      return domProfiles;
    } else {
      LOG.log(Level.WARNING,
          "Username {0}: ILLEGAL USER-OPERATION: Trying to access dwoadmin functionality by user with usercode {0}.",
          new Object[] {sc.getUserPrincipal().getName()});
      throw new Dwo2RestException(Dwo2ExceptionCode.User_IllegalAction,
          "You Don't Have Permission to access this using usercode "
              + sc.getUserPrincipal().getName() + ".");
    }
  }

  @GET
  @Produces({"application/json"})
  @Path("/getList/{language}")
  public List<DomAppletConfig> getConfigurations(@Context SecurityContext sc,
      @PathParam("language") String language) {
    List<DomAppletConfig> list = getConfigurations(sc);
    Iterator<DomAppletConfig> it = list.iterator();
    while (it.hasNext()) {
      DomAppletConfig type = it.next();
      if (!type.getLanguage().equals(language)) it.remove();
    }
    return list;
  }
  
  @PUT
  @Produces({"application/json"})
  @Path("/getList/{language}")
  public List<DomAppletConfig> getConfigurations(@Context SecurityContext sc, RestContext rest,
      @PathParam("language") String language) throws Dwo2Exception {
    List<DomAppletConfig> list = getConfigurations(sc, rest);
    Iterator<DomAppletConfig> it = list.iterator();
    while (it.hasNext()) {
      DomAppletConfig type = it.next();
      if (!type.getLanguage().equals(language)) it.remove();
    }
    return list;
  }
  
  
  

  /**
   * Registers a new DwoProfile.
   *
   * @param sc
   * @param restConfig
   * @return
 * @throws Dwo2Exception 
   */
  @PUT
  @Produces({"application/json"})
  @Path("/submit")
  public Boolean submitAppletConfig(@Context SecurityContext sc, RestAppletConfig restConfig) throws Dwo2Exception {
    if (restConfig == null) {
      throw new Dwo2RestException(Dwo2ExceptionCode.Rest_FormatError,
          "Incorrect formatted REST-request.");
    }
    PersistentHasRole hr = null;
    UserState_HR_R_S_SG_U state = AnonDomainAuthorizer.build().submitUser(sc).setHasRoleIfType(restConfig.getRestContext().getDomHasRole(), RoleType.ADMIN);
    hr = state.getHasRole();
    state.buildDwoAdmin();
    DomAppletConfig config = restConfig.getDomAppletConfig();

    if (hr != null) {
      // allowed user role
      PersistentAppletConfig p = new PersistentAppletConfig();
      p.setAppletID(config.getAppletID());
      p.setLanguage(config.getLanguage());
      p.setLaunchdata(config.getLaunchdata());
      p.setName(config.getName());

      try {
        // 1.5.1 features
        p.setDwoProfileID(MySQLPersistenceId.getNativeId(config.getDwoProfileId()));
        p.setAppletConfigDataID(MySQLPersistenceId.getNativeId(config.getAppletConfigDataId()));
        AppletConfigManager.create(p);
        return Boolean.TRUE;
      } catch (Dwo2Exception e) {
        throw new Dwo2RestException(e);
      } catch (Exception e) {
        throw new Dwo2RestException(Dwo2ExceptionCode.Rest_InternalError,
            "An exception occured while creating appletConfig " + config.getName() + ".");
      }
    } else {
      LOG.log(Level.WARNING,
          "Username {0}: ILLEGAL USER-OPERATION: Trying to access dwoadmin functionality by user with usercode {0}.",
          new Object[] {sc.getUserPrincipal().getName()});
      throw new Dwo2RestException(Dwo2ExceptionCode.User_IllegalAction,
          "You Don't Have Permission to access this using usercode "
              + sc.getUserPrincipal().getName() + ".");
    }
  }

  /**
   * Updates the DwoProfile.
   *
   * @param sc
   * @param restConfig
   * @return
 * @throws Dwo2Exception 
   */
  @PUT
  @Produces({"application/json"})
  @Path("/update")
  public Boolean updateConfig(@Context SecurityContext sc, RestAppletConfig restConfig) throws Dwo2Exception {
    if (restConfig == null) {
      throw new Dwo2RestException(Dwo2ExceptionCode.Rest_FormatError,
          "Incorrect formatted REST-request.");
    }
    PersistentHasRole hr = null;
    UserState_HR_R_S_SG_U state = AnonDomainAuthorizer.build().submitUser(sc).setHasRoleIfType(restConfig.getRestContext().getDomHasRole(), RoleType.ADMIN);
    hr = state.getHasRole();
    state.buildDwoAdmin();
    DomAppletConfig config = restConfig.getDomAppletConfig();
    if (hr != null) {
      try {
        long id = MySQLPersistenceId.getNativeId(config);
        PersistentAppletConfig editConfig = AppletConfigManager.findEntity(id);
        // AppletConfig to update.
        editConfig.setAppletID(config.getAppletID());
        editConfig.setLanguage(config.getLanguage());
        editConfig.setLaunchdata(config.getLaunchdata());
        editConfig.setName(config.getName());
        // 1.5.1 features
        editConfig.setDwoProfileID(MySQLPersistenceId.getNativeId(config.getDwoProfileId()));
        editConfig
            .setAppletConfigDataID(MySQLPersistenceId.getNativeId(config.getAppletConfigDataId()));
        AppletConfigManager.edit(editConfig);
        return Boolean.TRUE;
      } catch (Dwo2Exception e) {
        throw new Dwo2RestException(e);
      } catch (Exception e) {
        LOG.log(Level.SEVERE,
            "Username " + sc.getUserPrincipal().getName() + ": Unexpected exception", e);
        throw new Dwo2RestException(Dwo2ExceptionCode.Rest_InternalError,
            "Failed to update profile with name " + config.getName() + " .");
      }
    } else {
      LOG.log(Level.WARNING,
          "Username {0}: ILLEGAL USER-OPERATION: Trying to update the appletconfig with name {1}.",
          new Object[] {sc.getUserPrincipal().getName(), config.getName()});
      throw new Dwo2RestException(Dwo2ExceptionCode.User_IllegalAction,
          "You Don't Have Permission to update the appletconfig data.");
    }
  }

  /**
   * remove applet config
   * @throws Dwo2Exception 
   */
  @PUT
  @Produces({"application/json"})
  @Path("/remove")
  public Boolean removeConfig(@Context SecurityContext sc, RestAppletConfig rest) throws Dwo2Exception {
    if (rest == null) {
      throw new Dwo2RestException(Dwo2ExceptionCode.Rest_FormatError,
          "Incorrect formatted REST-request.");
    }
    DwoAdminState_HR_R_S_SG_U state = AnonDomainAuthorizer.build()
        .submitUser(sc)
        .setHasRoleIfType(rest.getRestContext().getDomHasRole(), RoleType.ADMIN)
        .buildDwoAdmin();
//TODO move to Action:
      DomAppletConfig config = rest.getDomAppletConfig();
      try {
        Long id = MySQLPersistenceId.getNativeId(config);
        AppletConfigManager.destroy(id);
        return Boolean.TRUE;
      } catch (PersistenceException pe) {
        return Boolean.FALSE;
      } catch (Exception e) {
        LOG.log(Level.SEVERE,
            "Username " + sc.getUserPrincipal().getName() + ": Unexpected exception", e);
        throw new Dwo2RestException(Dwo2ExceptionCode.Rest_InternalError,
            "Failed to remove config with name " + config.getName() + " .");
      }
  }
}
