package nl.uu.fi.dwo.mobile.client.ui.views;

import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;

import fi.dwo.gwt.lib.rest.util.PersistenceIdDecoderInterface;
import nl.uu.fi.dwo.mobile.client.text.Text;
import nl.uu.fi.dwo.mobile.client.ui.SelectModuleItem;
import nl.uu.fi.dwo.mobile.client.ui.places.Exam;
import nl.uu.fi.dwo.rest.persistence.PersistenceClassType;

public class UnSafeModuleView extends Composite {

	private static UnSafeModuleViewUiBinder uiBinder = GWT
			.create(UnSafeModuleViewUiBinder.class);

	@UiField HTML title;
	@UiField Text rb;
	@UiField TreeModuleViewNumworxCss style;
	HeaderView header;
	PlaceController controller;
	Object token;
	
	interface UnSafeModuleViewUiBinder extends
			UiBinder<Widget, UnSafeModuleView> {
	}

	@Inject public UnSafeModuleView(HeaderView headerView, PlaceController controller) {
		header = headerView;
		this.controller = controller;
		initWidget(uiBinder.createAndBindUi(this));
	}

	public void selectItem(SelectModuleItem item) {
		header.show();
		title.setText(item.getName());
		SelectModuleItem parent = item.getParent();
		if (parent == null) 
			header.setUpPlace(header.getHomePlace());
		else
			header.setUpPlace(parent.getPlace());

		token = PersistenceIdDecoderInterface.instance.idOf(item.getClassCourse().getId(), PersistenceClassType.PersistentClassCourse);
	}

	@UiHandler("anchor")
	void onAnchor(ClickEvent click) {
		controller.goTo(new Exam(token));
	}
}
