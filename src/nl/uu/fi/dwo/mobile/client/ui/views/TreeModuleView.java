package nl.uu.fi.dwo.mobile.client.ui.views;

import java.util.List;

import nl.uu.fi.dwo.mobile.client.ui.SelectModuleItem;

import com.google.gwt.user.client.ui.IsWidget;
import com.googlecode.mgwt.ui.client.widget.HeaderButton;

public interface TreeModuleView extends IsWidget
{
	interface Presenter {
		void back();
		void selectItem(SelectModuleItem object);
	}
	
	
	void render(List<SelectModuleItem> currentModel);

	void selectModule(SelectModuleItem item);

	void setPresenter(Presenter presenter);

}
