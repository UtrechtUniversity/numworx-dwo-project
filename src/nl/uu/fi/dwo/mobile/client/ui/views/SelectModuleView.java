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
	interface Presenter {
		void selectItem(SelectModuleItem item);
		void back();
	}
	
	public void render(List<SelectModuleItem> items);
	public void render(SelectModuleItem item);

	public List<SelectModuleItem> getItems();
	
	void setPresenter(Presenter presenter);
	void setDescription(SelectModuleItem item);
}
