package nl.uu.fi.dwo.account.client.boot;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiRenderer;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import java.util.logging.Level;
import java.util.logging.Logger;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolRoleAndClassV2;

/**
 * GWT Panel that handles the login-authentication.
 *
 * @author G.A.J. van der Plas
 */
public class SwitchSchoolPanel extends Composite implements ClickHandler {

    private static final Logger LOG = Logger.getLogger(SwitchSchoolPanel.class.getName());

    interface MyUiBinder extends UiBinder<Widget, SwitchSchoolPanel> {
    }
    private static final MyUiBinder uiBinder = GWT.create(MyUiBinder.class);
   
    private SwitchSchoolPanelHandler handler;

    @UiField
    FlexTable flexTable;
    @UiField
    Button cancelBtn;
    @UiField
    Button switchBtn;
    int selectedRow = 0;

    private BootPanel parent;

    public void setParent(BootPanel aParent) {
        parent = aParent;
    }

    /**
     * @return the parent
     */
    public BootPanel getParent() {
        return parent;
    }

    public SwitchSchoolPanel() {
        initWidget(uiBinder.createAndBindUi(this));
        handler = new SwitchSchoolPanelHandler(this);
        //controller must be before clicks occur
        switchBtn.addClickHandler(this);
    }

    public void init() {
        flexTable.setWidget(0, 0, new Label("Schoolnaam"));
        flexTable.getCellFormatter().addStyleName(0,0,"flexTableHeader");
        int i = 1;
        for (DomSchoolRoleAndClassV2 role : handler.getTeacherRoles()) {
            flexTable.setWidget(i, 0, new Label(role.getSchool().getSchoolName()));
            if(i%2==0){
//               flexTable.getCellFormatter().addStyleName(i,0,"flexTableOddRow");                
            }else{
//               flexTable.getCellFormatter().addStyleName(i,0,"flexTableEvenRow");                                
            }
            flexTable.addClickHandler(new ClickHandler() {
                public void onClick(ClickEvent event) {
                    int curRow = selectedRow;
                    selectedRow = flexTable.getCellForEvent(event).getRowIndex();
                    flexTable.getRowFormatter().getElement(selectedRow).addClassName("flexTableSelectedBackground");
                    if (curRow != selectedRow) {
                        flexTable.getRowFormatter().getElement(curRow).removeClassName("flexTableSelectedBackground");
                    }
                    LOG.log(Level.INFO, "" + selectedRow);
                }
            });
            i++;
        }
//        cancelBtn.getElement().getStyle().setVisibility(Style.Visibility.VISIBLE);
        switchBtn.getElement().getStyle().setVisibility(Style.Visibility.VISIBLE);
    }

    public void onClick(ClickEvent event) {
        if (event.getSource() == switchBtn) {
            parent.getSchoolName().setText(handler.getTeacherRoles().get(selectedRow).getSchool().getSchoolName());
            handler.switchSchool();//handler.getTeacherRoles().get(selectedRow)
            parent.showResultWidget();
        }
    }
//
//    /**
//     * Called from handler after successful login.
//     */
//    public void onLoginSuccess() {
//        LOG.log(Level.INFO, "Login succeeded.");
//        parent.showResultWidget();
//    }
//
//    /**
//     * Called from handler after failed login.
//     *
//     * @param failMessage
//     */
//    public void onLoginFailure(String failMessage) {
//        LOG.log(Level.INFO, failMessage);
//        Window.alert(failMessage);
//        //reset user interface?
//    }

    public void updateView() {
        //fetch and display all the schoollogins that have teachers.
//        teacherRole.setText(
//                DwoGlobalVars.instance().getSchoolLogins().getActiveSchoolRoleAndClass().getRole().getRoleName()
//        );
        handler.init();
    }

}
