package fi.dwo.gwt.lib.rest.CallManagers;

import java.util.List;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.fusesource.restygwt.client.MethodCallback;
import org.osgi.util.promise.Promise;

import com.google.gwt.core.client.GWT;

import static fi.dwo.gwt.lib.rest.GwtRestVars.F;
import fi.dwo.gwt.lib.rest.client.RestCallers.SecuredSchoolAdminSchoolClassRestCaller;
import fi.dwo.gwt.lib.rest.util.PromiseCallback;
import nl.uu.fi.dwo.rest.dom.entities.DomContext;
import nl.uu.fi.dwo.rest.dom.entities.DomDwoProfileId;
import nl.uu.fi.dwo.rest.dom.entities.DomMoveStudentToSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomNewSingleSchoolStudent;
import nl.uu.fi.dwo.rest.dom.entities.DomRemoveStudentFromSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomRemoveTeacherFromSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClassFull;
import nl.uu.fi.dwo.rest.dom.entities.DomStudent;
import nl.uu.fi.dwo.rest.dom.entities.DomSubmitStudentToSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomSubmitTeacherToSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomTeacher;
import nl.uu.fi.dwo.rest.entities.RestContext;
import nl.uu.fi.dwo.rest.entities.RestMoveStudentToSchoolClass;
import nl.uu.fi.dwo.rest.entities.RestNewSingleSchoolStudent;
import nl.uu.fi.dwo.rest.entities.RestNewSingleSchoolStudentv2;
import nl.uu.fi.dwo.rest.entities.RestRemoveStudentFromSchoolClass;
import nl.uu.fi.dwo.rest.entities.RestRemoveTeacherFromSchoolClass;
import nl.uu.fi.dwo.rest.entities.RestSchoolClass;
import nl.uu.fi.dwo.rest.entities.RestSchoolClassFull;
import nl.uu.fi.dwo.rest.entities.RestSubmitStudentToSchoolClass;
import nl.uu.fi.dwo.rest.entities.RestSubmitTeacherToSchoolClass;
import nl.uu.fi.dwo.rest.util.PathId;

/**
 *
 * @author Gert van der Plas
 */
public class SecuredSchoolAdminSchoolClassManager {

    private static final Logger LOG = Logger.getLogger(SecuredSchoolAdminSchoolClassManager.class.getName());
    private SecuredSchoolAdminSchoolClassRestCaller service;

    public SecuredSchoolAdminSchoolClassManager() {
        service = GWT.create(SecuredSchoolAdminSchoolClassRestCaller.class);
    }

    public Promise<List<DomSchoolClass>> getSchoolClasses(DomContext context) {
        RestContext rest = new RestContext(); rest.setRestContext(context);
        return F(service::getSchoolClasses, PathId.getId(context), rest);
    }
 
    public Promise<List<DomTeacher>> getTeachersInSchool(DomContext context) {
    	RestContext rest = new RestContext(); rest.setRestContext(context);
    	return F(service::getTeachersInSchool,PathId.getId(context), rest);
    }
   
    public Promise<List<DomStudent>> getStudentsInSchool(DomContext context) {
    	RestContext rest = new RestContext();
    	rest.setRestContext(context);
    	return F(service::getStudentsInSchool,PathId.getId(context), rest);
    }

    public Promise<List<DomTeacher>> getTeachersInSchoolClass(DomContext context, DomSchoolClass schoolClass) {
        PromiseCallback<List<DomTeacher>> defer = new PromiseCallback<List<DomTeacher>>();
        RestSchoolClass restSchoolClass = new RestSchoolClass();
        restSchoolClass.setRestContext(context);
        restSchoolClass.setDomSchoolClass(schoolClass);
        this.getTeachersInSchoolClass(restSchoolClass, defer);
        return defer.getPromise();
    }

    private void getTeachersInSchoolClass(RestSchoolClass restData, MethodCallback<List<DomTeacher>> callBack) {
        F(service::getTeachersInSchoolClass,PathId.getId(restData.getRestContext()), restData, callBack);
        LOG.log(Level.FINE, "Rest Callback performed.");
    }

    public Promise<List<DomStudent>> getStudentsInSchoolClass(DomContext context, DomSchoolClass schoolClass) {
        PromiseCallback<List<DomStudent>> defer = new PromiseCallback<List<DomStudent>>();
        RestSchoolClass restSchoolClass = new RestSchoolClass();
        restSchoolClass.setRestContext(context);
        restSchoolClass.setDomSchoolClass(schoolClass);
        this.getStudentsInSchoolClass(restSchoolClass, defer);
        return defer.getPromise();
    }

    private void getStudentsInSchoolClass(RestSchoolClass restData, MethodCallback<List<DomStudent>> callBack) {
        F(service::getStudentsInSchoolClass,PathId.getId(restData.getRestContext()), restData, callBack);
    }

    public Promise<Boolean> submitSchoolClass(DomContext context, DomSchoolClassFull schoolClass) {
        RestSchoolClassFull restSchoolClass = new RestSchoolClassFull();
        restSchoolClass.setRestContext(context);
        restSchoolClass.setDomSchoolClassFull(schoolClass);
        PromiseCallback<Boolean> defer = new PromiseCallback<Boolean>();
        this.submitSchoolClass(restSchoolClass, defer);
        return defer.getPromise();
    }

    private void submitSchoolClass(RestSchoolClassFull schoolClass, MethodCallback<Boolean> callBack) {
        F(service::submitSchoolClass,PathId.getId(schoolClass.getRestContext()), schoolClass, callBack);
    }

    public Promise<Boolean> removeSchoolClass(DomContext context, DomSchoolClass schoolClass) {
        RestSchoolClass restSchoolClass = new RestSchoolClass();
        restSchoolClass.setRestContext(context);
        restSchoolClass.setDomSchoolClass(schoolClass);
        PromiseCallback<Boolean> defer = new PromiseCallback<Boolean>();
        this.removeSchoolClass(restSchoolClass, defer);
        return defer.getPromise();
    }

    private void removeSchoolClass(RestSchoolClass restData, MethodCallback<Boolean> callBack) {
        F(service::removeSchoolClass,PathId.getId(restData.getRestContext()), restData, callBack);
    }

    public Promise<Boolean> submitTeacherToSchoolClass(DomContext context, DomSubmitTeacherToSchoolClass teacherToClass) {
        RestSubmitTeacherToSchoolClass restData = new RestSubmitTeacherToSchoolClass();
        restData.setRestContext(context);
        restData.setDomSubmitTeacherToSchoolClass(teacherToClass);
        PromiseCallback<Boolean> defer = new PromiseCallback<Boolean>();
        this.submitTeacherToSchoolClass(restData, defer);
        return defer.getPromise();
    }

    private void submitTeacherToSchoolClass(RestSubmitTeacherToSchoolClass restData, MethodCallback<Boolean> callBack) {
        F(service::submitTeacherToSchoolClass,PathId.getId(restData.getRestContext()), restData, callBack);
    }

    public Promise<Boolean> removeTeacherFromSchoolClass(DomContext context, DomRemoveTeacherFromSchoolClass data) {
        RestRemoveTeacherFromSchoolClass restData = new RestRemoveTeacherFromSchoolClass();
        restData.setRestContext(context);
        restData.setDomRemoveTeacherFromSchoolClass(data);
        PromiseCallback<Boolean> defer = new PromiseCallback<Boolean>();
        this.removeTeacherFromSchoolClass(restData, defer);
        return defer.getPromise();
    }

    private void removeTeacherFromSchoolClass(RestRemoveTeacherFromSchoolClass restData, MethodCallback<Boolean> callBack) {
        F(service::removeTeacherFromSchoolClass,PathId.getId(restData.getRestContext()), restData, callBack);
    }

    public Promise<Boolean> moveStudentToSchoolClass(DomContext context, DomMoveStudentToSchoolClass studentToClass) {
        RestMoveStudentToSchoolClass restData = new RestMoveStudentToSchoolClass();
        restData.setRestContext(context);
        restData.setDomMoveStudentToSchoolClass(studentToClass);
        PromiseCallback<Boolean> defer = new PromiseCallback<Boolean>();
        this.moveStudentToSchoolClass(restData, defer);
        return defer.getPromise();
    }

    private void moveStudentToSchoolClass(RestMoveStudentToSchoolClass restData, MethodCallback<Boolean> callBack) {
        F(service::moveStudentToSchoolClass,PathId.getId(restData.getRestContext()), restData, callBack);
    }    
    
    public Promise<Boolean> submitStudentToSchoolClass(DomContext context, DomSubmitStudentToSchoolClass studentToClass) {
        RestSubmitStudentToSchoolClass restData = new RestSubmitStudentToSchoolClass();
        restData.setRestContext(context);
        restData.setDomSubmitStudentToSchoolClass(studentToClass);
        PromiseCallback<Boolean> defer = new PromiseCallback<Boolean>();
        this.submitStudentToSchoolClass(restData, defer);
        return defer.getPromise();
    }

    private void submitStudentToSchoolClass(RestSubmitStudentToSchoolClass restData, MethodCallback<Boolean> callBack) {
        F(service::submitStudentToSchoolClass,PathId.getId(restData.getRestContext()), restData, callBack);
    }

    public Promise<Boolean> removeStudentFromSchoolClass(DomContext context, DomRemoveStudentFromSchoolClass data) {
        RestRemoveStudentFromSchoolClass restData = new RestRemoveStudentFromSchoolClass();
        restData.setRestContext(context);
        restData.setDomRemoveStudentFromSchoolClass(data);
        PromiseCallback<Boolean> defer = new PromiseCallback<Boolean>();
        this.removeStudentFromSchoolClass(restData, defer);
        return defer.getPromise();
    }

    private void removeStudentFromSchoolClass(RestRemoveStudentFromSchoolClass restData, MethodCallback<Boolean> callBack) {
        F(service::removeStudentFromSchoolClass,PathId.getId(restData.getRestContext()), restData, callBack);
    }

    public Promise<Boolean> updateSchoolClass(DomContext context, DomSchoolClassFull schoolClassFull) {
        RestSchoolClassFull restData = new RestSchoolClassFull();
        restData.setRestContext(context);
        restData.setDomSchoolClassFull(schoolClassFull);
        PromiseCallback<Boolean> defer = new PromiseCallback<Boolean>();
        this.updateSchoolClass(restData, defer);
        return defer.getPromise();
    }

    private void updateSchoolClass(RestSchoolClassFull restData, MethodCallback<Boolean> callBack) {
        F(service::updateSchoolClass,PathId.getId(restData.getRestContext()), restData, (callBack));
    }

    public Promise<DomSchoolClassFull> getFullSchoolClass(DomContext context, DomSchoolClass schoolClass) {
        RestSchoolClass restData = new RestSchoolClass();
        restData.setRestContext(context);
        restData.setDomSchoolClass(schoolClass);
        PromiseCallback<DomSchoolClassFull> defer = new PromiseCallback<DomSchoolClassFull>();
        this.getFullSchoolClass(restData, defer);
        return defer.getPromise();
    }

    private void getFullSchoolClass(RestSchoolClass restData, MethodCallback<DomSchoolClassFull> callBack) {
        F(service::getFullSchoolClass,PathId.getId(restData.getRestContext()), restData, (callBack));
    }

    public Promise<List<DomStudent>> getSingleSchoolStudentsInSchool(DomContext context) {
        PromiseCallback<List<DomStudent>> defer = new PromiseCallback<List<DomStudent>>();
        RestContext rest = new RestContext(); rest.setRestContext(context);
        F(service::getSingleSchoolStudentsInSchool,PathId.getId(context), rest, defer);
        return defer.getPromise();
    }

    public Promise<Boolean> submitSingleSchoolStudent(DomContext context, DomNewSingleSchoolStudent newStudent) {
        RestNewSingleSchoolStudent restData = new RestNewSingleSchoolStudent();
        restData.setRestContext(context);
        restData.setDomNewSingleSchoolStudent(newStudent);
        PromiseCallback<Boolean> defer = new PromiseCallback<Boolean>();
        this.submitSingleSchoolStudent(restData, defer);
        return defer.getPromise();
    }

    public Promise<Boolean> submitSingleSchoolStudentv2(DomContext context, DomNewSingleSchoolStudent newStudent, DomDwoProfileId profile) {
        RestNewSingleSchoolStudentv2 restData = new RestNewSingleSchoolStudentv2();
        restData.setRestContext(context);
        restData.setDomNewSingleSchoolStudent(newStudent);
        restData.setDwoProfile(profile);
        PromiseCallback<Boolean> defer = new PromiseCallback<Boolean>();
        this.submitSingleSchoolStudentv2(restData, defer);
        return defer.getPromise();
    }

    private void submitSingleSchoolStudent(RestNewSingleSchoolStudent restData, MethodCallback<Boolean> callBack) {
        F(service::submitSingleSchoolStudent,PathId.getId(restData.getRestContext()), restData, (callBack));
    }

    private void submitSingleSchoolStudentv2(RestNewSingleSchoolStudentv2 restData, MethodCallback<Boolean> callBack) {
        F(service::submitSingleSchoolStudentv2,PathId.getId(restData.getRestContext()), restData, (callBack));
    }
   
}
