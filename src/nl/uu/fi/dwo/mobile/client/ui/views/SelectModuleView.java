package nl.uu.fi.dwo.mobile.client.ui.views;

import java.util.List;

import nl.uu.fi.dwo.mobile.client.ui.SelectModuleItem;

import com.google.gwt.user.client.ui.IsWidget;
import com.googlecode.mgwt.ui.client.widget.celllist.HasCellSelectedHandler;

/**
 * 
 * @author Danny Hendrix
 * 
 */
public interface SelectModuleView extends IsWidget
{
	public void render(List<SelectModuleItem> items);

	public HasCellSelectedHandler getList();
}
