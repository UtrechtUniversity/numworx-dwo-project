package nl.uu.fi.dwo.lms.gwtclient.gwt.jsdisplays.persons;

import com.google.gwt.json.client.JSONObject;
import fi.dwo.gwt.lib.rest.util.DomUserCodec;
import java.util.Map;
import nl.uu.fi.dwo.lms.gwtclient.gwt.persons.PersonsPresenter;
import nl.uu.fi.dwo.rest.dom.entities.DomUser;

/**
 * Mapper to allow java interface implementation.
 * 
 * @author G.A.J. van der Plas
 */
public class JsPersonsView implements PersonsPresenter.Display{

    @Override
    public void setHelp(String url) {
        JsPersonsDisplay.setHelp(url);
    }
    
    @Override
    public void clear() {
        JsPersonsDisplay.clear();
    }

    @Override
    public void init() {
        JsPersonsDisplay.init();
    }

    @Override
    public void setEmptyTableMessage() {
        JsPersonsDisplay.setEmptyTableMessage();
    }

    @Override
    public void setLoadingTableMessage() {
       JsPersonsDisplay.setLoadingTableMessage();
    }

    @Override
    public void showPersonen(Map<String, DomUser> personen) {
        JSONObject object = new JSONObject();
        for(DomUser person : personen.values()){
            object.put(person.getId().getIdString(), DomUserCodec.CODEC.encode(person));
        }
        JsPersonsDisplay.showPersons(object.getJavaScriptObject());
    }


}
