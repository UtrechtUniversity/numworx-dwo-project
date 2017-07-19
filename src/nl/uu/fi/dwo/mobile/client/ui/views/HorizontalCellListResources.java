package nl.uu.fi.dwo.mobile.client.ui.views;

import com.google.gwt.user.cellview.client.CellList;
import com.google.gwt.user.cellview.client.CellList.Style;

public interface HorizontalCellListResources extends CellList.Resources {

	@Source({"HorizontalCellList.css"})
	@Override
	public Style cellListStyle();
	
}
