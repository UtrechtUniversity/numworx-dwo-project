/*
 * Created on Feb 28, 2005
 *
 */
package fi.dwo.dwojapplet.gui;

import fi.dwo.rest.dom.entities.DomUserFull;
import fi.dwo.rest.dom.entities.DomSchoolClass;
import fi.dwo.rest.exceptions.Dwo2Exception;
import fi.dwo.commons.exceptions.LoginException;
import fi.dwo.commons.system.TextMapper;
import fi.dwo.dwojapplet.domain.DwoHelper;
import fi.dwo.dwojapplet.domain.rest.SecureStudentSchoolClassManager;
import java.awt.event.MouseEvent;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JDialog;

/**
 * A Linked Label that represents a SchoolClass.
 *
 * @author M.J.B. Kupers
 *
 */
public class DomSchoolClassLinkedLabel extends LinkedLabel {

    private static final Logger LOG = Logger.getLogger(DomSchoolClassLinkedLabel.class.getName());

    private DomSchoolClass schoolClass;

    /**
     * Creates a new Linked Label with the specified SchoolClass.
     *
     * @param c The SchoolClass of the label.
     */
    public DomSchoolClassLinkedLabel(DomSchoolClass c) {
        super("-  " + c.getSchoolClassName());
        schoolClass = c;
    }

    /**
     * Returns the SchoolClass of the Label.
     *
     * @return The SchoolClass of the Label.
     */
    public DomSchoolClass getSchoolClass() {
        return schoolClass;
    }

    /**
     * Sets the SchoolClass of the label.
     *
     * @param schoolClass The schoolClass to set.
     */
    public void setSchoolClass(DomSchoolClass schoolClass) {
        this.schoolClass = schoolClass;
        super.setText("-  " + schoolClass.getSchoolClassName());
    }

    protected void processMouseEvent(MouseEvent evt) {
        super.processMouseEvent(evt);
        if (evt.getID() == MouseEvent.MOUSE_CLICKED) {

            try {
                SecureStudentSchoolClassManager.setActiveSchoolClass(schoolClass);
//                tableModel.init(prop, loginImage, removeImage);
//                tableModel.fireTableDataChanged();
                //get user data
                DomUserFull user = DwoHelper.getCurrentUser();
                //switch role now
                LOG.log(Level.FINE, "switching class now");
                GuiCreator.instance().loginWithMd5(user.getUserName(), user.getPassword());
            }
            catch (LoginException ex) {
                LOG.log(Level.SEVERE, null, ex);
                GuiCreator.instance().ShowMessageDialog(this, TextMapper.getText(TextMapper.GUIW_ERR_LOGIN));
            }
            catch (Dwo2Exception ex) {
                LOG.log(Level.SEVERE, null, ex);
                GuiCreator.instance().ShowErrorDialog(this,ex);
            }
        }
    }
}
