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
import nl.uu.fi.dwo.rest.util.PathId;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
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

    public void setActiveSchoolClass(DomContext context, DomSchoolClass schoolClass, AsyncCallback<Boolean> callBack) {
        RestSchoolClass restSchoolClass = new RestSchoolClass();
        restSchoolClass.setRestContext(context);
        restSchoolClass.setDomSchoolClass(schoolClass);
		String id = PathId.getId(context);
		service.setActiveSchoolClass(id, restSchoolClass, new Callback<Boolean>(callBack));
        LOG.log(Level.FINE, "Rest Callback performed.");
    }


    public Promise<Boolean> removeSchoolClass(DomContext context, DomSchoolClass schoolClass) {
        PromiseCallback<Boolean> defer = new PromiseCallback<Boolean>();
        this.removeSchoolClass(context, schoolClass, defer);
        return defer.getPromise();
    }
    
    public void removeSchoolClass(DomContext context, DomSchoolClass schoolClass, AsyncCallback<Boolean> callBack) {
        RestSchoolClass restSchoolClass = new RestSchoolClass();
        restSchoolClass.setRestContext(context);
        restSchoolClass.setDomSchoolClass(schoolClass);
		String id = PathId.getId(context);
		service.removeSchoolClass(id, restSchoolClass, new Callback<Boolean>(callBack));
        LOG.log(Level.FINE, "Rest Callback performed.");
    }


    public Promise<List<DomSchoolClass>> getStudentsSchoolClasses(DomContext context) {
        PromiseCallback<List<DomSchoolClass>> defer = new PromiseCallback<List<DomSchoolClass>>();
        this.getStudentsSchoolClasses(context, defer);
        return defer.getPromise();
    }
    
    public void getStudentsSchoolClasses(DomContext context, AsyncCallback<List<DomSchoolClass>> callBack) {
		String id = PathId.getId(context);
		service.getStudentsSchoolClasses(id, new Callback<List<DomSchoolClass>>(callBack));
        LOG.log(Level.FINE, "Rest Callback performed.");
    }

    public Promise<Boolean> registerStudentForSchoolClass(DomContext context, DomNewSchoolClass4Student submit) {
        PromiseCallback<Boolean> defer = new PromiseCallback<Boolean>();
        this.registerStudentForSchoolClass(context, submit, defer);
        return defer.getPromise();
    }
    
    public void registerStudentForSchoolClass(DomContext context, DomNewSchoolClass4Student submit, AsyncCallback<Boolean> callBack) {
        RestNewSchoolClass4Student restData = new RestNewSchoolClass4Student();
        restData.setRestContext(context);
        restData.setDomNewSchoolClass4Student(submit);
        String id = PathId.getId(context);
        service.registerStudentForSchoolClass(id, restData, new Callback<Boolean>(callBack));
        LOG.log(Level.FINE, "Rest Callback performed.");
    }


    public Promise<List<DomSchoolClass>> getSchoolsClasses(DomContext context) {
        PromiseCallback<List<DomSchoolClass>> defer = new PromiseCallback<List<DomSchoolClass>>();
        this.getSchoolsClasses(context, defer);
        return defer.getPromise();
    }
    
    public void getSchoolsClasses(DomContext context, AsyncCallback<List<DomSchoolClass>> callBack) {
    	
        service.getSchoolsClasses(PathId.getId(context), new Callback<List<DomSchoolClass>>(callBack));
        LOG.log(Level.FINE, "Rest Callback performed.");
    }


    public Promise<DomSchoolClass> getActiveSchoolClass(DomContext context) {
        PromiseCallback<DomSchoolClass> defer = new PromiseCallback<DomSchoolClass>();
        this.getActiveSchoolClass(context, defer);
        return defer.getPromise();
    }
    
    public void getActiveSchoolClass(DomContext context, AsyncCallback<DomSchoolClass> callBack) {
        service.getActiveSchoolClass(PathId.getId(context), new Callback<DomSchoolClass>(callBack));
        LOG.log(Level.FINE, "Rest Callback performed.");
    }

}
