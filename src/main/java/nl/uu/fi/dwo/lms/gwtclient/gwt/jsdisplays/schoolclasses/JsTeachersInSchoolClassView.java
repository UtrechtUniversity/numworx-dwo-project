package nl.uu.fi.dwo.lms.gwtclient.gwt.jsdisplays.schoolclasses;

import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import java.util.List;
import java.util.Map;
import nl.uu.fi.dwo.lms.gwtclient.gwt.schoolclasses.old.TeacherListBoxItem;
import nl.uu.fi.dwo.lms.gwtclient.gwt.schoolclasses.old.TeachersInSchoolclassPresenter;

/**
 * Mapper to allow java interface implementation.
 * 
 * @author G.A.J. van der Plas
 */
public class JsTeachersInSchoolClassView implements TeachersInSchoolclassPresenter.Display{
    @Override
    public void clear() {
        JsSchoolClassesDisplay.clear();
    }

    @Override
    public void init() {
        JsSchoolClassesDisplay.init();
    }

    @Override
    public void updateView(Map<String, TeachersInSchoolclassPresenter.TeacherItem> data) {
        JSONObject object = new JSONObject();
        for(TeachersInSchoolclassPresenter.TeacherItem item : data.values()){
            String sn = item.givenName; 
            sn+= (item.insertion.length()!=0) ? " "+item.insertion : " " ;
            sn+= item.familyName;
            object.put(item.key, new JSONString(sn));
        }
        JsTeachersInSchoolClassDisplay.updateView(object.getJavaScriptObject());
    }

    @Override
    public void updateTeacherList(List<TeacherListBoxItem> data) {
        JSONObject object = new JSONObject();
        for(TeacherListBoxItem item : data){
            object.put(item.getKey(), new JSONString(item.getTeacherName()));
        }
        JsTeachersInSchoolClassDisplay.updateView(object.getJavaScriptObject());
    }    

    @Override
        public void setEmptyTableMessage() {
        JsTeachersInSchoolClassDisplay.setEmptyTableMessage();
    }

    @Override
        public void setLoadingTableMessage() {
        JsTeachersInSchoolClassDisplay.setLoadingTableMessage();
    }

}
