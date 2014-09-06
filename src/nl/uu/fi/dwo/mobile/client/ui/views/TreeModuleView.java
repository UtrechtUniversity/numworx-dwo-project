package nl.uu.fi.dwo.mobile.client.ui.views;

import java.util.List;

import nl.uu.fi.dwo.mobile.client.ui.SelectModuleItem;

import com.google.gwt.event.logical.shared.HasSelectionHandlers;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.TreeItem;
import com.googlecode.mgwt.dom.client.event.tap.HasTapHandlers;
import com.googlecode.mgwt.ui.client.widget.celllist.HasCellSelectedHandler;

public interface TreeModuleView extends IsWidget
{
	
	
	void render(List<SelectModuleItem> currentModel);
	
	void selectModule(SelectModuleItem item);

	void close();
	void setPresenter(Presenter presenter);

	    public interface Presenter {
	        void goTo(Place place);
	    }

}
