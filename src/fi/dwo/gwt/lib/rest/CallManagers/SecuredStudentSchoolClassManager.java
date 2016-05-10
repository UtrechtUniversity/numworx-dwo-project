/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.dwo.gwt.lib.rest.CallManagers;

import fi.dwo.gwt.lib.rest.util.RestAuthenticator;
import com.google.gwt.user.client.rpc.AsyncCallback;
import fi.dwo.gwt.lib.rest.client.SecuredStudentSchoolClassRestCaller;
import fi.dwo.rest.dom.entities.DomContext;
import fi.dwo.rest.dom.entities.DomNewSchoolClass4Student;
import fi.dwo.rest.dom.entities.DomSchoolClass;
import fi.dwo.rest.entities.RestNewSchoolClass4Student;
import fi.dwo.rest.entities.RestSchoolClass;
import java.util.List;
import java.util.logging.Logger;

/**
 *
 * @author Gert van der Plas
 */
public class SecuredStudentSchoolClassManager {

    private RestAuthenticator auth = new RestAuthenticator();
    private SecuredStudentSchoolClassRestCaller service;
    private static final Logger LOG = Logger.getLogger(SecuredStudentSchoolClassManager.class.getName());

    

    public void setActiveSchoolClass(DomSchoolClass schoolClass, AsyncCallback<Boolean> callBack) {
        RestSchoolClass restSchoolClass = new RestSchoolClass();
        restSchoolClass.setRestContext(new DomContext());
        restSchoolClass.setDomSchoolClass(schoolClass);
        service.setActiveSchoolClass(restSchoolClass, new Callback<Boolean>(callBack));
    }

    public void removeSchoolClass(DomSchoolClass schoolClass, AsyncCallback<Boolean> callBack) {
        RestSchoolClass restSchoolClass = new RestSchoolClass();
        restSchoolClass.setRestContext(new DomContext());
        restSchoolClass.setDomSchoolClass(schoolClass);
        service.removeSchoolClass(restSchoolClass, new Callback<Boolean>(callBack));
    }

    public void getStudentsSchoolClasses(AsyncCallback<List<DomSchoolClass>> callBack) {
        service.getStudentsSchoolClasses(new Callback<List<DomSchoolClass>>(callBack));
    }

    public void registerStudentForSchoolClass(DomNewSchoolClass4Student submit, AsyncCallback<Boolean> callBack) {
        RestNewSchoolClass4Student restData = new RestNewSchoolClass4Student();
        restData.setRestContext(new DomContext());
        restData.setDomNewSchoolClass4Student(submit);
        service.registerStudentForSchoolClass(restData, new Callback<Boolean>(callBack));
    }

    public void getSchoolsClasses(AsyncCallback<List<DomSchoolClass>> callBack) {
        service.getSchoolsClasses(new Callback<List<DomSchoolClass>>(callBack));
    }

    public void getActiveSchoolClass(AsyncCallback<DomSchoolClass> callBack) {
        service.getActiveSchoolClass(new Callback<DomSchoolClass>(callBack));
    }

}
