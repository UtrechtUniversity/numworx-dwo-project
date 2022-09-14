package fi.dwo.gwt.lib.rest.CallManagers;

import com.google.gwt.core.client.GWT;
import static fi.dwo.gwt.lib.rest.GwtRestVars.F;
import fi.dwo.gwt.lib.rest.client.RestCallers.SecuredStudentSchoolClassRestCaller;
import fi.dwo.gwt.lib.rest.util.PromiseCallback;
import nl.uu.fi.dwo.rest.dom.entities.DomContext;
import nl.uu.fi.dwo.rest.dom.entities.DomNewSchoolClass4Student;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomStudent;
import nl.uu.fi.dwo.rest.dom.entities.DomTeacher;
import nl.uu.fi.dwo.rest.entities.RestContext;
import nl.uu.fi.dwo.rest.entities.RestNewSchoolClass4Student;
import nl.uu.fi.dwo.rest.entities.RestSchoolClass;
import nl.uu.fi.dwo.rest.util.PathId;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.fusesource.restygwt.client.MethodCallback;
import org.osgi.util.promise.Promise;

/**
 *
 * @author Gert van der Plas
 */
public class SecuredStudentSchoolClassManager {

    private static final Logger LOG = Logger.getLogger(SecuredStudentSchoolClassManager.class.getName());
    private SecuredStudentSchoolClassRestCaller service;

    public SecuredStudentSchoolClassManager() {
        service = GWT.create(SecuredStudentSchoolClassRestCaller.class);
    }

    public Promise<Boolean> setActiveSchoolClass(DomContext context, DomSchoolClass schoolClass) {
        PromiseCallback<Boolean> defer = new PromiseCallback<Boolean>();
        this.setActiveSchoolClass(context, schoolClass, defer);
        return defer.getPromise();
    }

    private void setActiveSchoolClass(DomContext context, DomSchoolClass schoolClass, PromiseCallback<Boolean> callBack) {
        RestSchoolClass restSchoolClass = new RestSchoolClass();
        restSchoolClass.setRestContext(context);
        restSchoolClass.setDomSchoolClass(schoolClass);
		String id = PathId.getId(context);
		F(service::setActiveSchoolClass,id, restSchoolClass, (callBack));
        LOG.log(Level.FINE, "Rest Callback performed.");
    }


    public Promise<Boolean> removeSchoolClass(DomContext context, DomSchoolClass schoolClass) {
        PromiseCallback<Boolean> defer = new PromiseCallback<Boolean>();
        this.removeSchoolClass(context, schoolClass, defer);
        return defer.getPromise();
    }
    
    private void removeSchoolClass(DomContext context, DomSchoolClass schoolClass, PromiseCallback<Boolean> callBack) {
        RestSchoolClass restSchoolClass = new RestSchoolClass();
        restSchoolClass.setRestContext(context);
        restSchoolClass.setDomSchoolClass(schoolClass);
		String id = PathId.getId(context);
		F(service::removeSchoolClass,id, restSchoolClass, (callBack));
        LOG.log(Level.FINE, "Rest Callback performed.");
    }


    public Promise<List<DomSchoolClass>> getStudentsSchoolClasses(DomContext context) {
        PromiseCallback<List<DomSchoolClass>> defer = new PromiseCallback<List<DomSchoolClass>>();
        this.getStudentsSchoolClasses(context, defer);
        return defer.getPromise();
    }
    
    private void getStudentsSchoolClasses(DomContext context, PromiseCallback<List<DomSchoolClass>> callBack) {
		String id = PathId.getId(context);
	      RestContext rest = new RestContext();
	      rest.setRestContext(context);
		F(service::getStudentsSchoolClasses,id, rest, (callBack));
        LOG.log(Level.FINE, "Rest Callback performed.");
    }

    public Promise<Boolean> registerStudentForSchoolClass(DomContext context, DomNewSchoolClass4Student submit) {
        PromiseCallback<Boolean> defer = new PromiseCallback<Boolean>();
        this.registerStudentForSchoolClass(context, submit, defer);
        return defer.getPromise();
    }
    
    private void registerStudentForSchoolClass(DomContext context, DomNewSchoolClass4Student submit, PromiseCallback<Boolean> callBack) {
        RestNewSchoolClass4Student restData = new RestNewSchoolClass4Student();
        restData.setRestContext(context);
        restData.setDomNewSchoolClass4Student(submit);
        String id = PathId.getId(context);
        F(service::registerStudentForSchoolClass,id, restData, (callBack));
        LOG.log(Level.FINE, "Rest Callback performed.");
    }


    public Promise<List<DomSchoolClass>> getSchoolsClasses(DomContext context) {
        PromiseCallback<List<DomSchoolClass>> defer = new PromiseCallback<List<DomSchoolClass>>();
        this.getSchoolsClasses(context, defer);
        return defer.getPromise();
    }
    
    private void getSchoolsClasses(DomContext context, PromiseCallback<List<DomSchoolClass>> callBack) {
      RestContext rest = new RestContext();
      rest.setRestContext(context);
      F(service::getSchoolsClasses,PathId.getId(context), rest, (callBack));
      LOG.log(Level.FINE, "Rest Callback performed.");
    }


    public Promise<DomSchoolClass> getActiveSchoolClass(DomContext context) {
        PromiseCallback<DomSchoolClass> defer = new PromiseCallback<DomSchoolClass>();
        this.getActiveSchoolClass(context, defer);
        return defer.getPromise();
    }
    
    private void getActiveSchoolClass(DomContext context, PromiseCallback<DomSchoolClass> callBack) {
      RestContext rest = new RestContext();
      rest.setRestContext(context);
        F(service::getActiveSchoolClass,PathId.getId(context), rest, (callBack));
        LOG.log(Level.FINE, "Rest Callback performed.");
    }

    public Promise<List<DomStudent>> getStudentsInSchoolClass(DomContext context, DomSchoolClass schoolClass) {
        PromiseCallback<List<DomStudent>> defer = new PromiseCallback<List<DomStudent>>();
        RestSchoolClass restSchoolClass = new RestSchoolClass();
        restSchoolClass.setRestContext(context);
        restSchoolClass.setDomSchoolClass(schoolClass);
        F(service::getStudentsInSchoolClass,PathId.getId(context),restSchoolClass, (defer));
        return defer.getPromise();
    }

    public Promise<List<DomTeacher>> getTeachersInSchoolClass(DomContext context, DomSchoolClass schoolClass) {
        PromiseCallback<List<DomTeacher>> defer = new PromiseCallback<List<DomTeacher>>();
        RestSchoolClass restSchoolClass = new RestSchoolClass();
        restSchoolClass.setRestContext(context);
        restSchoolClass.setDomSchoolClass(schoolClass);
        F(this.service::getTeachersInSchoolClass,PathId.getId(context),restSchoolClass, defer);
        return defer.getPromise();
    }
}
