package nl.uu.fi.dwo.lms.gwtclient.gwt;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.RootLayoutPanel;

import java.util.logging.Level;
import java.util.logging.Logger;
import nl.uu.fi.dwo.lms.gwtclient.gwt.dagger.DaggerBootComponent;

/**
 * BootPanel, boots the app, initializes with the server, requests a login.
 *
 * @author G.A.J. van der Plas
 */
public class BootPanel  implements EntryPoint, Window.ClosingHandler{
    private BootPanelController controller;
    
    private static final Logger LOG = Logger.getLogger(BootPanel.class.getName());

    public BootPanel() {
        //nothing is nice.
    }

    private static native void jsInitMainApp() /*-{
        $wnd.jsInitMainApp()
    }-*/;
    
    int cnt;
    @Override
    public void onModuleLoad() {
        try {
          jsInitMainApp();
        } catch(Exception oops) {
          LOG.log(Level.SEVERE, "jsInitMainApp", oops);
          Timer t = new Timer() {

            @Override
            public void run() {
                if(cnt++ < 10) 
                onModuleLoad(); // retry
            }};
          t.schedule(100);
          return;
        }
        //init teacher app
        controller = DaggerBootComponent.create().controller();
        controller.go(RootLayoutPanel.get());
        Window.addWindowClosingHandler(this);

    }

    
    @Override
    public void onWindowClosing(Window.ClosingEvent event) {
        if(controller.isSession()) {
            event.setMessage("Translate this");
        }else{
            event.setMessage(null);
        }
    }

}
