package nl.uu.fi.dwo.mobile.client.ui.views;

import com.google.gwt.user.client.ui.PopupPanel;

import nl.uu.fi.dwo.mobile.client.ui.ScoreNavPanel;
//import nl.uu.fi.dwo.mobile.client.ui.SlidingPopup;

public class MyPopup extends PopupPanel {
    private ScoreNavPanel w;

	public MyPopup(ScoreNavPanel w) {
        // PopupPanel's constructor takes 'auto-hide' as its boolean parameter.
        // If this is set, the panel closes itself automatically when the user
        // clicks outside of it.
        super(true);
        setGlassEnabled(true);
        // PopupPanel is a SimplePanel, so you have to set it's widget property to
        // whatever you want its contents to be.
        this.w = w;
        setWidget(w);
      }

	@Override
	public void setPopupPositionAndShow(PositionCallback callback) {
		// TODO Auto-generated method stub
		super.setPopupPositionAndShow(callback);
		w.refresh();
	}
    
    	
}