package fi.dwo.gwt.lib.rest.CallManagers;

import com.google.gwt.core.client.GWT;
import static fi.dwo.gwt.lib.rest.GwtRestVars.F;
import fi.dwo.gwt.lib.rest.client.RestCallers.SecuredTeacherSchoolClassRestCaller;
import fi.dwo.gwt.lib.rest.util.PromiseCallback;
import nl.uu.fi.dwo.rest.dom.entities.DomContext;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClass;
import nl.uu.fi.dwo.rest.entities.RestSchoolClass;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import nl.uu.fi.dwo.rest.dom.entities.DomCoursesOfSchoolClass4Teacher;
import nl.uu.fi.dwo.rest.dom.entities.DomMoveStudentToSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomNewSingleSchoolStudent;
import nl.uu.fi.dwo.rest.dom.entities.DomRemoveStudentFromSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomRemoveTeacherFromSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClassAndProfile;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClassFull;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClassId;
import nl.uu.fi.dwo.rest.dom.entities.DomSingleSchoolStudent;
import nl.uu.fi.dwo.rest.dom.entities.DomStudent;
import nl.uu.fi.dwo.rest.dom.entities.DomSubmitStudentToSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomSubmitTeacherToSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomTeacher;
import nl.uu.fi.dwo.rest.entities.RestContext;
import nl.uu.fi.dwo.rest.entities.RestGetSingleSchoolStudent;
import nl.uu.fi.dwo.rest.entities.RestMoveStudentToSchoolClass;
import nl.uu.fi.dwo.rest.entities.RestNewSingleSchoolStudent;
import nl.uu.fi.dwo.rest.entities.RestRemoveStudentFromSchoolClass;
import nl.uu.fi.dwo.rest.entities.RestRemoveTeacherFromSchoolClass;
import nl.uu.fi.dwo.rest.entities.RestSchoolClassAndProfile;
import nl.uu.fi.dwo.rest.entities.RestSchoolClassCourseAndProfile;
import nl.uu.fi.dwo.rest.entities.RestSchoolClassCourseAndProfileNew;
import nl.uu.fi.dwo.rest.entities.RestSchoolClassCourseProfilewAccessKey;
import nl.uu.fi.dwo.rest.entities.RestSchoolClassCourseProfilewFrom;
import nl.uu.fi.dwo.rest.entities.RestSchoolClassCourseProfilewTo;
import nl.uu.fi.dwo.rest.entities.RestSchoolClassCourseProfilewType;
import nl.uu.fi.dwo.rest.entities.RestSchoolClassFull;
import nl.uu.fi.dwo.rest.entities.RestSingleSchoolStudent;
import nl.uu.fi.dwo.rest.entities.RestStudent;
import nl.uu.fi.dwo.rest.entities.RestSubmitStudentToSchoolClass;
import nl.uu.fi.dwo.rest.entities.RestSubmitTeacherToSchoolClass;
import nl.uu.fi.dwo.rest.entities.RestTeacher;
import nl.uu.fi.dwo.rest.util.PathId;

import org.fusesource.restygwt.client.MethodCallback;
import org.osgi.util.promise.Promise;

/**
 *
 * @author Gert van der Plas
 */
public class SecuredTeacherSchoolClassManager {

    private static final Logger LOG = Logger.getLogger(SecuredTeacherSchoolClassManager.class.getName());
    private SecuredTeacherSchoolClassRestCaller service;

    private RestContext restContext(DomContext dom) {
    	RestContext rest = new RestContext();
    	rest.setRestContext(dom);
    	return rest;
    }
        
    public SecuredTeacherSchoolClassManager() {
        service = (SecuredTeacherSchoolClassRestCaller) GWT.create(SecuredTeacherSchoolClassRestCaller.class);
    }

    public Promise<List<DomSchoolClass>> getTeachersSchoolClasses(DomContext context) {
        PromiseCallback<List<DomSchoolClass>> defer = new PromiseCallback<List<DomSchoolClass>>();
        this.getTeachersSchoolClasses(PathId.getId(context),context, defer);
        return defer.getPromise();
    }

    private void getTeachersSchoolClasses(String id, DomContext dom, MethodCallback<List<DomSchoolClass>> callBack) {
        F(service::getTeachersSchoolClasses,id, restContext(dom), (callBack));
        LOG.log(Level.FINE, "Rest Callback performed.");
    }

    public Promise<List<DomTeacher>> getTeachersInSchool(DomContext context) {
        PromiseCallback<List<DomTeacher>> defer = new PromiseCallback<List<DomTeacher>>();
        this.getTeachersInSchool(PathId.getId(context),context, defer);
        return defer.getPromise();
    }

    private void getTeachersInSchool(String id, DomContext dom, MethodCallback<List<DomTeacher>> callBack) {
        F(service::getTeachersInSchool,id, restContext(dom), (callBack));
        LOG.log(Level.FINE, "Rest Callback performed.");
    }

    public Promise<List<DomStudent>> getStudentsInSchool(DomContext context) {
        PromiseCallback<List<DomStudent>> defer = new PromiseCallback<List<DomStudent>>();
        this.getStudentsInSchool(PathId.getId(context),context, defer);
        return defer.getPromise();
    }

    private void getStudentsInSchool(String id, DomContext dom, MethodCallback<List<DomStudent>> callBack) {
        F(service::getStudentsInSchool,id, restContext(dom), (callBack));
        LOG.log(Level.FINE, "Rest Callback performed.");
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
        F(service::getTeachersInSchoolClass,PathId.getId(restData.getRestContext()),restData, callBack);
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
        F(service::getStudentsInSchoolClass,PathId.getId(restData.getRestContext()),restData, (callBack));
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
        F(service::submitSchoolClass,PathId.getId(schoolClass.getRestContext()),schoolClass, (callBack));
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
        F(service::removeSchoolClass,PathId.getId(restData.getRestContext()),restData, (callBack));
    }

    public Promise<List<DomSchoolClass>> getSchoolsClasses(DomContext context) {
        PromiseCallback<List<DomSchoolClass>> defer = new PromiseCallback<List<DomSchoolClass>>();
        this.getSchoolsClasses(PathId.getId(context),context, defer);
        return defer.getPromise();
    }

    private void getSchoolsClasses(String id, DomContext dom, MethodCallback<List<DomSchoolClass>> callBack) {
        F(service::getSchoolsClasses,id, restContext(dom), (callBack));
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
        F(service::submitTeacherToSchoolClass,PathId.getId(restData.getRestContext()),restData, (callBack));
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
        F(service::removeTeacherFromSchoolClass,PathId.getId(restData.getRestContext()),restData, (callBack));
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
        F(service::moveStudentToSchoolClass,PathId.getId(restData.getRestContext()),restData, (callBack));
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
        F(service::submitStudentToSchoolClass,PathId.getId(restData.getRestContext()),restData, (callBack));
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
        F(service::removeStudentFromSchoolClass,PathId.getId(restData.getRestContext()),restData, (callBack));
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
        F(service::updateSchoolClass,PathId.getId(restData.getRestContext()),restData, (callBack));
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
        F(service::getFullSchoolClass,PathId.getId(restData.getRestContext()),restData, (callBack));
    }

    public Promise<List<DomStudent>> getSingleSchoolStudentsInSchool(DomContext context) {
        PromiseCallback<List<DomStudent>> defer = new PromiseCallback<List<DomStudent>>();
        this.getSingleSchoolStudentsInSchool(PathId.getId(context),context, defer);
        return defer.getPromise();
    }

    private void getSingleSchoolStudentsInSchool(String id, DomContext dom, MethodCallback<List<DomStudent>> callBack) {
        F(service::getSingleSchoolStudentsInSchool,id, restContext(dom), (callBack));
    }

    public Promise<Boolean> submitSingleSchoolStudent(DomContext context, DomNewSingleSchoolStudent newStudent) {
        RestNewSingleSchoolStudent restData = new RestNewSingleSchoolStudent();
        restData.setRestContext(context);
        restData.setDomNewSingleSchoolStudent(newStudent);
        PromiseCallback<Boolean> defer = new PromiseCallback<Boolean>();
        this.submitSingleSchoolStudent(restData, defer);
        return defer.getPromise();
    }

    private void submitSingleSchoolStudent(RestNewSingleSchoolStudent restData, MethodCallback<Boolean> callBack) {
        F(service::submitSingleSchoolStudent,PathId.getId(restData.getRestContext()),restData, (callBack));
    }

    public Promise<DomSingleSchoolStudent> getSingleSchoolStudent(RestGetSingleSchoolStudent restData) {
        PromiseCallback<DomSingleSchoolStudent> defer = new PromiseCallback<DomSingleSchoolStudent>();
        this.getSingleSchoolStudent(restData, defer);
        return defer.getPromise();
    }

    private void getSingleSchoolStudent(RestGetSingleSchoolStudent restData, MethodCallback<DomSingleSchoolStudent> callBack) {
        F(service::getSingleSchoolStudent,PathId.getId(restData.getRestContext()),restData, callBack);
    }

    public Promise<Boolean> updateSingleSchoolStudent(RestSingleSchoolStudent restData) {        
        PromiseCallback<Boolean> defer = new PromiseCallback<Boolean>();
        this.updateSingleSchoolStudent(restData, defer);
        return defer.getPromise();
    }

    private void updateSingleSchoolStudent(RestSingleSchoolStudent restData, MethodCallback<Boolean> callBack) {
        F(service::updateSingleSchoolStudent,PathId.getId(restData.getRestContext()),restData, (callBack));
    }

    public Promise<DomCoursesOfSchoolClass4Teacher> getModules(DomContext context, DomSchoolClassAndProfile submit) {
        RestSchoolClassAndProfile restData = new RestSchoolClassAndProfile();
        restData.setRestContext(context);
        restData.setDomSchoolClassAndProfile(submit);
        PromiseCallback<DomCoursesOfSchoolClass4Teacher> defer = new PromiseCallback<DomCoursesOfSchoolClass4Teacher>();
        this.getModules(restData, defer);
        return defer.getPromise();
    }

    private void getModules(RestSchoolClassAndProfile restData, MethodCallback<DomCoursesOfSchoolClass4Teacher> callBack) {
        F(service::getModules,PathId.getId(restData.getRestContext()),restData, (callBack));
    }

    public Promise<Boolean> addCourseToClass(RestSchoolClassCourseAndProfileNew rest) {
        PromiseCallback<Boolean> defer = new PromiseCallback<Boolean>();
        this.addCourseToClass(rest, defer);
        return defer.getPromise();
    }
    
    private void addCourseToClass(RestSchoolClassCourseAndProfileNew restData, MethodCallback<Boolean> callBack) {
        F(service::addCourseToClass,PathId.getId(restData.getRestContext()),restData, (callBack));
    }            
    
    
    public Promise<Boolean> attachCourseToClass(RestSchoolClassCourseAndProfile rest) {
        PromiseCallback<Boolean> defer = new PromiseCallback<Boolean>();
        this.attachCourseToClass(rest, defer);
        return defer.getPromise();
    }
    
    private void attachCourseToClass(RestSchoolClassCourseAndProfile restData, MethodCallback<Boolean> callBack) {
        F(service::attachCourseToClass,PathId.getId(restData.getRestContext()),restData, (callBack));
    }            

    public Promise<Boolean> detachCourseFromClass(RestSchoolClassCourseAndProfile rest) {
        PromiseCallback<Boolean> defer = new PromiseCallback<Boolean>();
        this.detachCourseFromClass(rest, defer);
        return defer.getPromise();
    }
    
    private void detachCourseFromClass(RestSchoolClassCourseAndProfile restData, MethodCallback<Boolean> callBack) {
        F(service::detachCourseFromClass,PathId.getId(restData.getRestContext()),restData, (callBack));
    }            

    public Promise<Boolean> setFromDateClassCourse(RestSchoolClassCourseProfilewFrom rest) {
        PromiseCallback<Boolean> defer = new PromiseCallback<Boolean>();
        this.setFromDateClassCourse(rest, defer);
        return defer.getPromise();
    }
    
    private void setFromDateClassCourse(RestSchoolClassCourseProfilewFrom restData, MethodCallback<Boolean> callBack) {
        F(service::setFromDateClassCourse,PathId.getId(restData.getRestContext()),restData, (callBack));
    }            

    public Promise<Boolean> setToDateClassCourse(RestSchoolClassCourseProfilewTo rest) {
        PromiseCallback<Boolean> defer = new PromiseCallback<Boolean>();
        this.setToDateClassCourse(rest, defer);
        return defer.getPromise();
    }
    
    private void setToDateClassCourse(RestSchoolClassCourseProfilewTo restData, MethodCallback<Boolean> callBack) {
        F(service::setToDateClassCourse,PathId.getId(restData.getRestContext()),restData, (callBack));
    }            

    public Promise<Boolean> setClassCourseType(RestSchoolClassCourseProfilewType rest) {
        PromiseCallback<Boolean> defer = new PromiseCallback<Boolean>();
        this.setClassCourseType(rest, defer);
        return defer.getPromise();
    }
    
    private void setClassCourseType(RestSchoolClassCourseProfilewType restData, MethodCallback<Boolean> callBack) {
    	F(service::setClassCourseType,PathId.getId(restData.getRestContext()),restData, (callBack));
    }

	public Promise<Boolean> setAccessKeyClassCourse(RestSchoolClassCourseProfilewAccessKey restData) {
        PromiseCallback<Boolean> defer = new PromiseCallback<Boolean>();
        F(service::setAccessKeyClassCourse,PathId.getId(restData.getRestContext()),restData, (defer));
        return defer.getPromise();
	}            

    public Promise<List<DomStudent>> getTeachersStudents(DomContext context) {
        PromiseCallback<List<DomStudent>> defer = new PromiseCallback<List<DomStudent>>();
        this.getTeachersStudents(PathId.getId(context),context, defer);
        return defer.getPromise();
    }
    
    private void getTeachersStudents(String id, DomContext dom, MethodCallback<List<DomStudent>> callBack) {
        F(service::getTeachersStudents,id, restContext(dom), (callBack));
    }

    public Promise<List<DomSchoolClassId>> getTeachersClassesOfStudent(RestStudent restData) {
        PromiseCallback<List<DomSchoolClassId>> defer = new PromiseCallback<List<DomSchoolClassId>>();
        this.getTeachersClassesOfStudent(restData, defer);
        return defer.getPromise();
    }
    
    private void getTeachersClassesOfStudent(RestStudent restData, MethodCallback<List<DomSchoolClassId>> callBack) {
        F(service::getTeachersClassesOfStudent,PathId.getId(restData.getRestContext()),restData, (callBack));
    }            

    public Promise<List<DomSchoolClassId>> getSharedTeacherClasses(RestTeacher rest) {
        PromiseCallback<List<DomSchoolClassId>> defer = new PromiseCallback<List<DomSchoolClassId>>();
        this.getSharedTeacherClasses(rest, defer);
        return defer.getPromise();
    }
        private void getSharedTeacherClasses(RestTeacher restData, MethodCallback<List<DomSchoolClassId>> callBack) {
        F(service::getSharedTeacherClasses,PathId.getId(restData.getRestContext()),restData, (callBack));
    }            

}
