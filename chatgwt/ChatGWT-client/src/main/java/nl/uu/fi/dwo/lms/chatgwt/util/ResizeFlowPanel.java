package nl.uu.fi.dwo.lms.chatgwt.util;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.ProvidesResize;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.Widget;

public class ResizeFlowPanel extends FlowPanel implements RequiresResize, ProvidesResize {
	  
    @Override
    public void onResize() {       
      for (Widget w: getChildren()) {
        if (w instanceof RequiresResize) ((RequiresResize) w).onResize();
      }
    }
    
}
