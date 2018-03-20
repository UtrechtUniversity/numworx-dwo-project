package nl.uu.fi.dwo.lms.gwtclient.gwt.jsdisplays.account;

import com.google.gwt.json.client.JSONValue;
import fi.dwo.gwt.lib.rest.util.DomSchoolsRolesAndClassesV2Codec;
import nl.uu.fi.dwo.lms.gwtclient.gwt.account.AccountPresenter;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolsRolesAndClassesV2;

/**
 * Mapper to allow java interface implementation.
 * 
 * @author G.A.J. van der Plas
 */
public class JsAccountView implements AccountPresenter.Display{
    @Override
    public void clear() {
        JsAccountDisplay.clear();
    }

    @Override
    public void init() {
        JsAccountDisplay.init();
    }

    @Override
    public void updateSchoolLogins(DomSchoolsRolesAndClassesV2 schoolLogins) {
        JSONValue sl = DomSchoolsRolesAndClassesV2Codec.CODEC.encode(schoolLogins);
        
        JsAccountDisplay.updateView(sl);  
    }

}
