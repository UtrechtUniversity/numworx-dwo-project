package nl.uu.fi.dwo.mobile.client.ui.views;

import java.util.List;
import com.googlecode.mgwt.dom.client.event.tap.HasTapHandlers;
import com.googlecode.mgwt.ui.client.widget.celllist.HasCellSelectedHandler;

import nl.uu.fi.dwo.mobile.client.ui.SelectModuleItem;

import com.google.gwt.event.logical.shared.HasSelectionHandlers;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.TreeItem;

public interface TreeModuleView extends IsWidget
{
	
	HasTapHandlers getBackBtn();
	
	void render(List<SelectModuleItem> currentModel);

	void selectModule(SelectModuleItem item);

	HasSelectionHandlers<TreeItem> getTree();

	HasCellSelectedHandler getCells();
	List<SelectModuleItem> getCellItems();

}
