package nl.uu.fi.dwo.lms.gwtclient.gwt.jsdisplays.schoolclasses;

import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import java.util.List;
import nl.uu.fi.dwo.lms.gwtclient.gwt.schoolclasses.AddStudentsPresenter;

/**
 * Mapper to allow java interface implementation.
 * 
 * @author G.A.J. van der Plas
 */
public class JsAddStudentsView implements AddStudentsPresenter.Display{
    @Override
    public void clear() {
        JsAddStudentsDisplay.clear();
    }

    @Override
    public void init() {
        JsAddStudentsDisplay.init();
    }

    @Override
    public void updateView(List<AddStudentsPresenter.StudentItem> data) {
        JSONObject object = new JSONObject();
        for(AddStudentsPresenter.StudentItem item : data){
            JSONArray line = new JSONArray();
            line.set(0, new JSONString(item.givenName));
            line.set(0, new JSONString(item.insertion));
            line.set(0, new JSONString(item.familyName));
            line.set(0, new JSONString(item.email));
            line.set(0, new JSONString(item.usercode));
            line.set(0, new JSONString(item.password));
            object.put(item.key,line);
        }
        JsAddStudentsDisplay.updateView(object.getJavaScriptObject());
    }

    @Override
    public void refreshView() {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        //do nothing, assume jsView takes care of it.
    }


}
