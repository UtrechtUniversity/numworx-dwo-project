package fi.dwo.gwt.lib.rest.CallManagers;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import fi.dwo.gwt.lib.rest.GwtRestVars;
import fi.dwo.gwt.lib.rest.client.RestCallers.SecuredTeacherSchoolClassRestCaller;
import fi.dwo.gwt.lib.rest.util.PromiseCallback;
import nl.uu.fi.dwo.rest.dom.entities.DomContext;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClass;
import nl.uu.fi.dwo.rest.entities.RestSchoolClass;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import nl.uu.fi.dwo.rest.dom.entities.DomNewSingleSchoolStudent;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClassFull;
import nl.uu.fi.dwo.rest.dom.entities.DomSingleSchoolStudent;
import nl.uu.fi.dwo.rest.dom.entities.DomStudent;
import nl.uu.fi.dwo.rest.dom.entities.DomSubmitStudentToSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomSubmitTeacherToSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomTeacher;
import nl.uu.fi.dwo.rest.entities.RestNewSingleSchoolStudent;
import nl.uu.fi.dwo.rest.entities.RestSchoolClassFull;
import nl.uu.fi.dwo.rest.entities.RestSingleSchoolStudent;
import nl.uu.fi.dwo.rest.entities.RestStudent;
import nl.uu.fi.dwo.rest.entities.RestSubmitStudentToSchoolClass;
import nl.uu.fi.dwo.rest.entities.RestSubmitTeacherToSchoolClass;
import nl.uu.fi.dwo.rest.entities.RestTeacher;
import org.osgi.util.promise.Promise;

/**
 *
 * @author Gert van der Plas
 */
public class SecuredTeacherSchoolClassManager {

    private static final Logger LOG = Logger.getLogger(SecuredTeacherSchoolClassManager.class.getName());
    private SecuredTeacherSchoolClassRestCaller service;

    private GwtRestVars dgv;

    public SecuredTeacherSchoolClassManager() {
        service = (SecuredTeacherSchoolClassRestCaller) GWT.create(SecuredTeacherSchoolClassRestCaller.class);
    }


    public Promise<List<DomSchoolClass>> getTeachersSchoolClasses() {
        PromiseCallback<List<DomSchoolClass>> defer = new PromiseCallback<List<DomSchoolClass>>();
        this.getTeachersSchoolClasses(defer);
        return defer.getPromise();
    }
    
    public void getTeachersSchoolClasses(AsyncCallback<List<DomSchoolClass>> callBack) {
        service.getTeachersSchoolClasses(new Callback<List<DomSchoolClass>>(callBack));
        LOG.log(Level.FINE, "Rest Callback performed.");
    }    
    
    public Promise<List<DomTeacher>> getTeachersInSchool() {
        PromiseCallback<List<DomTeacher>> defer = new PromiseCallback<List<DomTeacher>>();
        this.getTeachersInSchool(defer);
        return defer.getPromise();
    }
    
    public void getTeachersInSchool(AsyncCallback<List<DomTeacher>> callBack) {
        service.getTeachersInSchool(new Callback<List<DomTeacher>>(callBack));
        LOG.log(Level.FINE, "Rest Callback performed.");
    }    

   
    public Promise<List<DomStudent>> getStudentsInSchool() {
        PromiseCallback<List<DomStudent>> defer = new PromiseCallback<List<DomStudent>>();
        this.getStudentsInSchool(defer);
        return defer.getPromise();
    }
    
    public void getStudentsInSchool(AsyncCallback<List<DomStudent>> callBack) {
        service.getStudentsInSchool(new Callback<List<DomStudent>>(callBack));
        LOG.log(Level.FINE, "Rest Callback performed.");
    }    

    
    public Promise<List<DomTeacher>> getTeachersInSchoolClass(DomSchoolClass schoolClass) {
        PromiseCallback<List<DomTeacher>> defer = new PromiseCallback<List<DomTeacher>>();
        RestSchoolClass restSchoolClass = new RestSchoolClass();
        restSchoolClass.setRestContext(new DomContext());
        restSchoolClass.setDomSchoolClass(schoolClass);
        this.getTeachersInSchoolClass(restSchoolClass,defer);
        return defer.getPromise();
    }
    
    public void getTeachersInSchoolClass(RestSchoolClass restData, AsyncCallback<List<DomTeacher>> callBack) {
        service.getTeachersInSchoolClass(restData,new Callback<List<DomTeacher>>(callBack));
        LOG.log(Level.FINE, "Rest Callback performed.");
    }    
    
    public Promise<List<DomStudent>> getStudentsInSchoolClass(DomSchoolClass schoolClass) {
        PromiseCallback<List<DomStudent>> defer = new PromiseCallback<List<DomStudent>>();
        RestSchoolClass restSchoolClass = new RestSchoolClass();
        restSchoolClass.setRestContext(new DomContext());
        restSchoolClass.setDomSchoolClass(schoolClass);
        this.getStudentsInSchoolClass(restSchoolClass,defer);
        return defer.getPromise();
    }
    
    public void getStudentsInSchoolClass(RestSchoolClass restData, AsyncCallback<List<DomStudent>> callBack) {
        service.getStudentsInSchoolClass(restData,new Callback<List<DomStudent>>(callBack));
    }    

            
    public Promise<Boolean> submitSchoolClass(DomSchoolClassFull schoolClass) {
        RestSchoolClassFull restSchoolClass = new RestSchoolClassFull();
        restSchoolClass.setRestContext(new DomContext());
        restSchoolClass.setDomSchoolClassFull(schoolClass);
        PromiseCallback<Boolean> defer = new PromiseCallback<Boolean>();
        this.submitSchoolClass(restSchoolClass, defer);
        return defer.getPromise();
    }
    
    private void submitSchoolClass(RestSchoolClassFull schoolClass, AsyncCallback<Boolean> callBack) {
        service.submitSchoolClass(schoolClass, new Callback<Boolean>(callBack));
    }    

    public Promise<Boolean> removeSchoolClass(DomSchoolClass schoolClass) {
        RestSchoolClass restSchoolClass = new RestSchoolClass();
        restSchoolClass.setRestContext(new DomContext());
        restSchoolClass.setDomSchoolClass(schoolClass);
        PromiseCallback<Boolean> defer = new PromiseCallback<Boolean>();
        this.removeSchoolClass(restSchoolClass, defer);
        return defer.getPromise();
    }    
    private void removeSchoolClass(RestSchoolClass restData, AsyncCallback<Boolean> callBack) {
        service.removeSchoolClass(restData, new Callback<Boolean>(callBack));
    }

    public Promise<List<DomSchoolClass>> getSchoolsClasses() {
        PromiseCallback<List<DomSchoolClass>> defer = new PromiseCallback<List<DomSchoolClass>>();
        this.getSchoolsClasses(defer);
        return defer.getPromise();
    }
    
    public void getSchoolsClasses(AsyncCallback<List<DomSchoolClass>> callBack) {
        service.getSchoolsClasses(new Callback<List<DomSchoolClass>>(callBack));
    }
            
    public Promise<Boolean> submitTeacherToSchoolClass(DomSubmitTeacherToSchoolClass teacherToClass) {
        RestSubmitTeacherToSchoolClass restData = new RestSubmitTeacherToSchoolClass();
        restData.setRestContext(new DomContext());
        restData.setDomSubmitTeacherToSchoolClass(teacherToClass);
        PromiseCallback<Boolean> defer = new PromiseCallback<Boolean>();
        this.submitTeacherToSchoolClass(restData, defer);
        return defer.getPromise();
    }
    
    private void submitTeacherToSchoolClass(RestSubmitTeacherToSchoolClass restData, AsyncCallback<Boolean> callBack) {
        service.submitTeacherToSchoolClass(restData, new Callback<Boolean>(callBack));
    }   

    public Promise<Boolean> removeTeacherFromSchoolClass(DomTeacher teacher) {
        RestTeacher restData = new RestTeacher();
        restData.setRestContext(new DomContext());
        restData.setDomTeacher(teacher);
        PromiseCallback<Boolean> defer = new PromiseCallback<Boolean>();
        this.removeTeacherFromSchoolClass(restData, defer);
        return defer.getPromise();
    }
    
    private void removeTeacherFromSchoolClass(RestTeacher restData, AsyncCallback<Boolean> callBack) {
        service.removeTeacherFromSchoolClass(restData, new Callback<Boolean>(callBack));
    }   
    
    public Promise<Boolean> submitStudentToSchoolClass(DomSubmitStudentToSchoolClass studentToClass) {
        RestSubmitStudentToSchoolClass restData = new RestSubmitStudentToSchoolClass();
        restData.setRestContext(new DomContext());
        restData.setDomSubmitStudentToSchoolClass(studentToClass);
        PromiseCallback<Boolean> defer = new PromiseCallback<Boolean>();
        this.submitStudentToSchoolClass(restData, defer);
        return defer.getPromise();
    }
    
    private void submitStudentToSchoolClass(RestSubmitStudentToSchoolClass restData, AsyncCallback<Boolean> callBack) {
        service.submitStudentToSchoolClass(restData, new Callback<Boolean>(callBack));
    }   

    public Promise<Boolean> removeStudentFromSchoolClass(DomStudent student) {
        RestStudent restData = new RestStudent();
        restData.setRestContext(new DomContext());
        restData.setDomStudent(student);
        PromiseCallback<Boolean> defer = new PromiseCallback<Boolean>();
        this.removeStudentFromSchoolClass(restData, defer);
        return defer.getPromise();
    }
    
    private void removeStudentFromSchoolClass(RestStudent restData, AsyncCallback<Boolean> callBack) {
        service.removeStudentFromSchoolClass(restData, new Callback<Boolean>(callBack));
    }   


    public Promise<Boolean> updateSchoolClass(DomSchoolClassFull schoolClassFull) {
        RestSchoolClassFull restData = new RestSchoolClassFull();
        restData.setRestContext(new DomContext());
        restData.setDomSchoolClassFull(schoolClassFull);
        PromiseCallback<Boolean> defer = new PromiseCallback<Boolean>();
        this.updateSchoolClass(restData, defer);
        return defer.getPromise();
    }
    
    private void updateSchoolClass(RestSchoolClassFull restData, AsyncCallback<Boolean> callBack) {
        service.updateSchoolClass(restData, new Callback<Boolean>(callBack));
    }   

    public Promise<DomSchoolClassFull> getFullSchoolClass(DomSchoolClass schoolClass) {
        RestSchoolClass restData = new RestSchoolClass();
        restData.setRestContext(new DomContext());
        restData.setDomSchoolClass(schoolClass);
        PromiseCallback<DomSchoolClassFull> defer = new PromiseCallback<DomSchoolClassFull>();
        this.getFullSchoolClass(restData, defer);
        return defer.getPromise();
    }
    
    private void getFullSchoolClass(RestSchoolClass restData, AsyncCallback<DomSchoolClassFull> callBack) {
        service.getFullSchoolClass(restData, new Callback<DomSchoolClassFull>(callBack));
    }       
    

    public Promise<List<DomStudent>> getSingleSchoolStudentsInSchool() {
        PromiseCallback<List<DomStudent>> defer = new PromiseCallback<List<DomStudent>>();
        this.getSingleSchoolStudentsInSchool(defer);
        return defer.getPromise();
    }
    
    public void getSingleSchoolStudentsInSchool(AsyncCallback<List<DomStudent>> callBack) {
        service.getSingleSchoolStudentsInSchool(new Callback<List<DomStudent>>(callBack));
    }        
    

    public Promise<Boolean> submitSingleSchoolStudent(DomNewSingleSchoolStudent newStudent) {
        RestNewSingleSchoolStudent restData = new RestNewSingleSchoolStudent();
        restData.setRestContext(new DomContext());
        restData.setDomNewSingleSchoolStudent(newStudent);
        PromiseCallback<Boolean> defer = new PromiseCallback<Boolean>();
        this.submitSingleSchoolStudent(restData, defer);
        return defer.getPromise();
    }
    
    private void submitSingleSchoolStudent(RestNewSingleSchoolStudent restData, AsyncCallback<Boolean> callBack) {
        service.submitSingleSchoolStudent(restData, new Callback<Boolean>(callBack));
    }       
    

    public Promise<List<DomSingleSchoolStudent>> getSingleSchoolStudent() {
        PromiseCallback<List<DomSingleSchoolStudent>> defer = new PromiseCallback<List<DomSingleSchoolStudent>>();
        this.getSingleSchoolStudent(defer);
        return defer.getPromise();
    }
    
    public void getSingleSchoolStudent(AsyncCallback<List<DomSingleSchoolStudent>> callBack) {
        service.getSingleSchoolStudent(new Callback<List<DomSingleSchoolStudent>>(callBack));
    }        
    
    public Promise<Boolean> updateSingleSchoolStudent(DomSingleSchoolStudent updateStudent) {
        RestSingleSchoolStudent restData = new RestSingleSchoolStudent();
        restData.setRestContext(new DomContext());
        restData.setDomSingleSchoolStudent(updateStudent);
        PromiseCallback<Boolean> defer = new PromiseCallback<Boolean>();
        this.updateSingleSchoolStudent(restData, defer);
        return defer.getPromise();
    }
    
    private void updateSingleSchoolStudent(RestSingleSchoolStudent restData, AsyncCallback<Boolean> callBack) {
        service.updateSingleSchoolStudent(restData, new Callback<Boolean>(callBack));
    }       
    
}
