package nl.uu.fi.dwo.lms.gwtclient.gwt;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.RootLayoutPanel;

import nl.uu.fi.dwo.lms.gwtclient.gwt.dagger.BootComponent;
import nl.uu.fi.dwo.lms.gwtclient.gwt.dagger.DaggerBootComponent;

import java.util.logging.Logger;

/**
 * BootPanel, boots the app, initializes with the server, requests a login.
 *
 * @author G.A.J. van der Plas
 */
public class BootPanel implements EntryPoint {

    private static final Logger LOG = Logger.getLogger(BootPanel.class.getName());

    public BootPanel() {
        //nothing is nice.
    }

    @Override
    public void onModuleLoad() {
    	BootComponent boot = DaggerBootComponent.create();

    	//init app controller
        BootPanelController appViewer = boot.controller();
        appViewer.go(RootLayoutPanel.get());
        
        
    }

}
