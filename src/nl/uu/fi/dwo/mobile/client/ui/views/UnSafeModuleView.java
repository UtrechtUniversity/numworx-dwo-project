package nl.uu.fi.dwo.mobile.client.ui.views;

import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;

import nl.uu.fi.dwo.mobile.client.text.Text;
import nl.uu.fi.dwo.mobile.client.ui.SelectModuleItem;
import nl.uu.fi.dwo.mobile.client.ui.places.TreeModulePlace;

public class UnSafeModuleView extends Composite {

	private static UnSafeModuleViewUiBinder uiBinder = GWT
			.create(UnSafeModuleViewUiBinder.class);

	@UiField HTML title;
	@UiField Text rb;
	@UiField TreeModuleViewNumworxCss style;
	HeaderView header;
	
	interface UnSafeModuleViewUiBinder extends
			UiBinder<Widget, UnSafeModuleView> {
	}

	@Inject public UnSafeModuleView(HeaderView headerView) {
		header = headerView;
		initWidget(uiBinder.createAndBindUi(this));
	}

	public void selectItem(SelectModuleItem item) {
		header.show();
		title.setText(item.getName());
		Object upId = item.getParentID();
		if (upId == null) 
			header.setUpPlace(header.getHomePlace());
		else
			header.setUpPlace(new TreeModulePlace(upId));
	}

}
