package fi.dwo.gwt.lib.rest.CallManagers;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import fi.dwo.gwt.lib.rest.GwtRestVars;
import fi.dwo.gwt.lib.rest.client.RestCallers.SecuredStudentSchoolClassRestCaller;
import fi.dwo.gwt.lib.rest.util.PromiseCallback;
import nl.uu.fi.dwo.rest.dom.entities.DomContext;
import nl.uu.fi.dwo.rest.dom.entities.DomNewSchoolClass4Student;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClass;
import nl.uu.fi.dwo.rest.entities.RestNewSchoolClass4Student;
import nl.uu.fi.dwo.rest.entities.RestSchoolClass;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import nl.uu.fi.dwo.rest.dom.entities.DomNewUser;
import org.osgi.util.promise.Promise;

/**
 *
 * @author Gert van der Plas
 */
public class SecuredStudentSchoolClassManager {

    private static final Logger LOG = Logger.getLogger(SecuredStudentSchoolClassManager.class.getName());
    private SecuredStudentSchoolClassRestCaller service;

    private GwtRestVars dgv;

    public SecuredStudentSchoolClassManager() {
        service = (SecuredStudentSchoolClassRestCaller) GWT.create(SecuredStudentSchoolClassRestCaller.class);
    }

    public Promise<Boolean> setActiveSchoolClass(DomSchoolClass schoolClass) {
        PromiseCallback<Boolean> defer = new PromiseCallback<Boolean>();
        this.setActiveSchoolClass(schoolClass, defer);
        return defer.getPromise();
    }

    public void setActiveSchoolClass(DomSchoolClass schoolClass, AsyncCallback<Boolean> callBack) {
        RestSchoolClass restSchoolClass = new RestSchoolClass();
        restSchoolClass.setRestContext(new DomContext());
        restSchoolClass.setDomSchoolClass(schoolClass);
        service.setActiveSchoolClass(restSchoolClass, new Callback<Boolean>(callBack));
        LOG.log(Level.FINE, "Rest Callback performed.");
    }


    public Promise<Boolean> removeSchoolClass(DomSchoolClass schoolClass) {
        PromiseCallback<Boolean> defer = new PromiseCallback<Boolean>();
        this.removeSchoolClass(schoolClass, defer);
        return defer.getPromise();
    }
    
    public void removeSchoolClass(DomSchoolClass schoolClass, AsyncCallback<Boolean> callBack) {
        RestSchoolClass restSchoolClass = new RestSchoolClass();
        restSchoolClass.setRestContext(new DomContext());
        restSchoolClass.setDomSchoolClass(schoolClass);
        service.removeSchoolClass(restSchoolClass, new Callback<Boolean>(callBack));
        LOG.log(Level.FINE, "Rest Callback performed.");
    }


    public Promise<List<DomSchoolClass>> getStudentsSchoolClasses() {
        PromiseCallback<List<DomSchoolClass>> defer = new PromiseCallback<List<DomSchoolClass>>();
        this.getStudentsSchoolClasses(defer);
        return defer.getPromise();
    }
    
    public void getStudentsSchoolClasses(AsyncCallback<List<DomSchoolClass>> callBack) {
        service.getStudentsSchoolClasses(new Callback<List<DomSchoolClass>>(callBack));
        LOG.log(Level.FINE, "Rest Callback performed.");
    }

    public Promise<Boolean> registerStudentForSchoolClass(DomNewSchoolClass4Student submit) {
        PromiseCallback<Boolean> defer = new PromiseCallback<Boolean>();
        this.registerStudentForSchoolClass(submit, defer);
        return defer.getPromise();
    }
    
    public void registerStudentForSchoolClass(DomNewSchoolClass4Student submit, AsyncCallback<Boolean> callBack) {
        RestNewSchoolClass4Student restData = new RestNewSchoolClass4Student();
        restData.setRestContext(new DomContext());
        restData.setDomNewSchoolClass4Student(submit);
        service.registerStudentForSchoolClass(restData, new Callback<Boolean>(callBack));
        LOG.log(Level.FINE, "Rest Callback performed.");
    }


    public Promise<List<DomSchoolClass>> getSchoolsClasses() {
        PromiseCallback<List<DomSchoolClass>> defer = new PromiseCallback<List<DomSchoolClass>>();
        this.getSchoolsClasses(defer);
        return defer.getPromise();
    }
    
    public void getSchoolsClasses(AsyncCallback<List<DomSchoolClass>> callBack) {
        service.getSchoolsClasses(new Callback<List<DomSchoolClass>>(callBack));
        LOG.log(Level.FINE, "Rest Callback performed.");
    }


    public Promise<DomSchoolClass> getActiveSchoolClass() {
        PromiseCallback<DomSchoolClass> defer = new PromiseCallback<DomSchoolClass>();
        this.getActiveSchoolClass(defer);
        return defer.getPromise();
    }
    
    public void getActiveSchoolClass(AsyncCallback<DomSchoolClass> callBack) {
        service.getActiveSchoolClass(new Callback<DomSchoolClass>(callBack));
        LOG.log(Level.FINE, "Rest Callback performed.");
    }

}
