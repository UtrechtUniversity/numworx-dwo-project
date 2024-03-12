package fi.dwo.server.rest;


import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.security.Principal;
import java.util.Collections;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import javax.annotation.security.RolesAllowed;
import javax.imageio.ImageIO;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import nl.uu.fi.dwo.rest.dom.entities.DomContext;
import nl.uu.fi.dwo.rest.dom.entities.DomCourse;
import nl.uu.fi.dwo.rest.dom.entities.DomCourseStudent;
import nl.uu.fi.dwo.rest.dom.entities.DomDwoProfile;
import nl.uu.fi.dwo.rest.dom.entities.DomHasRole;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClassId;
import nl.uu.fi.dwo.rest.dom.entities.RoleType;
import nl.uu.fi.dwo.rest.dom.entities.util.ACL;
import nl.uu.fi.dwo.rest.dom.entities.util.AboType;
import nl.uu.fi.dwo.rest.dom.entities.util.ViewState;
import nl.uu.fi.dwo.rest.entities.RestCourse;
import nl.uu.fi.dwo.rest.entities.RestDwoProfile;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
import nl.uu.fi.dwo.rest.exceptions.Dwo2ExceptionCode;
import nl.uu.fi.dwo.rest.exceptions.Dwo2RestException;
import nl.uu.fi.dwo.rest.persistence.PersistenceId;
import fi.beans.dwomaccess.JSONEncoder;
import fi.beans.private_base64code.StringCodeObject;
import fi.dwo.commons.persistence.MySQLPersistenceId;
import fi.dwo.commons.persistence.entities.PersistentClassCourse;
import fi.dwo.commons.persistence.entities.PersistentCourse;
import fi.dwo.commons.persistence.entities.PersistentCourseData;
import fi.dwo.commons.persistence.entities.PersistentDwoProfile;
import fi.dwo.commons.persistence.entities.PersistentHasRole;
import fi.dwo.commons.persistence.entities.PersistentHasRolePK;
import fi.dwo.commons.persistence.entities.PersistentSchool;
import fi.dwo.commons.persistence.entities.PersistentSchoolClass;
import fi.dwo.commons.persistence.entities.PersistentSchoolGroup;
import fi.dwo.commons.persistence.entities.PersistentStudentOfClass;
import fi.dwo.commons.persistence.entities.PersistentStudentOfClassPK;
import fi.dwo.commons.persistence.entities.PersistentUser;
import fi.dwo.server.PersistentDataManagers.access.AnonDomainAuthorizer;
import fi.dwo.server.PersistentDataManagers.access.DwoAdminDomainAuthorizer.DwoAdminState_HR_R_S_SG_U;
import fi.dwo.server.PersistentDataManagers.access.SchoolAdminTeacherDomainAuthorizer.SchoolAdminTeacherState_HR_R_S_SG_U;
import fi.dwo.server.PersistentDataManagers.access.StudentDomainAuthorizer.StudentState_HR_R_S_SG_U;
import fi.dwo.server.PersistentDataManagers.access.UserDomainAuthorizer.UserState_HR_R_S_SG_U;
import fi.dwo.server.PersistentDataManagers.core.ClassCourseManager;
import fi.dwo.server.PersistentDataManagers.core.CourseDataManager;
import fi.dwo.server.PersistentDataManagers.core.CourseManager;
import fi.dwo.server.PersistentDataManagers.core.DwoProfileManager;
import fi.dwo.server.PersistentDataManagers.core.HasRoleManager;
import fi.dwo.server.PersistentDataManagers.core.SchoolClassManager;
import fi.dwo.server.PersistentDataManagers.core.StudentOfClassManager;
import fi.dwo.server.PersistentDataManagers.core.UserManager;
import fi.dwo.server.PersistentDataManagers.util.HasRoleUtilManager;
import fi.dwo.server.rest.jaxrsfilters.DwoUserPrincipal;
import fi.dwo.server.rest.util.CourseBuilder;

/**
 * Handles the public registration of new users.
 *
 * @author W.P.G. 
 */
@Path("/secure/user/course")
public class SecuredUserCourseManager {

    private static final String PUBLIC_COURSE_GET_IMAGE = "../../../public/course/getImage";
	private static final Logger LOG = Logger.getLogger(SecuredUserCourseManager.class.getName());

    /**
     * Returns the Course description of a course. This method uses MySQL-based
     * indices and should be phased out.
     * 
     * @param courseId
     * @return
     * @throws Dwo2Exception 
     */
    @PUT
    @Produces({"application/json"})
    @Path("/getCourseDescription")
    public Response getCourseDescription(@Context SecurityContext sc, RestCourse id) {
    	try {
// TODO NPE tests 		    		
    		DomDwoProfile domDwoProfile = id.getDomDwoProfile();
    		DomHasRole    hasRole = id.getRestContext().getDomHasRole();
    		PersistentUser user = getUserFromContext(sc);		

// TODO verify profile/limited
// hasRole correct	
			PersistentHasRolePK hasRoleKey = MySQLPersistenceId.getNativeId(hasRole);
            PersistentHasRole phr = HasRoleManager.findEntity(hasRoleKey);
// userid must match hasrole
     		if (user.getId().longValue() != phr.getPersistentHasRolePK().getUserID().longValue())
     		{	LOG.warning("getCourseDescription "  + id + " user getid wrong");
     			return Response.ok().entity("{}").build();
     		}
    		Long courseId = MySQLPersistenceId.getNativeId(id.getDomCourse());
            PersistentCourse course = CourseManager.findEntity(courseId);
            if(course == null) 
            {   LOG.warning("getCourseDescription "  + id + " course null");
            	return Response.ok().entity("{}").build(); // Not fatal
            }
            Date modified = new Date(course.getLastChangeTimeStamp());
            if(! course.getDwoProfileID().equals(MySQLPersistenceId.getNativeId(domDwoProfile)))
            {	LOG.warning("getCourseDescription "  + id + " profileid wrong");
            	return Response.ok().entity("{}").build();
            }
            if (course.getSchoolID() != null) {
            	RoleType role = RoleType.values()[phr.getSchoolGroup().getGroupID()];
            	DomSchoolClassId schoolClassId;
				switch (role) {
            	case STUDENT: // schoolclass test
            		schoolClassId = id.getSchoolClassID();
        			Long cid = MySQLPersistenceId.getNativeId(schoolClassId);
        			PersistentSchoolClass schoolClass = SchoolClassManager.findEntity(cid);
        			List<PersistentClassCourse> pcc = ClassCourseManager.findEntities(schoolClass, course);
        			PersistentStudentOfClassPK socId = new PersistentStudentOfClassPK(user.getId(), cid, phr.getSchoolGroup().getSchoolGroupID());
    				PersistentStudentOfClass soc = StudentOfClassManager.findEntity(socId);
        			if(pcc.isEmpty() || soc == null) // FIXME ook bij toets hier!
        				return Response.ok().entity("{}").build();
        			PersistentClassCourse pcc1 = pcc.get(0);
        			if( pcc1.getViewState() != ViewState.studentsAndTeachers) {
        				LOG.warning("getCourseDescription "  + id + " viewstate wrong");
        				return Response.ok().entity("{}").build();
        			}
        			java.util.Date now = new java.util.Date();
        			if (pcc1.getNotAfter() != null && now.after(pcc1.getNotAfter()))
        			{	LOG.warning("getCourseDescription "  + id + " not after wrong");
        				return Response.ok().entity("{}").build();
        			}
        			if (pcc1.getNotBefore() != null && now.before(pcc1.getNotBefore()))
        			{	LOG.warning("getCourseDescription "  + id + " not before wrong");
        				return Response.ok().entity("{}").build();
        			}
            		break;
            	case TEACHER: // ACL test only if accesscontrol.
            		if (phr.getSchoolGroup().getSchool().accessControl()) {
	            		ACL acl = SecuredCommonScoDataManager.getACL(phr, course);
	            		if (acl == ACL.NONE) {
	            			LOG.warning("getCourseDescription "  + id + " ACL NONE");
	            			return Response.ok().entity("{}").build();         	
	            		}
            		}
            	default:
            	}
            }
	        CacheControl cc = new CacheControl();
	        cc.setMaxAge(3600);
	        Date expires = new Date(System.currentTimeMillis() + cc.getMaxAge()*1000L);
            PersistentCourseData data = CourseDataManager.findEntity(course.getCourseID());
            if (data != null) {
            	byte[] bytes = data.getDescriptionbytes();
        		modified = new Date(data.getLastChangeTimeStamp());
            	if (bytes != null) {
            		ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
	            	GZIPInputStream gis = new GZIPInputStream(bis);
	            	return Response.ok().encoding("UTF-8").entity(gis)
	            		.lastModified(modified)
	            		.expires(expires)
	            		.cacheControl(cc)
            			.build();
            	} else {
            		course.setDescription(data.getDescription());
            	}
            }

            Hashtable map = (Hashtable) StringCodeObject.decodeStringToObject(course.getDescription(), null); // FIXM load wiskopdr.jar
            StringWriter writer = new StringWriter();
			JSONEncoder.encode(map, writer, null); // FIXME, load wiskopdr.jar
	        String string = writer.toString();
	        if (data != null) {
	        	ByteArrayOutputStream bos = new ByteArrayOutputStream(string.length());
	        	GZIPOutputStream gos = new GZIPOutputStream(bos);
	        	OutputStreamWriter w = new OutputStreamWriter(gos, StandardCharsets.UTF_8);
	        	w.write(string);
	        	w.close();
	        	data.setDescriptionbytes(bos.toByteArray());
	        	data = CourseDataManager.edit(data);
	        }
			return Response.ok().entity(string).cacheControl(cc).expires(expires)
					.lastModified(modified)
					.build();
		} catch (Exception e) {
			LOG.log(Level.SEVERE,"getCourseDescription "  + id , e);
			return Response.ok().entity("{}").build();
		}
    }
     
    @GET
    @Produces({"application/json"})
    @Path("/getCourseDescription")
    public Response getCourseDescription(@Context SecurityContext sc, @QueryParam("courseId") Long courseId, @QueryParam("profile") Long profile, @QueryParam("classId") Long clsId) {
    	RestCourse rest = new RestCourse();
    	DomCourse course = new DomCourse(PersistentCourse.buildPersistenceId(courseId));
    	rest.setDomCourse(course);
    	DomDwoProfile dwoProfile = new DomDwoProfile(); dwoProfile.setId(PersistentDwoProfile.buildPersistenceId(profile));
    	rest.setDomDwoProfile(dwoProfile);
    	DomSchoolClassId schoolclassId= new DomSchoolClassId(PersistentSchoolClass.buildPersistenceId(cls));
    	rest.setSchoolClassID(schoolclassId);
    	
    	DomContext context = new DomContext();
    	DwoUserPrincipal user = (DwoUserPrincipal) sc.getUserPrincipal();
    	context.setDomHasRole(user.getHr().buildDomHasRole());
    	rest.setRestContext(context);
    	
    	return getCourseDescription(sc, rest);
    	
    }
    
    @PUT
    @Path("/getSchool")
    @Produces({"application/json"})
    public List<DomCourseStudent> getCoursesSchool(@Context SecurityContext sc, RestDwoProfile rest, @Context UriInfo info) throws Dwo2Exception {
// TODO NPE tests 		    		
		DomDwoProfile domDwoProfile = rest.getDomDwoProfile();
		DomHasRole    hasRole = rest.getRestContext().getDomHasRole();
		PersistentUser user = getUserFromContext(sc);		
		Long id = MySQLPersistenceId.getNativeId(domDwoProfile);
		PersistentDwoProfile profile = DwoProfileManager.findEntity(id);
        PersistentHasRolePK hasRoleKey = MySQLPersistenceId.getNativeId(hasRole);        
		PersistentHasRole hr = HasRoleManager.findEntity(hasRoleKey);
// FIXME check role is not a guest/student
		
		PersistentSchool school = HasRoleUtilManager.getSchoolforHasRole(hr);
		List<PersistentCourse> courses = CourseManager.findChildrenOf(profile, school);		
		Stream<PersistentCourse> stream = courses.stream();
		String pfx = info.getRequestUri().resolve(PUBLIC_COURSE_GET_IMAGE).toString();
		Stream<DomCourseStudent> map = stream.map(new CourseBuilder(pfx, hasRole,school.accessControl()));
		map = map.sorted(DomCourseStudentComparator.INSTANCE);
		return map.collect(Collectors.toList());
    }
    
    @PUT
    @Path("/getTrashedSchool")
    @Produces({MediaType.APPLICATION_JSON})
    public List<DomCourseStudent> getTrashedCourse(@Context SecurityContext sc, RestDwoProfile rest, @Context UriInfo info) throws Dwo2Exception {
    	UserState_HR_R_S_SG_U state = AnonDomainAuthorizer.build().submitUser(sc).setHasRole(rest.getRestContext().getDomHasRole());
    	PersistentDwoProfile profile = DwoProfileManager.findEntity(MySQLPersistenceId.getNativeId(rest.getDomDwoProfile()));
    	List<PersistentCourse> list = CourseManager.findTrashedChildrenOf(profile, state.getSchool());
    	return list.stream().map(course -> {
    		DomCourseStudent st = course.buildDomCourseStudent();
    		st.setSequenceNr(course.getTrashID());
    		return st;
    	}).sorted(DomCourseStudentComparator.INSTANCE).
    	collect(Collectors.toList());
    }

    @PUT
    @Path("/getTrashedRoot")
    @Produces({MediaType.APPLICATION_JSON})
    public List<DomCourseStudent> getTrashedRoot(@Context SecurityContext sc, RestDwoProfile rest, @Context UriInfo info) throws Dwo2Exception {
    	UserState_HR_R_S_SG_U state = AnonDomainAuthorizer.build().submitUser(sc).setHasRole(rest.getRestContext().getDomHasRole());
    	PersistentDwoProfile profile = DwoProfileManager.findEntity(MySQLPersistenceId.getNativeId(rest.getDomDwoProfile()));
    	PersistentSchool NULL = new PersistentSchool();
		List<PersistentCourse> list = CourseManager.findTrashedChildrenOf(profile, NULL);
    	return list.stream().map(course -> {
    		DomCourseStudent st = course.buildDomCourseStudent();
    		st.setSequenceNr(course.getTrashID());
    		return st;
    	}).sorted(DomCourseStudentComparator.INSTANCE).
    	collect(Collectors.toList());
    }
    
    @PUT
    @Path("/getAll")
    @Produces("application/json")
    public List<DomCourse> getAllCourses(@Context SecurityContext sc, RestDwoProfile rest) throws Dwo2Exception {
    	UserState_HR_R_S_SG_U state = AnonDomainAuthorizer.build().submitUser(sc).setHasRole(rest.getRestContext().getDomHasRole());
        Long profileID = MySQLPersistenceId.getNativeId(rest.getDomDwoProfile());
    	PersistentSchoolGroup group = state.getSchoolGroup();
    	//RoleType type = RoleType.values()[group.getGroupID()];
 // All public visible courses for a student
    	if (RoleType.STUDENT.ordinal() == group.getGroupID()) 
    	{
    		return CourseManager.findVisibleEntities(profileID).stream().map(PersistentCourse::buildDomCourse).collect(Collectors.toList());
    	}
    	PersistentSchool school = state.getSchool();
    	boolean premium = school.getAboType() == AboType.premium;
    	
        PersistentDwoProfile profile = DwoProfileManager.findEntity(profileID);
        if ( profile.isLimited())
        {
            // check schools
        }       
        List<PersistentCourse> list;
        list = CourseManager.findEntities(profileID, school.getSchoolID()); // invisible entities ook
        if (premium) {
        	if (RoleType.TEACHER.ordinal() == group.getGroupID() && school.accessControl()) {
        		LOG.fine("iets met ACL");
        	}
        } else {
        	list = list.stream().filter(PublicCourseManager::visible).collect(Collectors.toList());
        	list = CourseManager.reduceVisibility(list);
        }
        
        return list.stream().map(PersistentCourse::buildDomCourse).collect(Collectors.toList());
    }
    
    
    @PUT
    @Path("/getRoot")
    @Produces({"application/json"})
    public List<DomCourseStudent> getCourses(@Context SecurityContext sc, RestDwoProfile rest, @Context UriInfo info) 
    {
    	try {
   // TODO NPE tests 		    		
    		DomDwoProfile domDwoProfile = rest.getDomDwoProfile();
    		DomHasRole    hasRole = rest.getRestContext().getDomHasRole();
    		PersistentUser user = getUserFromContext(sc);
			PersistentHasRolePK hasRoleKey = MySQLPersistenceId.getNativeId(hasRole);
            PersistentHasRole phr = HasRoleManager.findEntity(hasRoleKey);
            boolean premium = phr.getSchoolGroup().getSchool().getAboType() == AboType.premium;
// userid must match hasrole
         		if (user.getId().longValue() != phr.getPersistentHasRolePK().getUserID().longValue())
         			return Collections.emptyList();

// Security, only non limited profiles are public 		
    		long id = MySQLPersistenceId.getNativeId(domDwoProfile);
    		PersistentDwoProfile profile = DwoProfileManager.findEntity(id);
    		if ( profile.isLimited())
    		{
    			PersistentHasRole hr = phr;
    			PersistentSchool limited = HasRoleUtilManager.getSchoolforHasRole(hr);
// SECURITY
    			// if ! limitedschools .contains (limited) return EMPTY_LIST;  			
    		}
    		
    		PersistentSchool school = new PersistentSchool(null);
    		List<PersistentCourse> courses = CourseManager.findChildrenOf(profile, school);
    		String pfx = info.getRequestUri().resolve(PUBLIC_COURSE_GET_IMAGE).toString();
    		Stream<PersistentCourse> stream = courses.stream();
    		if (!premium)
    		  stream = stream.filter(PublicCourseManager::visible);
    		Stream<DomCourseStudent> map = stream.map(new CourseBuilder(pfx,hasRole,false));
			map = map.sorted(DomCourseStudentComparator.INSTANCE);
    		return map.collect(Collectors.toList());
    	} catch (Dwo2RestException e) {
    		throw e;
    	} catch (Exception e) {
    		LOG.log(Level.SEVERE, "getCourses", e);
    		throw new Dwo2RestException(Dwo2ExceptionCode.Rest_InternalError, "An exception occured while fetching the modules.");	
    	}
    }
    
    @PUT
    @Path("/getChildren")
    @Produces({"application/json"})
    public List<DomCourseStudent> getCourses(@Context SecurityContext sc, RestCourse rest, @Context UriInfo info) {
    	try {
    		DomCourse course = rest.getDomCourse();
    		DomHasRole hasRole = rest.getRestContext().getDomHasRole();
    		DomDwoProfile domDwoProfile = rest.getDomDwoProfile();
    		Long id = MySQLPersistenceId.getNativeId(course);
// Context
            PersistentUser user = getUserFromContext(sc);		
            PersistentHasRolePK hasRoleKey = MySQLPersistenceId.getNativeId(hasRole);        
    		PersistentHasRole hr = HasRoleManager.findEntity(hasRoleKey);
// userid must match hasrole
         		if (! user.getId().equals( hr.getPersistentHasRolePK().getUserID()))
         			return Collections.emptyList();
// FIXME check role is not a guest/student
    		
    		PersistentSchool school = HasRoleUtilManager.getSchoolforHasRole(hr);
    		boolean premium = school.getAboType() == AboType.premium;
    		
    		PersistentCourse parent = CourseManager.findEntity(id);
// Verify parent is public and profile is not limited and hasChildren, 
// or that school of hasRole == school of course (no pun)
    		PersistentDwoProfile profile = DwoProfileManager.findEntity(parent.getDwoProfileID());
    		if ( parent.getSchoolID() != null || 
    			 ! parent.isWithChildren()	||
    			 profile.isLimited()
// Verify context: profile matches...
    			|| !profile.getDwoProfileID().equals(MySQLPersistenceId.getNativeId(domDwoProfile))
// standaard school: visible
    			|| (!premium && !PublicCourseManager.visible(parent))
    		    )
    		{
    			if(!school.getSchoolID().equals(parent.getSchoolID()))
    			{
    				//return Collections.emptyList();
    			}
    		}
    			
    		List<PersistentCourse> courses = CourseManager.findChildrenOf(parent);
    		String pfx = info.getRequestUri().resolve(PUBLIC_COURSE_GET_IMAGE).toString();
    		Stream<PersistentCourse> stream = courses.stream();
    		if (!premium)
    		  stream = stream.filter(PublicCourseManager::visible);
            return stream
    				.map( new CourseBuilder(pfx, hasRole, school.accessControl()) )
    				.sorted(DomCourseStudentComparator.INSTANCE)
    				.collect(Collectors.toList());
    	} catch (Dwo2RestException e) {
    		throw e;
    	} catch (Exception e) {
    		LOG.log(Level.WARNING, "getCourses", e);
    		throw new Dwo2RestException(Dwo2ExceptionCode.Rest_InternalError, "An exception occured while fetching the modules.");	
    	}    	
    }

	private static PersistentUser getUserFromContext(SecurityContext sc) {
		PersistentUser user = null;
		try {
			Principal principal = sc.getUserPrincipal();
			if (principal instanceof DwoUserPrincipal)
				return ((DwoUserPrincipal) principal).getUser();
			
		    user = UserManager.findByUserName(sc.getUserPrincipal().getName());
		    LOG.log(Level.FINE, "Username {0}: Fetched User with username {1}", new Object[]{sc.getUserPrincipal().getName(), user.getUsername()});
		}
		catch (Exception e) {
		    LOG.log(Level.SEVERE, "Username " + sc.getUserPrincipal().getName() + ": Unexpected exception", e);
		    throw new Dwo2RestException(Dwo2ExceptionCode.Rest_InternalError, "Failed to query user id " + sc.getUserPrincipal().getName() + " .");
		}
		return user;
	}
    
    @PUT
    @Path("/get")
    @Produces({"application/json"})
    public DomCourseStudent getCourse(@Context SecurityContext sc, RestCourse rest, @Context UriInfo info) {
    	try {
    		UserState_HR_R_S_SG_U ustate = AnonDomainAuthorizer.build().submitUser(sc).setHasRole(rest.getRestContext().getDomHasRole());
    		
// TODO NPE tests 		    		
    		DomDwoProfile domDwoProfile = rest.getDomDwoProfile();
    		DomSchoolClassId schoolClassId = rest.getSchoolClassID();
    		DomHasRole    hasRole = rest.getRestContext().getDomHasRole();
    		PersistentUser user = ustate.getUser();		
    		PersistentHasRole phr = ustate.getHasRole();
         // userid must match hasrole
     		if (user.getId().longValue() != phr.getPersistentHasRolePK().getUserID().longValue())
     			throwLoginNeeded();
     		DomCourse course = rest.getDomCourse();
    		Long id = MySQLPersistenceId.getNativeId(course);
    		PersistentCourse parent = CourseManager.findEntity(id);
    		PersistentDwoProfile profile = null;
    		if (parent != null) profile = DwoProfileManager.findEntity(parent.getDwoProfileID());
    		if (parent == null ||
    			!profile.getDwoProfileID().equals(MySQLPersistenceId.getNativeId(domDwoProfile))
    		   ) {
    			throw new Dwo2RestException(Dwo2ExceptionCode.Rest_ResourceNotFound, id + " not found for " + domDwoProfile.getId());
    		}
    		
    		
    		if(schoolClassId != null) {
    			StudentState_HR_R_S_SG_U sstate = ustate.buildStudent().setSchoolClass(schoolClassId);
    			PersistentSchoolClass schoolClass;
    			schoolClass = sstate.getContext().getStudentCtx().schoolClass;
    			//id = schoolClass.getClassID();
    			List<PersistentClassCourse> pcc = ClassCourseManager.findEntities(schoolClass, parent);
    			if(pcc.isEmpty() ) 
    				throwLoginNeeded();
    			PersistentClassCourse pcc1 = pcc.get(0);
    			if(pcc1.getType().intValue() == 1 || pcc1.getViewState() != ViewState.studentsAndTeachers) {
    				throwLoginNeeded();
    			}
    			java.util.Date now = new java.util.Date();
    			if (pcc1.getNotAfter() != null && now.after(pcc1.getNotAfter()))
    					throwLoginNeeded();
    			if (pcc1.getNotBefore() != null && now.before(pcc1.getNotBefore()))
    					throwLoginNeeded();
    			
    		} else {
    			if (parent.getSchoolID() != null) {
    				RoleType role = ustate.getRoleType();
    				if (parent.getSchoolID().intValue() != phr.getSchoolGroup().getSchoolID())
    					throwLoginNeeded();
    				switch(role) {
    				case TEACHER:
    					ACL acl = SecuredCommonScoDataManager.getACL(phr, parent);
    					if (acl != ACL.NONE && acl != ACL.ACCESS) break;
    				case STUDENT:
    					throwLoginNeeded();
    				default:
    				}    				
    			}
    		}
// TODO Verify parent is public and profile is not limited OR user.school matches course.school
//    		if ( parent.getSchoolID() != null || 
//    			 profile.isLimited())
//    			return null;

// Verify context: profile matches...  		
    		//if (profile.getDwoProfileID().equals(MySQLPersistenceId.getNativeId(domDwoProfile)))
    		String pfx = info.getRequestUri().resolve(PUBLIC_COURSE_GET_IMAGE).toString();
    		CourseBuilder builder = new CourseBuilder(pfx, hasRole, false); // FIXME school.accessControl() gebruiken?
    		return builder.apply(parent);
    	} catch (Dwo2RestException e) {
    		throw e;
    	} catch (Exception e) {
    		LOG.log(Level.WARNING, "getCourse", e);
    		throw new Dwo2RestException(Dwo2ExceptionCode.Rest_InternalError, "An exception occured while fetching the module.");	
    	}
    	//return null;
    }
 
    private void throwLoginNeeded() {
		throw new Dwo2RestException(Dwo2ExceptionCode.Rest_LoginNeeded, "Not allowed");
	}



	@GET
    @Path("/getImage")
    @Produces({"image/png"})
    public Response getImage(@Context SecurityContext sc, @QueryParam("courseId") Long courseId, @QueryParam("hasRoleId") String hasRoleId) {
    	try {
    		PersistentCourse course = CourseManager.findEntity(courseId);
        	//PersistentDwoProfile profile = DwoProfileManager.findEntity(course.getDwoProfileID());
// TODO Security
        	PersistenceId pid = new PersistenceId();
        	pid.setIdString(hasRoleId);
        	DomHasRole hasRole = new DomHasRole();
        	hasRole.setId(pid);
			PersistentHasRolePK hasRoleKey = MySQLPersistenceId.getNativeId(hasRole);
			PersistentHasRole hr = HasRoleManager.findEntity(hasRoleKey);
			PersistentUser user = getUserFromContext(sc);
// user.id == hr.getuserid
// schoolid = course.schoolid or course.schoolid = null
// profileRights != limited
    		Date last = new Date(course.getLastChangeTimeStamp());

    		byte[] imageData = course.getImageData();
    		if (imageData == null) {
    			PersistentCourseData cd = CourseDataManager.findEntity(courseId);
    			if (cd != null) {
    				imageData = cd.getImageData();
    				last = new Date(cd.getLastChangeTimeStamp());
    			}
    		}
    		BufferedImage image = ImageIO.read(new ByteArrayInputStream(imageData));
    		ByteArrayOutputStream out = new ByteArrayOutputStream();
    		ImageIO.write(image, "png", out);
    		imageData = out.toByteArray();
    		CacheControl cc = new CacheControl(); cc.setMaxAge(600);
			return Response.ok(imageData, "image/png")
    				.lastModified(last)
    				.cacheControl(cc)
    				.expires(new Date(System.currentTimeMillis()+cc.getMaxAge()*1000))
    				.build();    		
    	} catch(Exception e) {
    		LOG.log(Level.SEVERE, "getImage error", e);
    	}
    	return Response.status(Status.NOT_FOUND).build();
    }    
    @PUT
    @Path("/getTrashedChildren")
    @Produces({"application/json"})
    public List<DomCourseStudent> getTrashedCourses(@Context SecurityContext sc, RestCourse rest, @Context UriInfo info) throws Dwo2Exception {
    	UserState_HR_R_S_SG_U hasRole = AnonDomainAuthorizer.build().submitUser(sc).setHasRole(rest.getRestContext().getDomHasRole());
    	if (hasRole.getRoleType() != RoleType.ADMIN) {
    		SchoolAdminTeacherState_HR_R_S_SG_U state = hasRole.buildSchoolAdminTeacher();
    	} else {
    		DwoAdminState_HR_R_S_SG_U state = hasRole.buildDwoAdmin();
    		state.addDwoProfile(rest.getDomDwoProfile()).addCourse(rest.getDomCourse());
    	}
    	Long courseId = MySQLPersistenceId.getNativeId(rest.getDomCourse());
    	PersistentCourse c = CourseManager.findEntity(courseId);
// TODO Security: profile match, school match, ACL?
    	List<PersistentCourse> list = CourseManager.findTrashedChildrenOf(c);
    	return list.stream()
    		.map(course -> {
    			DomCourseStudent st = course.buildDomCourseStudent();
    			st.setSequenceNr(course.getTrashID());
    			return st;
    		})
			.sorted(DomCourseStudentComparator.INSTANCE)
			.collect(Collectors.toList());
    }
}
