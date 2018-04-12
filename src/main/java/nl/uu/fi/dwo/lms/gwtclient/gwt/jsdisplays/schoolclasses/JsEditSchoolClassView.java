package nl.uu.fi.dwo.lms.gwtclient.gwt.jsdisplays.schoolclasses;

import com.google.gwt.json.client.JSONValue;
import fi.dwo.gwt.lib.rest.util.DomSchoolClassFullCodec;
import nl.uu.fi.dwo.lms.gwtclient.gwt.schoolclasses.EditSchoolclassPresenter;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClassFull;

/**
 * Mapper to allow java interface implementation.
 * 
 * @author G.A.J. van der Plas
 */
public class JsEditSchoolClassView implements EditSchoolclassPresenter.Display{
    @Override
    public void clear() {
        JsEditSchoolclassDisplay.clear();
    }

    @Override
    public void init() {
        JsEditSchoolclassDisplay.init();
    }

    @Override
    public void showSchoolClass(DomSchoolClassFull schoolClass) {
        JSONValue sc = DomSchoolClassFullCodec.CODEC.encode(schoolClass);
	JsEditSchoolclassDisplay.showSchoolClass(sc);
    }

}
