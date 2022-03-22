package nl.uu.fi.dwo.account.client;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.osgi.util.promise.Failure;

import fi.dwo.gwt.lib.rest.CallManagers.SecuredUserAccountManager;
import nl.uu.fi.dwo.rest.dom.entities.DomUserFull;

/**
 *
 * @author Gert van der Plas
 */
class ProfileController {

  private static final Logger LOG = Logger.getLogger(ProfileController.class.getName());

  private ProfilePanel view = null;
  private DomUserFull currentUser = null;
  private DomUserFull updateUser = null;
  private SecuredUserAccountManager manager = new SecuredUserAccountManager();
  private Failure fail;
  private DwoGlobalVars vars;

  /**
   *
   * @param view
   * @param user
   * @param fail 
   */
  ProfileController(ProfilePanel view, DwoGlobalVars vars, Failure fail) {
    this.view = view;
    this.fail = fail;
    this.vars = vars;
    this.init(vars.getCurrentUser());
  }

  /**
   *
   * @param user
   */
  public void init(DomUserFull user) {
    currentUser = user;
    updateUser = currentUser.duplicate();
  }

  /**
   * Update the currentUser.
   *
   */
  public void callUpdate() {
    LOG.log(Level.INFO, "Calling REST-interface login.");
    manager.updateAccountData(vars.getContext(), updateUser).then(p -> {
      DomUserFull result = p.getValue();
      LOG.log(Level.INFO, "update was succesful.");
      currentUser = result;
      updateUser = currentUser.duplicate();
      // update Globals otherwise can't loginUser in passwd change!
      vars.setCurrentUser(currentUser);
      // update rest authentication done by setcurrentuser
      view.init(currentUser);
      view.getPopup().hide();
      return null;
    }, fail
    );

  }

  /**
   * @return the currentUser
   */
  public DomUserFull getCurrentUser() {
    return currentUser;
  }

  /**
   * @param aCurrentUser
   */
  public void setCurrentUser(DomUserFull aCurrentUser) {
    currentUser = aCurrentUser;
  }

  /**
   * @return the updateUser
   */
  public DomUserFull getUpdateUser() {
    return updateUser;
  }

  /**
   * @param updateUser the updateUser to set
   */
  public void setUpdateUser(DomUserFull updateUser) {
    this.updateUser = updateUser;
  }
}
