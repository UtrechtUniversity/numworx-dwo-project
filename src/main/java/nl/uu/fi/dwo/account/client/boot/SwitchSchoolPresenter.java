package nl.uu.fi.dwo.account.client.boot;

import com.google.gwt.event.shared.EventBus;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import nl.uu.fi.dwo.account.client.DwoGlobalVars;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolRoleAndClassV2;

/**
 * Handler for for Login actions.
 *
 * @author Gert van der Plas
 */
public class SwitchSchoolPresenter {

    private static final Logger LOG = Logger.getLogger(SwitchSchoolPresenter.class.getName());
    private DwoGlobalVars dwoGlobalVars;
    private EventBus eventBus;

    private SwitchSchoolView view;

    SwitchSchoolPresenter(EventBus anEventBus,DwoGlobalVars aDwoGlobalVars) {
        eventBus = anEventBus;
        dwoGlobalVars = aDwoGlobalVars;
        init();
    }

    public List<DomSchoolRoleAndClassV2> getTeacherRoles(){
        List<DomSchoolRoleAndClassV2> result =new ArrayList<DomSchoolRoleAndClassV2>();
        List<DomSchoolRoleAndClassV2>  fullList = DwoGlobalVars.instance().getSchoolLogins().getSchoolsRolesAndClassesList();
        for(DomSchoolRoleAndClassV2 hasRole : fullList){
            if(hasRole.getRole().getRoleName().equals("TEACHER")){
                result.add(hasRole);
            }
        }
        return result;
    }
    
    public void init() {

    }

    public void switchSchool() {
//        Promise<DwoGlobalVars.DwoGlobalVarsState> loginUser;
//        try {
//            loginUser = DwoGlobalVars.instance().initUser(user, password);
//            loginUser.then(new Success<DwoGlobalVars.DwoGlobalVarsState, Void>() {
//                @Override
//                public Promise<Void> call(Promise<DwoGlobalVars.DwoGlobalVarsState> resolved) throws Exception {
//                    if (resolved.getValue() == DwoGlobalVars.DwoGlobalVarsState.LoggedIn) {
//                        LOG.log(Level.INFO, "login succeeded for user:" + DwoGlobalVars.instance().getCurrentUser().getUniqueDisplayName());
//                        view.onLoginSuccess();
//                    } else {
//                        view.onLoginFailure("Illegal credentials.");
//                    }
//                    return null;
//                }
//            },
//                    new Failure() {
//                @Override
//                public void fail(Promise<?> resolved) throws Exception {
//                    view.onLoginFailure(resolved.getFailure().getMessage());
//                }
//            }
//            ).onResolve(new Runnable() {
//                public void run() {
//                    System.out.println("Need tot test onResolve and fill data here! Calling stuff to get results promise here!");
//                }
//            });;
//        } catch (Dwo2Exception ex) {
//            Logger.getLogger(SwitchSchoolPresenter.class.getName()).log(Level.SEVERE, null, ex);
//        }
    }

}
