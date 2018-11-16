package nl.uu.fi.dwo.lms.gwtclient.gwt.jsdisplays.modules;

import javax.inject.Inject;
import javax.inject.Singleton;

import nl.uu.fi.dwo.lms.gwtclient.gwt.modules.ModulesPresenter;

/**
 * Mapper to allow java interface implementation.
 * 
 * @author G.A.J. van der Plas
 */
@Singleton
public class JsModulesView implements ModulesPresenter.Display{

    @Override
    public void clear(){
        JsModulesDisplay.clear();
    }

    @Override
    public void openUrl(String url) {
        JsModulesDisplay.openUrl(url);
    }
    
    @Inject JsModulesView() {}

    @Override
    public void init() {
        JsModulesDisplay.init();
    }

    @Override
    public void setHelp(String url) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setMainNavVisible(boolean b) {
        JsModulesDisplay.setMainNavVisible(b);
      
    }

    @Override
    public boolean isMainNavVisible() {
        return JsModulesDisplay.isMainNavVisible();
    }

    @Override
    public void sendMessage(String message) {
      JsModulesDisplay.sendMessage(message);
      
    }
}
