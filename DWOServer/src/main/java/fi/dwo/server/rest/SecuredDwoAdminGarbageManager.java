/**
 * 
 */
package fi.dwo.server.rest;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.SecurityContext;

import fi.dwo.commons.persistence.MySQLPersistenceId;
import fi.dwo.commons.persistence.entities.PersistentClassCourse;
import fi.dwo.commons.persistence.entities.PersistentCourse;
import fi.dwo.commons.persistence.entities.PersistentLoginContext;
import fi.dwo.commons.persistence.entities.PersistentSchool;
import fi.dwo.commons.persistence.entities.PersistentSchoolClass;
import fi.dwo.commons.persistence.entities.PersistentUser;
import fi.dwo.server.PersistentDataManagers.access.AnonDomainAuthorizer;
import fi.dwo.server.PersistentDataManagers.access.DwoAdminDomainAuthorizer.DwoAdminState_HR_R_S_SG_U;
import fi.dwo.server.PersistentDataManagers.cache.LoginContextCache;
import fi.dwo.server.PersistentDataManagers.core.ClassCourseManager;
import fi.dwo.server.PersistentDataManagers.core.CourseManager;
import fi.dwo.server.PersistentDataManagers.core.LoginContextManager;
import fi.dwo.server.PersistentDataManagers.core.SchoolClassManager;
import fi.dwo.server.PersistentDataManagers.core.SchoolManager;
import fi.dwo.server.PersistentDataManagers.core.UserManager;
import fi.dwo.server.PersistentDataManagers.util.UserUtilManager;
import fi.dwo.server.persistence.DwoEmfFactory;
import nl.uu.fi.dwo.rest.dom.entities.DomClassCourse;
import nl.uu.fi.dwo.rest.dom.entities.DomHasRole;
import nl.uu.fi.dwo.rest.dom.entities.DomLoginContext;
import nl.uu.fi.dwo.rest.dom.entities.DomSchool4DwoAdmin;
import nl.uu.fi.dwo.rest.dom.entities.DomUserFull;
import nl.uu.fi.dwo.rest.dom.entities.DomUserFullwLoginContext;
import nl.uu.fi.dwo.rest.dom.entities.util.DelState;
import nl.uu.fi.dwo.rest.entities.RestClassCourse;
import nl.uu.fi.dwo.rest.entities.RestLoginContext;
import nl.uu.fi.dwo.rest.entities.RestUser;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;

/**
 * @author wim
 *
 */
@Path("/secure/dwoadmin/garbage")
public class SecuredDwoAdminGarbageManager {

  @GET
  @Produces({"application/json"})
  @Path("/context/get")
  public List<DomLoginContext> getContexts(@Context SecurityContext sc, @QueryParam("limit") Integer limit) throws Dwo2Exception
  {
    DwoAdminState_HR_R_S_SG_U admin = AnonDomainAuthorizer.build()
        .submitUser(sc)
        .setDefaultHasRole().buildDwoAdmin();
    if (limit == null) limit = 100;
    EntityManager em = DwoEmfFactory.getEntityManager();
    try {
      em.getTransaction().begin();
      CriteriaBuilder builder = em.getCriteriaBuilder();
// select * from tbluser where userid not in (select userid from tbllogincontext) and lastLogin is null and registerData < '2017-01-01' limit 100;
      CriteriaQuery<PersistentLoginContext> q = builder.createQuery(PersistentLoginContext.class);
      Root<PersistentLoginContext> u = q.from(PersistentLoginContext.class);
      
      Subquery<Long> sub = q.subquery(Long.class);
      Root<PersistentUser> context = sub.from(PersistentUser.class);
      sub = sub.select(context.get("userID"));
      javax.persistence.criteria.Path<Long> userid = u.get("userID");
      Predicate in = userid.in(sub);
      Predicate and = in.not();
      q = q.select(u).where(and);
   
      TypedQuery<PersistentLoginContext> query = em.createQuery(q);
      query.setMaxResults(limit);
      List<PersistentLoginContext> list = query.getResultList();
      return list.stream().map(PersistentLoginContext::buildDomLoginContext).collect(Collectors.toList());

    } finally {
      em.close();
    }
  }
  @GET
  @Produces({"application/json"})
  @Path("/user/get") 
  public List<DomUserFullwLoginContext> getUsers(@Context SecurityContext sc, @QueryParam("before") Long before, @QueryParam("limit") Integer limit, @QueryParam("single") Boolean single) throws Dwo2Exception {
    DwoAdminState_HR_R_S_SG_U admin = AnonDomainAuthorizer.build()
        .submitUser(sc)
        .setDefaultHasRole().buildDwoAdmin();
    Date when = new Date(System.currentTimeMillis() - 3L*365*24*3600*1000); // sensible defaults
    if (before != null) when.setTime(before.longValue());
    if (limit == null) limit = 100;
    if (single == null) single = Boolean.FALSE;
    EntityManager em = DwoEmfFactory.getEntityManager();
    try {
      em.getTransaction().begin();
      CriteriaBuilder builder = em.getCriteriaBuilder();
// select * from tbluser where userid not in (select userid from tbllogincontext) and lastLogin is null and registerData < '2017-01-01' limit 100;
      CriteriaQuery<PersistentUser> q = builder.createQuery(PersistentUser.class);
      Root<PersistentUser> u = q.from(PersistentUser.class);
      
      Expression<Date> lastLogin = u.get("lastLogin");
      javax.persistence.criteria.Path<Date> registerDate = u.get("registerDate");
      javax.persistence.criteria.Path<Long> userid = u.get("userID");
      javax.persistence.criteria.Path<Boolean> singleschool = u.get("singleSchoolAccount");
      
      ParameterExpression<Date> p = builder.parameter(Date.class);
      Predicate isNull = lastLogin.isNull();
      Predicate lt = builder.lessThan(registerDate, p);
      Predicate isFalse = builder.isFalse(singleschool);
      Subquery<Long> sub = q.subquery(Long.class);
      Root<PersistentLoginContext> context = sub.from(PersistentLoginContext.class);
      sub = sub.select(context.get("userID"));
      Predicate in = userid.in(sub);
      Predicate and = builder.and(isNull, lt, in.not());
      if (!single.booleanValue())
    	  and = builder.and(and, isFalse); // exclude single school students, the default
      
      lt = builder.lessThan(lastLogin, p);
      Predicate and2 = builder.and(lt, in.not());
      if (!single.booleanValue())
    	  and2 = builder.and(and2, isFalse); // exclude single school students, the default
      
      q = q.select(u).where(builder.or(and, and2));
   
      TypedQuery<PersistentUser> query = em.createQuery(q);
      query.setParameter(p, when);
      query.setMaxResults(limit);
      List<PersistentUser> list = query.getResultList();
      if (list.size() < limit) {
        CriteriaQuery<PersistentUser> q1 = builder.createQuery(PersistentUser.class);
        Root<PersistentLoginContext> c = q1.from(PersistentLoginContext.class);
        Root<PersistentUser> user = q1.from(PersistentUser.class);
        Expression<Long> lasttimestamp = c.get("lastLoginTimeStamp");
        Expression<Long> regitimestamp = c.get("registerTimeStamp");
        Expression<Long> uid = user.get("userID");
        Expression<Long> cid = c.get("userID");
        Predicate eq = builder.equal(uid, cid);
        singleschool = user.get("singleSchoolAccount");
        isFalse = builder.isFalse(singleschool);
        ParameterExpression<Long> l = builder.parameter(Long.class);
        Predicate ltt = builder.lessThan(lasttimestamp, l);
        Predicate ltr = builder.lessThan(regitimestamp, l);
        isNull = lasttimestamp.isNull();
        ltr = builder.and(isNull, ltr);
        ltt = builder.or(ltt, ltr);
        if (single)
        	q1 = q1.select(user).where(ltt, eq); // include single school students
        else
        	q1 = q1.select(user).where(ltt,eq, isFalse);
        TypedQuery<PersistentUser> query1 = em.createQuery(q1);
        query1.setParameter(l, Long.valueOf(when.getTime()));
        query1.setMaxResults(limit-list.size());
        list = new ArrayList<>(list);
        list.addAll(query1.getResultList());
      }
      em.getTransaction().commit();
      return list.stream().map(item -> {
        DomUserFull uf = item.buildDomUserFull();
        List<PersistentLoginContext> lc = LoginContextManager.findEntities(item.getId());        
        DomUserFullwLoginContext dom = new DomUserFullwLoginContext();
        dom.setDomUserFull(uf);
        if (!lc.isEmpty())
          dom.setDomLoginContext(lc.get(0).buildDomLoginContext());
        else {
          DomLoginContext dlc = new DomLoginContext();
          dlc.setUserId(uf.getId());
          dlc.setRegisterTimeStamp(item.getRegisterDate().getTime());
          if (item.getLastLogin() != null) {
            dlc.setLastLoginTimeStamp(item.getLastLogin().getTime());
          }
          if (item.getPersistentSchoolGroup() != null)
            dlc.setSchoolGroupId(item.getPersistentSchoolGroup().buildPersistenceId());
          dom.setDomLoginContext(dlc);
        }
        return dom;
        
      }).collect(Collectors.toList());
    } finally {
      em.close();
    }
  }

  @GET
  @Produces({"application/json"})
  @Path("/classcourse/get")
  public List<DomClassCourse> getClassCourses(@Context SecurityContext sc, @QueryParam("limit") Integer limit) throws Dwo2Exception
  {
	    DwoAdminState_HR_R_S_SG_U admin = AnonDomainAuthorizer.build()
	            .submitUser(sc)
	            .setDefaultHasRole().buildDwoAdmin();
	    if (limit == null) limit = 100;
	    EntityManager em = DwoEmfFactory.getEntityManager();
	    try {
	        em.getTransaction().begin();
	        CriteriaBuilder builder = em.getCriteriaBuilder();
	  // select * from tblclasscourse where classid not in (select classid from tblschoolclass) or courseid not in (select courseid from tblcourse) limit 100;
	        CriteriaQuery<PersistentClassCourse> q = builder.createQuery(PersistentClassCourse.class);
	        Root<PersistentClassCourse> u = q.from(PersistentClassCourse.class);
	        
	        Subquery<Long> subcourse = q.subquery(Long.class);
	        Root<PersistentCourse> contextCourse = subcourse.from(PersistentCourse.class);
	        subcourse = subcourse.select(contextCourse.get("courseID"));
	        javax.persistence.criteria.Path<Long> courseid = u.get("courseID");
	        Predicate inCourse = courseid.in(subcourse);

	        Subquery<Long> subclass = q.subquery(Long.class);
	        Root<PersistentSchoolClass> contextclass = subclass.from(PersistentSchoolClass.class);
	        subclass = subclass.select(contextclass.get("classID"));
	        javax.persistence.criteria.Path<Long> classid = u.get("classID");
	        Predicate inClass = classid.in(subclass);
	        Predicate or = builder.or(inCourse.not(), inClass.not());
	        q = q.select(u).where(or);
	     
	        TypedQuery<PersistentClassCourse> query = em.createQuery(q);
	        query.setMaxResults(limit);
	        List<PersistentClassCourse> list = query.getResultList();
	        return list.stream().map(PersistentClassCourse::buildDomClassCourse).collect(Collectors.toList());

	    } finally {
	    	em.close();
	    }
  }
  
  @GET
  @Produces({"application/json"})
  @Path("/school/get")
  public List<DomSchool4DwoAdmin> getSchools(@Context SecurityContext sc, @QueryParam("limit") Integer limit) throws Dwo2Exception
  {
      DwoAdminState_HR_R_S_SG_U admin = AnonDomainAuthorizer.build()
            .submitUser(sc)
            .setDefaultHasRole().buildDwoAdmin();
      if (limit == null) limit = 10;
      List<PersistentSchool> schools = SchoolManager.findEntities();
            
      return schools.stream()
          .filter(s -> s.getDelState() != DelState.not)
          .limit(limit)
          .map(PersistentSchool::buildDomSchool4DwoAdmin)
          .collect(Collectors.toList());
  }
  
  
  
  
  @PUT
  @Produces({"application/json"})
  @Path("/user/remove") 
  public Boolean removeUser(@Context SecurityContext sc, RestUser rest) throws Dwo2Exception {
  DwoAdminState_HR_R_S_SG_U admin = AnonDomainAuthorizer.build()
      .submitUser(sc)
      .setHasRole(rest.getRestContext().getDomHasRole()).buildDwoAdmin();
    Long id = MySQLPersistenceId.getNativeId(rest.getDomUser());
    PersistentUser user = UserManager.findEntity(id);
    if (user.isSingleSchoolAccount() && rest.getDomUser().getSingleSchool().booleanValue())
      return Boolean.FALSE;
    UserUtilManager.deleteUser(user);
    return Boolean.TRUE;
  }

  @PUT
  @Produces({"application/json"})
  @Path("/context/remove") 
  public Boolean removeContext(@Context SecurityContext sc, RestLoginContext rest) throws Dwo2Exception {
  DwoAdminState_HR_R_S_SG_U admin = AnonDomainAuthorizer.build()
      .submitUser(sc)
      .setHasRole(rest.getRestContext().getDomHasRole()).buildDwoAdmin();
    Long id = MySQLPersistenceId.getNativeId(rest.getDomLoginContext());
    PersistentLoginContext context = LoginContextManager.findEntity(id);
    PersistentUser u = UserManager.findEntity(context.getUserId());
    if (u == null)
    {
    	LoginContextManager.destroy(id);
    	LoginContextCache.remove(id);
    }
    else 
      return Boolean.FALSE;
    return Boolean.TRUE;
  }
  
  @PUT
  @Produces({"application/json"})
  @Path("/classcourse/remove") 
  public Boolean removeClassCourse(@Context SecurityContext sc, RestClassCourse rest) throws Dwo2Exception {
	  DwoAdminState_HR_R_S_SG_U admin = AnonDomainAuthorizer.build()
		      .submitUser(sc)
		      .setHasRole(rest.getRestContext().getDomHasRole()).buildDwoAdmin();
	Long id = MySQLPersistenceId.getNativeId(rest.getDomClassCourse());
	PersistentClassCourse cc = ClassCourseManager.findEntity(id);
	PersistentCourse course = CourseManager.findEntity(cc.getCourseID());
	PersistentSchoolClass  schoolClass = SchoolClassManager.findEntity(cc.getClassID());
	if (schoolClass == null || course == null) {
		ClassCourseManager.destroy(id);
		return Boolean.TRUE;
	}
	return Boolean.FALSE;
  }

  // TODO select * from tblhasrole where userid not in (select userid from tbluser) (14 stuks in productie, 5 in dev)
  @GET
  @Produces({"application/json"})
  @Path("/hasrole/get")
  public List<DomHasRole> getHasRoles(@Context SecurityContext sc, @QueryParam("limit") Integer limit) throws Dwo2Exception {
	  DwoAdminState_HR_R_S_SG_U admin = AnonDomainAuthorizer.build()
	            .submitUser(sc)
	            .setDefaultHasRole().buildDwoAdmin();
	  if (limit == null) limit = 100;
	  return Collections.emptyList();
  }
  
  // TODO select * from tblcourse where schoolid not in select (schoolid from tblschool) // part of delete school.
  // TODO entries in studentof, teacherof
  // TODO entries in schoolclass (no school) // part of delete school
  // TODO entries in ACL
  // TODO entries in fromTo (export schools)
  
  
}
