package fi.dwo.gwt.lib.rest.CallManagers;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import fi.dwo.gwt.lib.rest.GwtRestVars;
import fi.dwo.gwt.lib.rest.client.SecuredStudentSchoolClassRestCaller;
import fi.dwo.rest.dom.entities.DomContext;
import fi.dwo.rest.dom.entities.DomNewSchoolClass4Student;
import fi.dwo.rest.dom.entities.DomSchoolClass;
import fi.dwo.rest.entities.RestNewSchoolClass4Student;
import fi.dwo.rest.entities.RestSchoolClass;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

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


    public void setActiveSchoolClass(DomSchoolClass schoolClass, AsyncCallback<Boolean> callBack) {
        RestSchoolClass restSchoolClass = new RestSchoolClass();
        restSchoolClass.setRestContext(new DomContext());
        restSchoolClass.setDomSchoolClass(schoolClass);
        service.setActiveSchoolClass(restSchoolClass, new Callback<Boolean>(callBack));
        LOG.log(Level.FINE, "Rest Callback performed.");
    }

    public void removeSchoolClass(DomSchoolClass schoolClass, AsyncCallback<Boolean> callBack) {
        RestSchoolClass restSchoolClass = new RestSchoolClass();
        restSchoolClass.setRestContext(new DomContext());
        restSchoolClass.setDomSchoolClass(schoolClass);
        service.removeSchoolClass(restSchoolClass, new Callback<Boolean>(callBack));
        LOG.log(Level.FINE, "Rest Callback performed.");
    }

    public void getStudentsSchoolClasses(AsyncCallback<List<DomSchoolClass>> callBack) {
        service.getStudentsSchoolClasses(new Callback<List<DomSchoolClass>>(callBack));
        LOG.log(Level.FINE, "Rest Callback performed.");
    }

    public void registerStudentForSchoolClass(DomNewSchoolClass4Student submit, AsyncCallback<Boolean> callBack) {
        RestNewSchoolClass4Student restData = new RestNewSchoolClass4Student();
        restData.setRestContext(new DomContext());
        restData.setDomNewSchoolClass4Student(submit);
        service.registerStudentForSchoolClass(restData, new Callback<Boolean>(callBack));
        LOG.log(Level.FINE, "Rest Callback performed.");
    }

    public void getSchoolsClasses(AsyncCallback<List<DomSchoolClass>> callBack) {
        service.getSchoolsClasses(new Callback<List<DomSchoolClass>>(callBack));
        LOG.log(Level.FINE, "Rest Callback performed.");
    }

    public void getActiveSchoolClass(AsyncCallback<DomSchoolClass> callBack) {
        service.getActiveSchoolClass(new Callback<DomSchoolClass>(callBack));
        LOG.log(Level.FINE, "Rest Callback performed.");
    }

}
