package nl.uu.fi.dwo.mobile.client.ui.views;

import com.google.gwt.user.cellview.client.CellList;
import com.google.gwt.user.cellview.client.CellList.Style;

public interface HorizontalCellListResources extends CellList.Resources {

	interface MyStyle extends Style {
		String navCellList();
		String tileCellList();
	}
	
	@Source({"HorizontalCellList.css"})
	@Override
	public MyStyle cellListStyle();
	
}
