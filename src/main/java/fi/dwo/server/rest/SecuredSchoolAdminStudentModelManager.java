package fi.dwo.server.rest;

import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.SecurityContext;

import fi.dwo.commons.persistence.MySQLPersistenceId;
import fi.dwo.commons.persistence.entities.PersistentSchool;
import fi.dwo.commons.persistence.entities.PersistentSchoolMethod;
import fi.dwo.commons.persistence.entities.PersistentSchoolMethodPK;
import fi.dwo.commons.persistence.entities.PersistentStudentModelContext;
import fi.dwo.server.PersistentDataManagers.access.AnonDomainAuthorizer;
import fi.dwo.server.PersistentDataManagers.access.SchoolAdminTeacherDomainAuthorizer.SchoolAdminTeacherState_HR_R_S_SG_U;
import fi.dwo.server.PersistentDataManagers.access.TeacherDomainAuthorizer;
import fi.dwo.server.PersistentDataManagers.access.TeacherDomainAuthorizer.TeacherState_HR_R_S_SG_U;
import fi.dwo.server.PersistentDataManagers.access.UserDomainAuthorizer.UserState_HR_R_S_SG_U;
import fi.dwo.server.PersistentDataManagers.access.UserDomainAuthorizer.UserState_U;
import fi.dwo.server.PersistentDataManagers.core.SchoolMethodManager;
import fi.dwo.server.PersistentDataManagers.core.StudentModelContextManager;
import fi.dwo.server.PersistentDataManagers.util.StudentModelContextUtilManager;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolMethod;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContext;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContextId;
import nl.uu.fi.dwo.rest.entities.RestContext;
import nl.uu.fi.dwo.rest.entities.RestStudentModelContext;
import nl.uu.fi.dwo.rest.entities.RestStudentModelContextId;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;

@RolesAllowed({"SCHOOLADMIN"})
@Path("/secure/schooladmin/studentmodel")
public class SecuredSchoolAdminStudentModelManager {
    @PUT
    @Produces({MediaType.APPLICATION_JSON})
    @Path("/getReducedList")
    public List<DomStudentModelContext> getReducedStudentModels(@Context SecurityContext sc, RestContext context) throws Dwo2Exception {
         SchoolAdminTeacherState_HR_R_S_SG_U build = AnonDomainAuthorizer.build().submitUser(sc)
                .setHasRole(context.getRestContext().getDomHasRole())
                .buildSchoolAdminTeacher();
                
    	List<DomStudentModelContext> list = build.getReducedStudentModels();
    	return StudentModelContextUtilManager.reduce(list);
    }

    @PUT
    @Produces({MediaType.APPLICATION_JSON})
    @Path("get")
    public DomStudentModelContext getStudentModel(@Context SecurityContext sc, RestStudentModelContext rest) throws Dwo2Exception {
    	SchoolAdminTeacherState_HR_R_S_SG_U build = AnonDomainAuthorizer.build().submitUser(sc)
    			.setHasRole(rest.getRestContext().getDomHasRole())
    			.buildSchoolAdminTeacher();
    	return build.getStudentModel(rest.getDomStudentModelContext());
    }
 
// FIXME copy of teacherstudentmodelmanager.get    
    @PUT
    @Produces("application/json")
    @Path("/getMethod")
    public DomSchoolMethod getActiveMethod(@Context SecurityContext sc, RestStudentModelContextId rest) throws Dwo2Exception {
    	UserState_U ustate = AnonDomainAuthorizer.build().submitUser(sc);
		UserState_HR_R_S_SG_U hrstate = ustate.setHasRole(rest.getRestContext().getDomHasRole());
		SchoolAdminTeacherState_HR_R_S_SG_U state = hrstate.buildSchoolAdminTeacher();

		// verplaatsen naar state.getActiveMethod(dom) en dan delen met teacher
		
		DomStudentModelContextId dom = rest.getDomStudentModelContext();
		Long smID = MySQLPersistenceId.getNativeId(dom);
    	PersistentStudentModelContext context = StudentModelContextManager.findEntity(smID);
    	if (context == null) return null;
    	Long schoolID = context.getSchoolID();
    	PersistentSchool school = hrstate.getSchool();
    	if (schoolID.longValue() != 0L && !schoolID.equals(school.getSchoolID()))
    		return null; // illegal....
    	
    	PersistentSchoolMethodPK pk = new PersistentSchoolMethodPK(school.getSchoolID(), context.getModelID());
    	PersistentSchoolMethod   sm = SchoolMethodManager.findEntity(pk);
    	if (sm != null) {
    		return sm.buildDomSchoolMethod();
    	} else {
    		DomSchoolMethod dsm = new DomSchoolMethod(context.buildPersistenceId());
    		dsm.setActiveMethod(context.getModelStructure().getActiveMethod());
    		dsm.setOptLock(null); // fake, geen optlock
    		return dsm;
    	}
    }

}
