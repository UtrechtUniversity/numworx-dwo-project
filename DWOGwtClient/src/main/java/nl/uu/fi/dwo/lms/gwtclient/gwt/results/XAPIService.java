package nl.uu.fi.dwo.lms.gwtclient.gwt.results;

import javax.inject.Inject;

import org.fusesource.restygwt.client.Defaults;
import org.osgi.util.promise.Promise;
import org.osgi.util.promise.Promises;

import fi.dwo.gwt.lib.rest.CallManagers.SecuredTeacherStudentModelManager;
import fi.dwo.gwt.lib.rest.CallManagers.XapiManager;
import nl.uu.fi.dwo.lms.gwtclient.gwt.DwoGlobalVars;
import nl.uu.fi.dwo.lms.gwtclient.gwt.dagger.RoleScope;
import nl.uu.fi.dwo.rest.dom.entities.DomContext;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomScoContextId;
import nl.uu.fi.dwo.rest.dom.xapi.Account;
import nl.uu.fi.dwo.rest.dom.xapi.Agent;
import nl.uu.fi.dwo.rest.dom.xapi.Group;
import nl.uu.fi.dwo.rest.dom.xapi.Statement;
import nl.uu.fi.dwo.rest.dom.xapi.StatementsQuery;
import nl.uu.fi.dwo.rest.dom.xapi.StatementsResult;

@RoleScope
class XAPIService {
  
  public static final String COMPLETED = "http://adlnet.gov/expapi/verbs/completed";

private Promise<XapiManager> man;

  private DomContext context;

  @Inject XAPIService(SecuredTeacherStudentModelManager manager, DwoGlobalVars vars) {
    context = new DomContext();
    context.setDomHasRole(vars.getSchoolLogins().getActiveSchoolRoleAndClass().getHasRole());
    context.setRealm(vars.getCurrentLoginContext().getRealm());
    if (!vars.isPremium()) {
        man = Promises.failed(new IllegalArgumentException());
    } else {
      this.man = manager.getLRS(context).map(lrs -> {
        Defaults.setAddXHttpMethodOverrideHeader(false);
        XapiManager x = new XapiManager();
        x.setServer(lrs.getEndpoint());
        x.setAuth(lrs.getAuth());
        x.setAgent(lrs.getAgent());
        return x;
      });
    }
  }

  public Promise<String> saveStatement(Statement s) {
    return man.then(xapi -> {
      try {
        Defaults.ignoreJsonNulls();
        return xapi.getValue().saveStatement(s);
      } finally {
        Defaults.dontIgnoreJsonNulls();
      }
    });
  }
  
  public Promise<Agent> getAgent() {
    return man.map(XapiManager::getAgent);
  }

  public Promise<StatementsResult> query(DomSchoolClass sc, DomScoContextId sco) {
	  StatementsQuery q = new StatementsQuery();
	  q.relatedAgents = Boolean.TRUE;
	  q.verbID = COMPLETED;
	  q.activityID = "pid:" + sco.getId();
	  Agent a = q.agent = new Agent();
	  a.name = sc.getSchoolClassName();
	  a.account = new Account();
	  a.account.name = "pid:" + sc.getId();
	  a.objectType = Group.GROUP;  // This agent is a TEAM
	  q.ascending = Boolean.TRUE;
	  return man.then(xapi -> { 		  
		  try {
			  Defaults.ignoreJsonNulls();
			  q.agent.account.homePage = xapi.getValue().getAgent().account.homePage;
			  return xapi.getValue().queryStatements(q);
		  } finally {
			  Defaults.dontIgnoreJsonNulls();
		  }
	  });
  }
  
  
}
