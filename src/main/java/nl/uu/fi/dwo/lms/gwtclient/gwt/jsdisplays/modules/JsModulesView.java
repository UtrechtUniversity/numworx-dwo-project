package nl.uu.fi.dwo.lms.gwtclient.gwt.jsdisplays.modules;

import javax.inject.Inject;
import nl.uu.fi.dwo.lms.gwtclient.gwt.modules.ModulesPresenter;

/**
 * Mapper to allow java interface implementation.
 * 
 * @author G.A.J. van der Plas
 */
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

}
