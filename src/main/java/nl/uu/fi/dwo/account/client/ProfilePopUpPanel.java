package nl.uu.fi.dwo.account.client;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.RootLayoutPanel;

/**
 *
 * @author G.A.J. van der Plas
 */
public class ProfilePopUpPanel extends PopupPanel {

    public ProfilePopUpPanel() {
        super(true);
        ProfilePanel profPanel = new ProfilePanel();
        RootLayoutPanel.get().add((IsWidget) profPanel);

        // PopupPanel is a SimplePanel, so you have to set it's widget property to
        // whatever you want its contents to be.
        setWidget(new Label("Click outside of this popup to close it"));
    }

}
