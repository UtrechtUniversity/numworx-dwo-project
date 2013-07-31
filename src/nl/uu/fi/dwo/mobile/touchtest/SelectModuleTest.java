package nl.uu.fi.dwo.mobile.touchtest;

import java.util.Collections;
import java.util.List;

import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Widget;
import com.googlecode.mgwt.dom.client.event.tap.HasTapHandlers;
import com.googlecode.mgwt.ui.client.widget.HeaderButton;
import com.googlecode.mgwt.ui.client.widget.HeaderPanel;
import com.googlecode.mgwt.ui.client.widget.LayoutPanel;
import com.googlecode.mgwt.ui.client.widget.celllist.CellSelectedHandler;
import com.googlecode.mgwt.ui.client.widget.celllist.HasCellSelectedHandler;

import nl.uu.fi.dwo.mobile.client.ui.SelectModuleItem;
import nl.uu.fi.dwo.mobile.client.ui.views.SelectModuleView;

public class SelectModuleTest implements SelectModuleView {

	private LayoutPanel main;
	private HeaderButton backbutton;
	
	
	@Override
	public Widget asWidget() {
		return main;
	}

	@Override
	public void render(List<SelectModuleItem> items) {
		// TODO Auto-generated method stub

	}

	@Override
	public void render(SelectModuleItem item) {
		// TODO Auto-generated method stub

	}

	@Override
	public List<SelectModuleItem> getItems() {
		
		return Collections.emptyList();
	}

	public SelectModuleTest() {

		main = new LayoutPanel();

		HeaderPanel header = new HeaderPanel();
		header.setCenter("Selecteer activiteit");
		main.add(header);

		backbutton = new HeaderButton();
		backbutton.setBackButton(true);
		backbutton.setText("Terug");

		

		header.setLeftWidget(backbutton);
		
	}


	@Override
	public void setDescription(SelectModuleItem item) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public HasTapHandlers getBackBtn() {
		return backbutton;
	}

	@Override
	public HasCellSelectedHandler getList() {
		return new HasCellSelectedHandler() {

			@Override
			public HandlerRegistration addCellSelectedHandler(
					CellSelectedHandler cellSelectedHandler) {
				return null;
			}};
	}

}
