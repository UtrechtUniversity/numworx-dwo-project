/**
 * 
 */
package fi.dwo.server.rest;

import java.sql.Date;
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
import fi.dwo.commons.persistence.entities.PersistentLoginContext;
import fi.dwo.commons.persistence.entities.PersistentUser;
import fi.dwo.server.PersistentDataManagers.access.AnonDomainAuthorizer;
import fi.dwo.server.PersistentDataManagers.access.DwoAdminDomainAuthorizer.DwoAdminState_HR_R_S_SG_U;
import fi.dwo.server.PersistentDataManagers.core.LoginContextManager;
import fi.dwo.server.PersistentDataManagers.core.UserManager;
import fi.dwo.server.PersistentDataManagers.util.UserUtilManager;
import fi.dwo.server.persistence.DwoEmfFactory;
import nl.uu.fi.dwo.rest.dom.entities.DomLoginContext;
import nl.uu.fi.dwo.rest.dom.entities.DomUserFull;
import nl.uu.fi.dwo.rest.dom.entities.DomUserFullwLoginContext;
import nl.uu.fi.dwo.rest.entities.RestUser;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;

/**
 * @author wim
 *
 */
@Path("/garbage")
public class GarbageManager {

  @GET
  @Produces({"application/json"})
  @Path("/user/get") 
  List<DomUserFullwLoginContext> getUsers(@Context SecurityContext sc, @QueryParam("before") Long before, @QueryParam("limit") Integer limit) {
    Date when = new Date(System.currentTimeMillis() - 3L*365*24*3600*1000); // sensible defaults
    if (before != null) when.setTime(before.longValue());
    if (limit == null) limit = 100;
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
      Predicate and = builder.and(isNull, lt, isFalse, in.not());
      q = q.select(u).where(and);
   
      TypedQuery<PersistentUser> query = em.createQuery(q);
      query.setParameter(p, when);
      query.setMaxResults(limit);
      List<PersistentUser> list = query.getResultList();
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

  @PUT
  @Produces({"application/json"})
  @Path("/user/remove") 
  Boolean removeUser(@Context SecurityContext sc, RestUser rest) throws Dwo2Exception {
  DwoAdminState_HR_R_S_SG_U admin = AnonDomainAuthorizer.build()
      .submitUser(sc.getUserPrincipal().getName())
      .setHasRole(rest.getRestContext().getDomHasRole()).buildDwoAdmin();
    Long id = MySQLPersistenceId.getNativeId(rest.getDomUser());
    PersistentUser user = UserManager.findEntity(id);
    if (user.isSingleSchoolAccount())
      return Boolean.FALSE;
    UserUtilManager.deleteUser(user);
    return Boolean.TRUE;
  }
  
  
}
