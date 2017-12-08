package nl.uu.fi.dwo.mobile.client.ui.views;

import com.google.gwt.core.client.GWT;
import com.google.gwt.place.shared.Place;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;

import nl.uu.fi.dwo.account.client.DwoGlobalVars;
import nl.uu.fi.dwo.mobile.DWOplayer;
import nl.uu.fi.dwo.mobile.client.text.Text;
import nl.uu.fi.dwo.mobile.client.ui.SelectModuleItem;
import nl.uu.fi.dwo.mobile.client.ui.places.TreeModulePlace;

public class UnSafeModuleView extends Composite implements GotoController {

	private static UnSafeModuleViewUiBinder uiBinder = GWT
			.create(UnSafeModuleViewUiBinder.class);

	@UiField HTML title;
	@UiField Text rb;
	@UiField TreeModuleViewNumworxCss style;
	@UiField HeaderView header;
	
	interface UnSafeModuleViewUiBinder extends
			UiBinder<Widget, UnSafeModuleView> {
	}

	public UnSafeModuleView() {
		initWidget(uiBinder.createAndBindUi(this));
		header.setPresenter(this);
	}

	public void selectItem(SelectModuleItem item) {
		header.setUserAndRole(DwoGlobalVars.instance().getCurrentUser(), DWOplayer.clientfactory.getRoleType());
		title.setText(item.getName());
		Object upId = item.getParentID();
		if (upId == null) upId = "0"; // wrong place?
		header.setUpPlace(new TreeModulePlace(upId));
	}

	public void goTo(Place place) {
		DWOplayer.clientfactory.getPlaceController().goTo(place);
	}

}
