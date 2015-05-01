package nl.uu.fi.dwo.mobile.client.ui.views;

import nl.uu.fi.dwo.mobile.client.ui.ScoreNavPanel;
import nl.uu.fi.dwo.mobile.client.ui.SlidingPopup;

public class MyPopup extends SlidingPopup {
    public MyPopup(ScoreNavPanel w) {
        // PopupPanel's constructor takes 'auto-hide' as its boolean parameter.
        // If this is set, the panel closes itself automatically when the user
        // clicks outside of it.
        super(true);
        setGlassEnabled(true);
        setAnimationEnabled(true);
        // PopupPanel is a SimplePanel, so you have to set it's widget property to
        // whatever you want its contents to be.
        setWidget(w);
      }
}