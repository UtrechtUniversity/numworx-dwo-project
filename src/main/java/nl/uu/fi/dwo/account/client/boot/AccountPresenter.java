package nl.uu.fi.dwo.account.client.boot;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.Widget;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import nl.uu.fi.dwo.account.client.DwoGlobalVars;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentScoContext;
import nl.uu.fi.dwo.rest.dom.entities.DomUserFull;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
import org.osgi.util.promise.Failure;
import org.osgi.util.promise.Promise;
import org.osgi.util.promise.Success;

/**
 * Handler for for Login actions.
 *
 * @author Gert van der Plas
 */
public class AccountPresenter {

    private static final Logger LOG = Logger.getLogger(AccountPresenter.class.getName());
    private DwoGlobalVars dwoGlobalVars;
    private EventBus eventBus;
    private int selectedIndex = 0;
    private List<DomStudentScoContext> resultScoData;
    private AccountService accountService = new AccountService();

    private Display view;

    /**
     * @param view the view to set
     */
    public void setView(Display view) {
        this.view = view;
    }

    void goBackToResults() {
        eventBus.fireEvent(new SwitchViewEvent(SwitchViewEvent.SelectedView.RESULTS));
    }

    public interface Display {
        Widget asWidget();
        void clear();
        void init();
        void updateView(String username, String firstName, String insertion, String familyName);
    }

    AccountPresenter(EventBus anEventBus, DwoGlobalVars aDwoGlobalVars) {
        eventBus = anEventBus;
        dwoGlobalVars = aDwoGlobalVars;
    }

    public void init() {
        updateUserData();
    }
   public void updateUserData() {
        Promise<DomUserFull> userPromise;
        userPromise = accountService.getUserData();
        // onSuccess calculate results and show.
        userPromise.then(new Success<DomUserFull, Void>() {
            @Override
            public Promise<Void> call(Promise<DomUserFull> resolved) throws Exception {
                //calculate tree and call plotting
                LOG.log(Level.INFO, "DomFullUser data returned.");
                DomUserFull uf= resolved.getValue();
                view.updateView(uf.getUserName(), uf.getGivenName(), uf.getInsertion(), uf.getFamilyName());
                dwoGlobalVars.setCurrentUser(uf);//updating data
                return null;
            }
        },
                new Failure() {
            @Override
            public void fail(Promise<?> resolved) throws Exception {
                Throwable fail = resolved.getFailure();
                if (fail instanceof Dwo2Exception) {
                    //translate and display in gui
                } else {
                    //throw directly
                }
            }
        });
    }
    /**
     * @param row the course to set
     */
    public void selectRow(int row) {
        if (row != -1) {
            selectedIndex = row;
            return;
        }
    }

    private String[][] buildPlotData() {
        String[][] data = new String[resultScoData.size()+1][1];
//        data[0][0] = "School";//<div style=\"text-align: left; background-color: #aaaaaa; padding: 2px; overflow auto;\">School</div>";
//        int i = 1;
//        selectedIndex=0;
//        for (DomSchoolRoleAndClassV2 srac : resultScoData) {
//            data[i][0] = srac.getSchool().getSchoolName();
//            if (srac.getHasRole().getId().equals(srac.getHasRole().getId())) {
//                selectedIndex = i;
//            }
//            i++;
//        }
        return data;
    }
}
