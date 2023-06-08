package nl.uu.fi.dwo.lms.gwtclient.gwt.jsdisplays.schoolclasses;

import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import java.util.Map;
import nl.uu.fi.dwo.lms.gwtclient.gwt.jsdisplays.results.JsResultsDisplay;
import nl.uu.fi.dwo.lms.gwtclient.gwt.schoolclasses.SchoolclassesPresenter;

/**
 * Mapper to allow java interface implementation.
 * 
 * @author G.A.J. van der Plas
 */
public class JsSchoolClassesView implements SchoolclassesPresenter.Display{
    @Override
    public void clear() {
        JsSchoolClassesDisplay.clear();
    }

    @Override
    public void setHelp(String url) {
        JsSchoolClassesDisplay.setHelp(url);
    }
    
    @Override
    public void init() {
        JsSchoolClassesDisplay.init();
    }

    @Override
    public void updateView(Map<String, SchoolclassesPresenter.ClassItem> data) {
        JSONObject object = new JSONObject();
        for(SchoolclassesPresenter.ClassItem item : data.values()){
            object.put(item.getKey(), new JSONString(item.getSchoolclassName()));
        }
        JsSchoolClassesDisplay.updateView(object.getJavaScriptObject());
    }

    @Override
        public void setEmptyTableMessage() {
          JsSchoolClassesDisplay.setEmptyTableMessage();
    }

    @Override
        public void setLoadingTableMessage() {
          JsSchoolClassesDisplay.setLoadingTableMessage();
    }


}
