package fi.dwo.server.rest;

import javax.annotation.security.PermitAll;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.SecurityContext;

import fi.dwo.server.PersistentDataManagers.access.AnonDomainAuthorizer;
import fi.dwo.server.PersistentDataManagers.access.DwoAdminDomainAuthorizer.DwoAdminState_HR_P_R_S_SG_U;
import nl.uu.fi.dwo.rest.dom.entities.DomCourse;
import nl.uu.fi.dwo.rest.dom.entities.DomScoContext;
import nl.uu.fi.dwo.rest.dom.entities.DomScoContextFull;
import nl.uu.fi.dwo.rest.dom.entities.DomScoData;
import nl.uu.fi.dwo.rest.dom.entities.RoleType;
import nl.uu.fi.dwo.rest.entities.RestScoContext;
import nl.uu.fi.dwo.rest.entities.RestScoContextFull;
import nl.uu.fi.dwo.rest.entities.RestScoContextFull4DwoAdmin;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;

@PermitAll
@Path("/secure/dwoadmin/scoContext")
public class SecuredDwoAdminScoContextManager {
 
	@PUT
    @Path("update")
    @Produces({"application/json"})
    public DomScoContextFull update(@Context SecurityContext sc, RestScoContextFull4DwoAdmin rest) throws Dwo2Exception {

    DwoAdminState_HR_P_R_S_SG_U state = AnonDomainAuthorizer.build()
        .submitUser(sc)
        .setHasRoleIfType(rest.getRestContext().getDomHasRole(), RoleType.ADMIN)
        .buildDwoAdmin().addDwoProfile(rest.getDomDwoProfile());

      DomScoContextFull scoContext = rest.getDomScoContext();
	  DomScoData scoData = rest.getDomScoData();
	  Boolean delete = rest.getDelete();
	  
	  return state.addScoContext(scoContext).update(scoContext, scoData, delete);
    }
	
	@PUT
	@Path("add")
	@Produces({"application/json"})
	public DomScoContextFull add(@Context SecurityContext sc, RestScoContextFull rest) throws Dwo2Exception {
	    DwoAdminState_HR_P_R_S_SG_U state = AnonDomainAuthorizer.build()
	        .submitUser(sc)
	        .setHasRoleIfType(rest.getRestContext().getDomHasRole(), RoleType.ADMIN)
	        .buildDwoAdmin().addDwoProfile(rest.getDomDwoProfile());

	      DomScoContextFull scoContext = rest.getDomScoContext();
	      DomScoData scoData = rest.getDomScoData();
	      DomCourse course = new DomCourse();
	      course.setId(scoContext.getCourseId());
	      return state.addCourse(course).add(scoContext, scoData);
	}
	
	@PUT
	@Path("remove")
	@Produces({"application/json"})
	public Boolean remove(@Context SecurityContext sc, RestScoContext rest) throws Dwo2Exception {
      DwoAdminState_HR_P_R_S_SG_U state = AnonDomainAuthorizer.build()
          .submitUser(sc)
          .setHasRoleIfType(rest.getRestContext().getDomHasRole(), RoleType.ADMIN)
          .buildDwoAdmin().addDwoProfile(rest.getDomDwoProfile());

        DomScoContext scoContext = rest.getDomScoContext();
        return state.addScoContext(scoContext).removeSco();
	  
	}
	@PUT
	@Path("trash")
	@Produces({"application/json"})
	public Boolean trash(@Context SecurityContext sc, RestScoContext rest) throws Dwo2Exception {
      DwoAdminState_HR_P_R_S_SG_U state = AnonDomainAuthorizer.build()
          .submitUser(sc)
          .setHasRoleIfType(rest.getRestContext().getDomHasRole(), RoleType.ADMIN)
          .buildDwoAdmin().addDwoProfile(rest.getDomDwoProfile());

        DomScoContext scoContext = rest.getDomScoContext();
        return state.addScoContext(scoContext).trashSco();
	  
	}
}
