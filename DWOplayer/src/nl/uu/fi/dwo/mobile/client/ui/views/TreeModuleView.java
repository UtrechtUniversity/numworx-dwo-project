package nl.uu.fi.dwo.mobile.client.ui.views;

import java.util.List;

import com.google.gwt.user.client.ui.IsWidget;

import nl.uu.fi.dwo.mobile.client.ui.SelectModuleItem;

public interface TreeModuleView extends IsWidget
{
	
	
	void render(List<SelectModuleItem> currentModel);
	//void setSortModel( Comparator<SelectModuleItem> sorter);
	void selectModule(SelectModuleItem item);

	void close();
	void setPresenter(GotoController presenter);
 
	//void setMenuWidget(IsWidget w);
    void setBeheer(boolean b);
    //void showIcon(boolean b);
	    
}
