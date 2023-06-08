/**
 * 
 */
package nl.uu.fi.dwo.lms.gwtclient.gwt.persons;

import javax.inject.Inject;

import org.osgi.util.promise.Promise;
import org.osgi.util.promise.Promises;

import com.google.web.bindery.event.shared.EventBus;

import jsinterop.annotations.JsMethod;
import nl.uu.fi.dwo.lms.gwtclient.gwt.DwoGlobalVars;
import nl.uu.fi.dwo.rest.dom.entities.DomContext;
import nl.uu.fi.dwo.rest.dom.entities.DomGetSingleSchoolStudent;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomSingleSchoolStudent;
import nl.uu.fi.dwo.rest.dom.entities.DomStudent;
import nl.uu.fi.dwo.rest.dom.entities.DomSubmitStudentToSchoolClass;
import nl.uu.fi.dwo.rest.entities.RestGetSingleSchoolStudent;

/**
 * @author peterboon
 *
 */
public class EditStudentPresenterSchoolAdmin extends EditStudentPresenter {

  /**
   * @param anEventBus
   * @param aDwoGlobalVars
   * @param m
   */
  @Inject EditStudentPresenterSchoolAdmin(EventBus anEventBus, DwoGlobalVars aDwoGlobalVars,
      PersonsService m) {
    super(anEventBus, aDwoGlobalVars, m);
    
  }

  @Override
  Promise<Boolean> verifyStudentInTeachersClass() {
    return Promises.resolved(Boolean.TRUE);
  }

  @Override
  Promise<DomSingleSchoolStudent> getSingleSchoolStudent(DomGetSingleSchoolStudent student) {
    RestGetSingleSchoolStudent restData = new RestGetSingleSchoolStudent();
    DomContext ctx = new DomContext();
    ctx.setDomHasRole(dwoGlobalVars.getActiveSchoolRoleAndClass().getHasRole());
    restData.setRestContext(ctx);
    restData.setDomGetSingleSchoolStudent(student);
    return manager.getSingleSchoolStudent(restData);
  }

  @JsMethod
  public void submitStudentToSchoolClass(String schoolClassId) {
      DomSchoolClass to = taggedSchoolClassMap.get(schoolClassId).getSchoolClass();
      DomSubmitStudentToSchoolClass data = new DomSubmitStudentToSchoolClass();
      data.setSchoolClassFrom(null); // No from for Schooladmin
      data.setSchoolClassTo(to);
      data.setStudent(new DomStudent(user));
      Promise<Boolean> p = manager.submitStudentToSchoolClass(data);
      p.then((resolved) -> {
          this.initView(user);
          return null;
      }).then(null, FAILURE);

  }

  @Override
  Promise<Boolean> isInTeachersClass(Promise<Boolean> p) {
    return p;
  }

}
