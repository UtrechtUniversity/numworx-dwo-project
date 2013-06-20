package nl.uu.fi.dwo.mobile.touchtest;

import java.util.Collections;
import java.util.List;

import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.Widget;
import com.googlecode.mgwt.dom.client.event.tap.TapEvent;
import com.googlecode.mgwt.dom.client.event.tap.TapHandler;
import com.googlecode.mgwt.dom.client.event.touch.TouchEndEvent;
import com.googlecode.mgwt.dom.client.event.touch.TouchEndHandler;
import com.googlecode.mgwt.ui.client.widget.Button;
import com.googlecode.mgwt.ui.client.widget.HeaderButton;
import com.googlecode.mgwt.ui.client.widget.HeaderPanel;
import com.googlecode.mgwt.ui.client.widget.LayoutPanel;
import com.googlecode.mgwt.ui.client.widget.celllist.HasCellSelectedHandler;

import nl.uu.fi.dwo.mobile.client.ui.SelectModuleItem;
import nl.uu.fi.dwo.mobile.client.ui.views.SelectModuleView;
import nl.uu.fi.dwo.mobile.client.ui.views.SelectModuleView.Presenter;

public class SelectModuleTest implements SelectModuleView {

	private LayoutPanel main;
	private HeaderButton backbutton;
	private Presenter presenter;
	
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

		backbutton.addTapHandler(new TapHandler()
		{

			@Override
			public void onTap(TapEvent event)
			{
				presenter.back();
			}
		});

		header.setLeftWidget(backbutton);
		
		Button btn = new Button("DEZE DAN");
		btn.setWidth("276px");
		btn.addTouchEndHandler(new TouchEndHandler()
		{
			@Override
			public void onTouchEnd(TouchEndEvent event)
			{
				presenter.back();
			}
		});
		main.add(btn);
	}

	@Override
	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
		
	}

	@Override
	public void setDescription(SelectModuleItem item) {
		// TODO Auto-generated method stub
		
	}

}
