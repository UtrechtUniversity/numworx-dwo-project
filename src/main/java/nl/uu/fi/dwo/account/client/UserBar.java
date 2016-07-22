package nl.uu.fi.dwo.account.client;

import java.util.MissingResourceException;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;

import fi.dwo.rest.dom.entities.DomUser;
import fi.dwo.rest.dom.entities.RoleType;
import fi.dwo.rest.locale.DwoLocalesForGWT;

public class UserBar extends Composite implements Command {

    private final MenuBar top = new MenuBar();;
    private final MenuBar items = new MenuBar(true);
    private final ProfileCommand profileCmd = new ProfileCommand();
    private final SchoolLoginCommand schoolLoginCmd = new SchoolLoginCommand(this);
    private final SchoolClassStudentCommand schoolClassCmd = new SchoolClassStudentCommand(this);
	private MenuItem itemSchoolClass;
	private RoleType role;
	private String display;
	private MenuItem status;
	private MenuItem itemSchoolLogin;
	
    public UserBar() {
        init();
    }

    private void init() {
        initWidget(top);

        MenuItem item;
        final int correctie = 10; // width popup 
        item = new MenuItem("<i class='fa fa-navicon fa-2x'></i>", true, items) {
            @Override
            public int getAbsoluteLeft() {
                int w1 = items.getOffsetWidth();
                int w2 = this.getOffsetWidth();
                return super.getAbsoluteLeft() - w1 + w2 - correctie;
            }

        };

        top.addItem(item);

        status = new MenuItem("this is a status", new Command(){

			@Override
			public void execute() {
				// TODO Auto-generated method stub
				
			}});
        status.setEnabled(false); status.removeStyleDependentName("disabled");
        
        items.addItem(status);
        items.addSeparator();
        
        
        item = new MenuItem(DwoLocalesForGWT.instance.GUI_MyProfile(), profileCmd);
        items.addItem(item);

        itemSchoolLogin = new MenuItem(DwoLocalesForGWT.instance.GUI_MySchoolLogins(), schoolLoginCmd);
        items.addItem(itemSchoolLogin);

        itemSchoolClass = new MenuItem(DwoLocalesForGWT.instance.GUI_MySchoolClasses(), schoolClassCmd);
        items.addItem(itemSchoolClass);
    }

	@Override
	public void execute() {
        Window.alert("wim calls a new login here.");
	}
	
	public void setResetLogin(Command resetLogin) {
		if(resetLogin == null) resetLogin = this; // NEVER NULL
		schoolClassCmd.setResetLogin(resetLogin);
		schoolLoginCmd.setResetLogin(resetLogin);
	}

	/**
	 *  set RoleType dependent options
	 * @param role RoleType
	 */
	public void setRole(RoleType role) {
		this.role = role;
		switch(role) {
			default: 
				itemSchoolClass.setEnabled(false);
				itemSchoolLogin.setEnabled(true);
				break;
			case STUDENT:
				itemSchoolClass.setEnabled(true);
				break;
		}
		setStatus();
	}
	
	private void setStatus() {
		String rolename = role.name();
		try {
			rolename = DwoLocalesForGWT.instance.getString(rolename);
		} catch (MissingResourceException e) {
		}
		display = DwoGlobalVars.getInstance().getCurrentUser().getDisplayName();

		status.setText(rolename + " " + display);
	}

	public void setSingleSchool(Boolean singleSchool) {
		itemSchoolLogin.setEnabled(!Boolean.TRUE.equals(singleSchool));
	}
}
