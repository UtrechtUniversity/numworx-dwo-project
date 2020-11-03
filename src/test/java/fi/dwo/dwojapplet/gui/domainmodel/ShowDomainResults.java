package fi.dwo.dwojapplet.gui.domainmodel;

import java.net.URL;
import java.util.List;

import javax.swing.JFrame;

import fi.dwo.commons.system.MD5;
import fi.dwo.dwojapplet.domain.DwoHelper;
import nl.uu.fi.dwo.lms.jclient.lib.rest.managers.LoginManager;
import nl.uu.fi.dwo.lms.jclient.lib.rest.managers.SecureTeacherSchoolClassManager;
import nl.uu.fi.dwo.lms.jclient.lib.rest.managers.SecureTeacherStudentModelManager;
import nl.uu.fi.dwo.lms.jclient.lib.rest.managers.SecureUserAccountLoginsManager;
import nl.uu.fi.dwo.lms.jclient.lib.rest.transport.RestAuthenticator;
import nl.uu.fi.dwo.rest.dom.entities.DomContext;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContext;
import nl.uu.fi.dwo.rest.dom.entities.DomUserFullwLoginContext;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;

public class ShowDomainResults extends JFrame {

  private LeerdomeinResultsPanel2 panel;

  public ShowDomainResults() {
    setDefaultCloseOperation(EXIT_ON_CLOSE);
    panel = new LeerdomeinResultsPanel2();
    setContentPane(panel);
    pack();
  }

  public static void main(String[] args) throws Exception {
    if (args.length != 2) {
      System.err.println("showdomainresults user password");
      System.exit(1);
      return;
    }
    
    String username = args[0];
    String password = args[1];
    DwoHelper.setServerUrlPath(new URL("https://test.dwo.nl/dwo/"));
    DomUserFullwLoginContext user = LoginManager.basicLogin(username, MD5.getHashString(String.valueOf(password)));
    DwoHelper.setSchoolLogins(SecureUserAccountLoginsManager.getSchoolLogins());
    DomContext context = new DomContext();
    context.setDomHasRole(DwoHelper.getSchoolLogins().getActiveSchoolRoleAndClass().getHasRole());
    RestAuthenticator.getInstance().setContext(context);
    
//    DwoHelper.setCurrentUser(user.getDomUserFull(),user.getDomLoginContext());

    List<DomStudentModelContext> list = SecureTeacherStudentModelManager.getList();
    List<DomSchoolClass> classes = SecureTeacherSchoolClassManager.getTeachersSchoolClasses();
    int i = 0;
    ShowDomainResults main = new ShowDomainResults();
    main.setContext(list.get(i));
    main.setClasses(classes);
    main.show();

  }

  private void setClasses(List<DomSchoolClass> classes) {
    panel.setClasses(classes);
    
  }

  private void setContext(DomStudentModelContext domStudentModelContext) {
    panel.setContext(domStudentModelContext);
  }

}
