package nl.uu.fi.dwo.mobile.client.ui.views;

import nl.uu.fi.dwo.account.client.DwoGlobalVars;
import nl.uu.fi.dwo.mobile.DWOplayer;
import nl.uu.fi.dwo.mobile.client.text.Text;
import nl.uu.fi.dwo.mobile.client.ui.SelectModuleItem;
import nl.uu.fi.dwo.mobile.client.ui.places.LoginPlace;
import nl.uu.fi.dwo.mobile.client.ui.places.TreeModulePlace;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.place.shared.Place;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.Widget;

public class UnSafeModuleView extends Composite {

	private static UnSafeModuleViewUiBinder uiBinder = GWT
			.create(UnSafeModuleViewUiBinder.class);

	@UiField HTML title;
	@UiField(provided=true) String pfx;
	@UiField HasText loginLabel;
	@UiField(provided=true) MenuItem user;
	private MenuBar items = new MenuBar(true);
	@UiField Text rb;
	@UiField TreeModuleViewNumworxCss style;


	private Object upId;
	
	interface UnSafeModuleViewUiBinder extends
			UiBinder<Widget, UnSafeModuleView> {
	}

	public UnSafeModuleView() {
		pfx=DWOplayer.PARAMETERS.getResource("");
        final int correctie = 10; // width popup 
		user = new MenuItem("<i class='fa fa-caret-down fa-2x'></i>", true, items) {
            @Override
            public int getAbsoluteLeft() {
                int w1 = items.getOffsetWidth();
                int w2 = this.getOffsetWidth();
                return super.getAbsoluteLeft() - w1 + w2 - correctie;
            }
		};
		items.clearItems();
		initWidget(uiBinder.createAndBindUi(this));
		items.addItem(rb.logout(), new ScheduledCommand() {
			
			@Override
			public void execute() {
				goTo(new LoginPlace());					
			}
		}).addStyleName(style.menuItem());		
	}

	public void selectItem(SelectModuleItem item) {
		String login = DWOplayer.withUser()? DwoGlobalVars.instance().getCurrentUser().getDisplayName() : "GUEST";
		loginLabel.setText(login);
		title.setText(item.getName());
		upId = item.getParentID();
		
	}

	@UiHandler("homeBtn")
	void onHomeBtn(ClickEvent ev) {
		goTo(new TreeModulePlace());
	}

	private void goTo(Place place) {
		DWOplayer.clientfactory.getPlaceController().goTo(place);
	}

	@UiHandler("upBtn")
	void onUpBtn(ClickEvent ev) {
		Object parent = upId;
		if (parent == null) parent = "0"; // wrong place?
		goTo(new TreeModulePlace(parent));
	}
}
