package nl.uu.fi.dwo.mobile.client.ui.views;

import java.util.List;

import nl.uu.fi.dwo.mobile.client.ui.SelectModuleItem;

import com.google.gwt.user.client.ui.IsWidget;
import com.googlecode.mgwt.dom.client.event.tap.HasTapHandlers;
import com.googlecode.mgwt.ui.client.widget.celllist.HasCellSelectedHandler;

/**
 * 
 * @author Danny Hendrix
 * 
 */
public interface SelectModuleView extends IsWidget
{	
	public void render(List<SelectModuleItem> items);
	public void render(SelectModuleItem item);

	public List<SelectModuleItem> getItems();
	void setDescription(SelectModuleItem item);
	
	public HasTapHandlers getBackBtn();
	public HasCellSelectedHandler getList();

}
