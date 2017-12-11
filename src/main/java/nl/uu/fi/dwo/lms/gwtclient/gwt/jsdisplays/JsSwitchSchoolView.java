package nl.uu.fi.dwo.lms.gwtclient.gwt.jsdisplays;

import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import java.util.Map;
import nl.uu.fi.dwo.lms.gwtclient.gwt.roleswitch.SwitchSchoolPresenter;

/**
 * Mapper to allow java interface implementation.
 * 
 * @author G.A.J. van der Plas
 */
public class JsSwitchSchoolView implements SwitchSchoolPresenter.Display{

    @Override
    public void clear() {
        JsSwitchSchoolDisplay.clear();
    }

    @Override
    public void init() {
        JsSwitchSchoolDisplay.init();
    }

    @Override
    public void updateView(Map<String, SwitchSchoolPresenter.SchoolItem> data, SwitchSchoolPresenter.SchoolItem selected) {
        JSONObject object = new JSONObject();
        for(SwitchSchoolPresenter.SchoolItem item : data.values()){
            object.put(item.getKey(), new JSONString(item.getSchoolName()));
        }
        JsSchoolClassesDisplay.updateView(object.getJavaScriptObject());
        JsSwitchSchoolDisplay.clear();
    }


}
