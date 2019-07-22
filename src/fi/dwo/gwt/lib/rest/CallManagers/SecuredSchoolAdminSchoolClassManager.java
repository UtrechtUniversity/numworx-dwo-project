package fi.dwo.gwt.lib.rest.CallManagers;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.fusesource.restygwt.client.MethodCallback;
import org.osgi.util.promise.Promise;

import com.google.gwt.core.client.GWT;

import fi.dwo.gwt.lib.rest.client.RestCallers.SecuredSchoolAdminSchoolClassRestCaller;
import fi.dwo.gwt.lib.rest.util.PromiseCallback;
import nl.uu.fi.dwo.rest.dom.entities.DomContext;
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
        service =  GWT.create(SecuredSchoolAdminSchoolClassRestCaller.class);
    }

    public Promise<List<DomSchoolClass>> getSchoolClasses() {
        PromiseCallback<List<DomSchoolClass>> defer = new PromiseCallback<List<DomSchoolClass>>();
        this.getSchoolClasses(defer);
        return defer.getPromise();
    }

    private void getSchoolClasses(MethodCallback<List<DomSchoolClass>> callBack) {
        service.getSchoolClasses(callBack);
        LOG.log(Level.FINE, "Rest Callback performed.");
    }

    @Deprecated
    public Promise<List<DomTeacher>> getTeachersInSchool() {
        PromiseCallback<List<DomTeacher>> defer = new PromiseCallback<List<DomTeacher>>();
        this.getTeachersInSchool(defer);
        return defer.getPromise();
    }
    
    public Promise<List<DomTeacher>> getTeachersInSchool(DomContext context) {
    	PromiseCallback<List<DomTeacher>> defer = new PromiseCallback<>();
    	RestContext rest = new RestContext(); rest.setRestContext(context);
    	service.getTeachersInSchool(PathId.getId(context), rest, defer);
    	return defer.getPromise();
    }

    private void getTeachersInSchool(MethodCallback<List<DomTeacher>> callBack) {
        service.getTeachersInSchool(callBack);
        LOG.log(Level.FINE, "Rest Callback performed.");
    }

    @Deprecated
    public Promise<List<DomStudent>> getStudentsInSchool() {
        PromiseCallback<List<DomStudent>> defer = new PromiseCallback<List<DomStudent>>();
        this.getStudentsInSchool(defer);
        return defer.getPromise();
    }
    
    public Promise<List<DomStudent>> getStudentsInSchool(DomContext context) {
    	PromiseCallback<List<DomStudent>> defer = new PromiseCallback<>();
    	RestContext rest = new RestContext();
    	rest.setRestContext(context);
    	service.getStudentsInSchool(PathId.getId(context), rest, defer);
    	return defer.getPromise();
    }

    private void getStudentsInSchool(MethodCallback<List<DomStudent>> callBack) {
        service.getStudentsInSchool(callBack);
        LOG.log(Level.FINE, "Rest Callback performed.");
    }

    public Promise<List<DomTeacher>> getTeachersInSchoolClass(DomSchoolClass schoolClass) {
        PromiseCallback<List<DomTeacher>> defer = new PromiseCallback<List<DomTeacher>>();
        RestSchoolClass restSchoolClass = new RestSchoolClass();
        restSchoolClass.setRestContext(new DomContext());
        restSchoolClass.setDomSchoolClass(schoolClass);
        this.getTeachersInSchoolClass(restSchoolClass, defer);
        return defer.getPromise();
    }

    private void getTeachersInSchoolClass(RestSchoolClass restData, MethodCallback<List<DomTeacher>> callBack) {
        service.getTeachersInSchoolClass(restData, callBack);
        LOG.log(Level.FINE, "Rest Callback performed.");
    }

    public Promise<List<DomStudent>> getStudentsInSchoolClass(DomSchoolClass schoolClass) {
        PromiseCallback<List<DomStudent>> defer = new PromiseCallback<List<DomStudent>>();
        RestSchoolClass restSchoolClass = new RestSchoolClass();
        restSchoolClass.setRestContext(new DomContext());
        restSchoolClass.setDomSchoolClass(schoolClass);
        this.getStudentsInSchoolClass(restSchoolClass, defer);
        return defer.getPromise();
    }

    private void getStudentsInSchoolClass(RestSchoolClass restData, MethodCallback<List<DomStudent>> callBack) {
        service.getStudentsInSchoolClass(restData, callBack);
    }

    public Promise<Boolean> submitSchoolClass(DomSchoolClassFull schoolClass) {
        RestSchoolClassFull restSchoolClass = new RestSchoolClassFull();
        restSchoolClass.setRestContext(new DomContext());
        restSchoolClass.setDomSchoolClassFull(schoolClass);
        PromiseCallback<Boolean> defer = new PromiseCallback<Boolean>();
        this.submitSchoolClass(restSchoolClass, defer);
        return defer.getPromise();
    }

    private void submitSchoolClass(RestSchoolClassFull schoolClass, MethodCallback<Boolean> callBack) {
        service.submitSchoolClass(schoolClass, callBack);
    }

    public Promise<Boolean> removeSchoolClass(DomSchoolClass schoolClass) {
        RestSchoolClass restSchoolClass = new RestSchoolClass();
        restSchoolClass.setRestContext(new DomContext());
        restSchoolClass.setDomSchoolClass(schoolClass);
        PromiseCallback<Boolean> defer = new PromiseCallback<Boolean>();
        this.removeSchoolClass(restSchoolClass, defer);
        return defer.getPromise();
    }

    private void removeSchoolClass(RestSchoolClass restData, MethodCallback<Boolean> callBack) {
        service.removeSchoolClass(restData, callBack);
    }

    public Promise<List<DomSchoolClass>> getSchoolsClasses() {
        PromiseCallback<List<DomSchoolClass>> defer = new PromiseCallback<List<DomSchoolClass>>();
        this.getSchoolsClasses(defer);
        return defer.getPromise();
    }

    private void getSchoolsClasses(MethodCallback<List<DomSchoolClass>> callBack) {
        service.getSchoolsClasses(callBack);
    }

    public Promise<Boolean> submitTeacherToSchoolClass(DomSubmitTeacherToSchoolClass teacherToClass) {
        RestSubmitTeacherToSchoolClass restData = new RestSubmitTeacherToSchoolClass();
        restData.setRestContext(new DomContext());
        restData.setDomSubmitTeacherToSchoolClass(teacherToClass);
        PromiseCallback<Boolean> defer = new PromiseCallback<Boolean>();
        this.submitTeacherToSchoolClass(restData, defer);
        return defer.getPromise();
    }

    private void submitTeacherToSchoolClass(RestSubmitTeacherToSchoolClass restData, MethodCallback<Boolean> callBack) {
        service.submitTeacherToSchoolClass(restData, callBack);
    }

    public Promise<Boolean> removeTeacherFromSchoolClass(DomRemoveTeacherFromSchoolClass data) {
        RestRemoveTeacherFromSchoolClass restData = new RestRemoveTeacherFromSchoolClass();
        restData.setRestContext(new DomContext());
        restData.setDomRemoveTeacherFromSchoolClass(data);
        PromiseCallback<Boolean> defer = new PromiseCallback<Boolean>();
        this.removeTeacherFromSchoolClass(restData, defer);
        return defer.getPromise();
    }

    private void removeTeacherFromSchoolClass(RestRemoveTeacherFromSchoolClass restData, MethodCallback<Boolean> callBack) {
        service.removeTeacherFromSchoolClass(restData, callBack);
    }

    public Promise<Boolean> moveStudentToSchoolClass(DomMoveStudentToSchoolClass studentToClass) {
        RestMoveStudentToSchoolClass restData = new RestMoveStudentToSchoolClass();
        restData.setRestContext(new DomContext());
        restData.setDomMoveStudentToSchoolClass(studentToClass);
        PromiseCallback<Boolean> defer = new PromiseCallback<Boolean>();
        this.moveStudentToSchoolClass(restData, defer);
        return defer.getPromise();
    }

    private void moveStudentToSchoolClass(RestMoveStudentToSchoolClass restData, MethodCallback<Boolean> callBack) {
        service.moveStudentToSchoolClass(restData, callBack);
    }    
    
    public Promise<Boolean> submitStudentToSchoolClass(DomSubmitStudentToSchoolClass studentToClass) {
        RestSubmitStudentToSchoolClass restData = new RestSubmitStudentToSchoolClass();
        restData.setRestContext(new DomContext());
        restData.setDomSubmitStudentToSchoolClass(studentToClass);
        PromiseCallback<Boolean> defer = new PromiseCallback<Boolean>();
        this.submitStudentToSchoolClass(restData, defer);
        return defer.getPromise();
    }

    private void submitStudentToSchoolClass(RestSubmitStudentToSchoolClass restData, MethodCallback<Boolean> callBack) {
        service.submitStudentToSchoolClass(restData, callBack);
    }

    public Promise<Boolean> removeStudentFromSchoolClass(DomRemoveStudentFromSchoolClass data) {
        RestRemoveStudentFromSchoolClass restData = new RestRemoveStudentFromSchoolClass();
        restData.setRestContext(new DomContext());
        restData.setDomRemoveStudentFromSchoolClass(data);
        PromiseCallback<Boolean> defer = new PromiseCallback<Boolean>();
        this.removeStudentFromSchoolClass(restData, defer);
        return defer.getPromise();
    }

    private void removeStudentFromSchoolClass(RestRemoveStudentFromSchoolClass restData, MethodCallback<Boolean> callBack) {
        service.removeStudentFromSchoolClass(restData, callBack);
    }

    public Promise<Boolean> updateSchoolClass(DomSchoolClassFull schoolClassFull) {
        RestSchoolClassFull restData = new RestSchoolClassFull();
        restData.setRestContext(new DomContext());
        restData.setDomSchoolClassFull(schoolClassFull);
        PromiseCallback<Boolean> defer = new PromiseCallback<Boolean>();
        this.updateSchoolClass(restData, defer);
        return defer.getPromise();
    }

    private void updateSchoolClass(RestSchoolClassFull restData, MethodCallback<Boolean> callBack) {
        service.updateSchoolClass(restData, (callBack));
    }

    public Promise<DomSchoolClassFull> getFullSchoolClass(DomSchoolClass schoolClass) {
        RestSchoolClass restData = new RestSchoolClass();
        restData.setRestContext(new DomContext());
        restData.setDomSchoolClass(schoolClass);
        PromiseCallback<DomSchoolClassFull> defer = new PromiseCallback<DomSchoolClassFull>();
        this.getFullSchoolClass(restData, defer);
        return defer.getPromise();
    }

    private void getFullSchoolClass(RestSchoolClass restData, MethodCallback<DomSchoolClassFull> callBack) {
        service.getFullSchoolClass(restData, (callBack));
    }

    public Promise<List<DomStudent>> getSingleSchoolStudentsInSchool() {
        PromiseCallback<List<DomStudent>> defer = new PromiseCallback<List<DomStudent>>();
        this.getSingleSchoolStudentsInSchool(defer);
        return defer.getPromise();
    }

    public void getSingleSchoolStudentsInSchool(MethodCallback<List<DomStudent>> callBack) {
        service.getSingleSchoolStudentsInSchool(callBack);
    }

    public Promise<Boolean> submitSingleSchoolStudent(DomNewSingleSchoolStudent newStudent) {
        RestNewSingleSchoolStudent restData = new RestNewSingleSchoolStudent();
        restData.setRestContext(new DomContext());
        restData.setDomNewSingleSchoolStudent(newStudent);
        PromiseCallback<Boolean> defer = new PromiseCallback<Boolean>();
        this.submitSingleSchoolStudent(restData, defer);
        return defer.getPromise();
    }

    private void submitSingleSchoolStudent(RestNewSingleSchoolStudent restData, MethodCallback<Boolean> callBack) {
        service.submitSingleSchoolStudent(restData, (callBack));
    }
    
}
