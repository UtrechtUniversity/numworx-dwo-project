package nl.uu.fi.dwo.lms.gwtclient.gwt.jsdisplays.welcome;

import nl.uu.fi.dwo.lms.gwtclient.gwt.welcome.WelcomePresenter;

/**
 * Mapper to allow java interface implementation.
 * 
 * @author G.A.J. van der Plas
 */
public class JsWelcomeView implements WelcomePresenter.Display{

    @Override
    public void setHelp(String url) {
        JsWelcomeDisplay.setHelp(url);
    }


    @Override
    public void init(){
        JsWelcomeDisplay.init();
    }

    @Override
    public void clear(){
        JsWelcomeDisplay.clear();
    }
    @Override
    public void setDefaultText(){
        JsWelcomeDisplay.setDefaultText();
    }
    
    @Override
    public void setWelcomeText(String html){
        JsWelcomeDisplay.setWelcomeText(html);
    }


}
