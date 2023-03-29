package fi.dwo.dwojapplet.gui;

import nl.uu.fi.dwo.lms.jclient.lib.rest.managers.SchoolManager;
import nl.uu.fi.dwo.lms.jclient.lib.rest.managers.SecureSchoolAdminSchoolManager;
import nl.uu.fi.dwo.lms.jclient.lib.rest.managers.SecureSchoolAdminStudentModelManager;
import nl.uu.fi.dwo.lms.jclient.lib.rest.managers.SecureStudentModelManager;
import nl.uu.fi.dwo.lms.jclient.lib.rest.managers.SecureTeacherStudentModelManager;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
import nl.uu.fi.dwo.rest.exceptions.Dwo2ExceptionCode;
import nl.uu.fi.dwo.rest.exceptions.Dwo2RestException;
import fi.dwo.dwojapplet.domain.Course;
import fi.dwo.dwojapplet.domain.DWO;
import fi.dwo.dwojapplet.domain.SchoolAdmin;
import fi.dwo.dwojapplet.domain.User;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JComponent;

public class GuiCreatorSchoolAdmin extends GuiCreatorTeacher {

    private static final Logger LOG = Logger.getLogger(GuiCreatorSchoolAdmin.class.getName());

    public GuiCreatorSchoolAdmin(DWO dwo) {
        super(dwo);
    }

    @Override
    public SchoolManager getSchoolManager() {
      return new SecureSchoolAdminSchoolManager();
    }

    /* (non-Javadoc)
     * @see fi.dwo.client.gui.GuiCreatorTeacher#getMenuPanel()
     */
    @Override
    public GuestMenuPanel getMenuPanel() {
        User u = dwo.getUser();

        if (u instanceof SchoolAdmin) {
            return new SchoolAdminMenuPanel();
        }
        return super.getMenuPanel();
    }

    /* (non-Javadoc)
     * @see fi.dwo.client.gui.GuiCreator#getUserManagementPanel()
     */
    @Override
    public CenterSubPanel getUserManagementPanel() {
        //return new UserManagementPanel(dwo);
        CenterSubPanel csp = null;
        try {
            csp = new UsersInSchoolSchoolAdminPanel();
        }
        catch (Dwo2Exception ex) {
            LOG.log(Level.SEVERE, "", ex);
            GuiCreator.instance().ShowErrorDialog(mainPanel, ex);
        }
        return csp;
    }

    @Override
    public CenterSubPanel getClassAdminPanel() {
//        return new ClassAdminPanel(dwo);
        CenterSubPanel csp = null;
        try {
            csp = new SchoolClassesSchoolAdminPanel();
        }
        catch (Dwo2Exception ex) {
            LOG.log(Level.SEVERE, "", ex);
            GuiCreator.instance().ShowErrorDialog(mainPanel, ex);
        }
        return csp;
    }

    @Override
    public JComponent fx(Object o, JComponent b) {
      if (!CenterPanel.isIconizer()) {
          return null;
      }
      Box box = Box.createVerticalBox();
      box.add(Box.createGlue());
      box.add(b);
      box.setBorder(BorderFactory.createEmptyBorder(0, 0, 3, 0)); // Meten!
      return box;
    }
    
    /* (non-Javadoc)
     * @see fi.dwo.dwojapplet.gui.GuiCreatorTeacher#getResultPanel(fi.dwo.dwojapplet.domain.Course)
     */
    @Override
    public CenterSubPanel getResultPanel(Course c) {
      Dwo2Exception ex = new Dwo2Exception(Dwo2ExceptionCode.User_AuthorizationError, "");
      GuiCreator.instance().ShowErrorDialog(mainPanel, ex);
      throw new RuntimeException(ex);
    }

    /* Schooladmin altijd schrijfbaar, profileadmin is er al uit gefilterd
     * @see fi.dwo.dwojapplet.gui.GuiCreatorTeacher#canWrite(fi.dwo.dwojapplet.domain.Course)
     */
    @Override
    public boolean canWrite(Course c) {
      return true;
    }
    
    @Override
    public SecureStudentModelManager getStudentModelManager() {
      if (studentModelManager == null) studentModelManager = new SecureSchoolAdminStudentModelManager();
      return studentModelManager;
    }
}
