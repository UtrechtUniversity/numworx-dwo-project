package nl.uu.fi.dwo.lms.gwtclient.gwt;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.RootLayoutPanel;

import fi.dwo.gwt.lib.rest.GwtRestVars;

import java.util.logging.Level;
import java.util.logging.Logger;

import nl.uu.fi.dwo.lms.gwtclient.gwt.SwitchViewEvent.SelectedView;
import nl.uu.fi.dwo.lms.gwtclient.gwt.dagger.DaggerBootComponent;

/**
 * BootPanel, boots the app, initializes with the server, requests a login.
 *
 * @author G.A.J. van der Plas
 */
public class BootPanel  implements EntryPoint, Window.ClosingHandler, CloseHandler<Window>{
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
        Window.addCloseHandler(this);

    }

    
    @Override
    public void onWindowClosing(Window.ClosingEvent event) {
        if(controller.isSession()) {
        	controller.eventBus.fireEvent(new SwitchViewEvent(SelectedView.CLOSING));
            event.setMessage("Translate this");
        }else{
            event.setMessage(null);
        }
    }

    private static native void sendBeacon(String url, String query) /*-{
      var data = new URLSearchParams(query);
      navigator.sendBeacon(url, data);
    }-*/;
    
    @Override
    public void onClose(CloseEvent<Window> event) {
      GwtRestVars vars = GwtRestVars.getInstance();
      String query = vars.getLogoutQuery();
      if (query != null) {
        String url = vars.getLogoutURL();
        sendBeacon(url, query);
      }
      
    }

}
