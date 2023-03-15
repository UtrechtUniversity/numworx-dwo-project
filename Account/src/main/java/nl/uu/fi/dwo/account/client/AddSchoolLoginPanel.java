package nl.uu.fi.dwo.account.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.PopupPanel.PositionCallback;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

import nl.uu.fi.dwo.rest.dom.entities.DomNewSchoolLogin;
import nl.uu.fi.dwo.rest.dom.entities.RoleType;
import nl.uu.fi.dwo.rest.locale.DwoLocalesForGWT;

public class AddSchoolLoginPanel extends Composite implements ClickHandler {

	private static AddSchoolLoginPanelUiBinder uiBinder = GWT.create(AddSchoolLoginPanelUiBinder.class);

	interface AddSchoolLoginPanelUiBinder extends UiBinder<Widget, AddSchoolLoginPanel> {
	}

	static RoleType[] roles = { RoleType.STUDENT, RoleType.TEACHER, RoleType.SCHOOLADMIN, RoleType.ADMIN };
	private void construct() {
		initWidget(uiBinder.createAndBindUi(this));
		for(String type: roleStrs) {
			roleBox.addItem(type);
		}
	}

	@UiField
	Button ok, cancel;
	@UiField TextBox schoolLoginBox;
	@UiField PasswordTextBox passwordBox;
	@UiField ListBox roleBox;

	@UiField(provided=true) DwoLocalesForGWT rb = DwoLocalesForGWT.instance;
	String[] roleStrs = {rb.STUDENT(), rb.TEACHER(), rb.SCHOOLADMIN(), rb.ADMIN() };
	
	final SchoolLoginController control;
	final PopupPanel popup;
	
	public void show() {
		popup.setPopupPositionAndShow(new PositionCallback() {
			
			@Override
			public void setPosition(int offsetWidth, int offsetHeight) {
                int left = (Window.getClientWidth() - offsetWidth) / 2;
                int top = (Window.getClientHeight() - offsetHeight) / 2;
                popup.setPopupPosition(left, top);
			}
		});
	}

	public void hide() {
		popup.hide();
	}
	
	public AddSchoolLoginPanel(SchoolLoginController control, PopupPanel popup) {
		this.control = control;
		this.popup = popup;
		//initWidget(createWidget());
		construct();
		
		control.setAddSchoolLoginPanel(this);
		popup.setWidget(this);
	}

	private Widget createWidget() {
		// VERY simple widget
		FlowPanel flow = new FlowPanel();
		
		flow.add(new Label("SchoolLogin"));
		schoolLoginBox = new TextBox();
		flow.add(schoolLoginBox);
		
		flow.add(new Label("Rol"));
		roleBox = new ListBox();
		roleBox.setMultipleSelect(false);
		for(RoleType type: RoleType.values()) {
			roleBox.addItem(type.name());
		}
		flow.add(roleBox);
		
		flow.add(new Label("schoolCode"));
		passwordBox = new PasswordTextBox();
		flow.add(passwordBox);
		
		ok = new Button("OK");
		ok.addClickHandler(this);
		flow.add(ok);
		
		cancel = new Button("Cancel");
		cancel.addClickHandler(this);
		flow.add(cancel);
		
		return flow;
	}

	@Override
	@UiHandler({"ok","cancel"})
	public void onClick(ClickEvent event) {
		Object source = event.getSource();
		if(source == ok) {
			ok.setEnabled(false);
			cancel.setEnabled(false);
			String schoolLogin = schoolLoginBox.getText();
			String password = passwordBox.getText();
			int index = roleBox.getSelectedIndex();
			DomNewSchoolLogin dom = new DomNewSchoolLogin();
			dom.setRole(roles[index]);
			dom.setSchoolCode(password);
			dom.setSchoolLogin(schoolLogin);
			control.addASchoolLogin(dom);
		} else 
		if (source == cancel) {
			popup.hide();
		}
		
		
	}

	void enable() {
		ok.setEnabled(true);
		cancel.setEnabled(true);
	}

}
