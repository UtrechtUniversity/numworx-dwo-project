package nl.uu.fi.dwo.lms.gwtclient.gwt.graveyard;

import com.google.gwt.user.client.rpc.AsyncCallback;
import fi.dwo.gwt.lib.rest.CallManagers.SecuredUserSchoolLoginManager;
import fi.dwo.gwt.lib.rest.util.PromiseCallback;
import nl.uu.fi.dwo.rest.dom.entities.DomNewSchoolLogin;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolRoleAndClass;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolsRolesAndClasses;
import nl.uu.fi.dwo.rest.dom.entities.DomUserFull;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
import java.util.logging.Logger;
import nl.uu.fi.dwo.lms.gwtclient.gwt.DwoGlobalVars;

import org.osgi.util.promise.Promise;

/**
 *
 * @author Gert van der Plas
 */
public class SchoolLoginController {

    private static final Logger LOG = Logger.getLogger(SchoolLoginController.class.getName());

    private SchoolLoginPanel view = null;
    private SecuredUserSchoolLoginManager manager = new SecuredUserSchoolLoginManager();
    private DomSchoolsRolesAndClasses srcs;

    /**
     *
     * @return
     */
    public DomSchoolsRolesAndClasses getSrcs() {
        return srcs;
    }

    /**
     *
     * @param view
     * @param user
     * @throws Dwo2Exception
     */
    public SchoolLoginController(SchoolLoginPanel view, DomUserFull user) throws Dwo2Exception {
        this.view = view;
        this.init(user);
    }

    /**
     *
     * @param user
     * @throws Dwo2Exception
     */
    public void init(DomUserFull user) throws Dwo2Exception {
        manager.getSchoolLogins(new AsyncCallback<DomSchoolsRolesAndClasses>() {
            @Override
            public void onFailure(Throwable t) {
                view.init(DwoGlobalVars.instance().getCurrentUser());
            }

            @Override
            public void onSuccess(DomSchoolsRolesAndClasses result) {
                //success and set all the data in the view
                srcs = result;
                view.update(srcs);
            }
        }
        );
    }
    
    /**
     *
     * @param callBack
     */
    public void getSchoolLogins(AsyncCallback<DomSchoolsRolesAndClasses> callBack) {
        manager.getSchoolLogins(callBack);
    }
    
    /**
     *
     * @param sc
     * @return
     */
    public Promise<DomSchoolRoleAndClass> switchToSchoolLogin(DomSchoolRoleAndClass sc) {
    	PromiseCallback<DomSchoolRoleAndClass> cb = new PromiseCallback<DomSchoolRoleAndClass>();
    	manager.switchToSchoolLogin(sc, cb);
    	return cb.getPromise();
    	
    }

    /**
     *
     * @param reqSrac
     * @return
     */
    public Promise<Boolean> removeASchoolLogin(DomSchoolRoleAndClass reqSrac) {
    	PromiseCallback<Boolean> cb = new PromiseCallback<Boolean>();
        manager.removeASchoolLogin(reqSrac, cb);
        return cb.getPromise();
    }

    /**
     *
     * @param reqSrac
     * @param callBack
     */
    public void addASchoolLogin(DomNewSchoolLogin reqSrac, AsyncCallback<Boolean> callBack){
        manager.addASchoolLogin(reqSrac, callBack);
    }
}
