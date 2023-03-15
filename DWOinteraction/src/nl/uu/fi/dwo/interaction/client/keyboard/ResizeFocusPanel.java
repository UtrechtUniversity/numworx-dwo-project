package nl.uu.fi.dwo.interaction.client.keyboard;

import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.ProvidesResize;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.Widget;

public class ResizeFocusPanel extends FocusPanel implements RequiresResize, ProvidesResize {

		@Override
		public void onResize() {
			Widget w = getWidget();
			if(w instanceof RequiresResize)
				((RequiresResize) w).onResize();
		}

		public ResizeFocusPanel(Widget child) {
			super(child);
		}

        public ResizeFocusPanel() {
        }

//		@Override
//		protected void onAttach() {
//			// TODO Auto-generated method stub
//			super.onAttach();
//			fp.forceLayout();
//		}

	}