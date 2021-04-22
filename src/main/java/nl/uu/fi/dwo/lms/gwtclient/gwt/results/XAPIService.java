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
import nl.uu.fi.dwo.rest.dom.xapi.Agent;
import nl.uu.fi.dwo.rest.dom.xapi.Statement;

@RoleScope
class XAPIService {
  
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

}
