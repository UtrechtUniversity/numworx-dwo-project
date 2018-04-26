package nl.uu.fi.dwo.lms.gwtclient.gwt.jsdisplays.schoolclasses;

import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import java.util.List;
import java.util.Map;
import nl.uu.fi.dwo.lms.gwtclient.gwt.schoolclasses.old.SchoolClassListBoxItem;
import nl.uu.fi.dwo.lms.gwtclient.gwt.schoolclasses.old.StudentsInSchoolclassPresenter;

/**
 * Mapper to allow java interface implementation.
 * 
 * @author G.A.J. van der Plas
 */
public class JsStudentsInSchoolClassView implements StudentsInSchoolclassPresenter.Display{
    @Override
    public void clear() {
        JsSchoolClassesDisplay.clear();
    }

    @Override
    public void init() {
        JsSchoolClassesDisplay.init();
    }

    @Override
    public void updateView(Map<String, StudentsInSchoolclassPresenter.StudentItem> data) {
        JSONObject object = new JSONObject();
        for(StudentsInSchoolclassPresenter.StudentItem item : data.values()){
            String sn = item.givenName; 
            sn+= (item.insertion.length()!=0) ? " "+item.insertion : " " ;
            sn+= item.familyName;
            object.put(item.key, new JSONString(sn));
        }
        JsStudentsInSchoolClassDisplay.updateView(object.getJavaScriptObject());
    }

    @Override
    public void updateSchoolClassList(List<SchoolClassListBoxItem> data) {
        JSONObject object = new JSONObject();
        for(SchoolClassListBoxItem item : data){
            object.put(item.getKey(), new JSONString(item.getSchoolclassName()));
        }
        JsStudentsInSchoolClassDisplay.updateSchoolClasses(object.getJavaScriptObject());
    }    

    @Override
        public void setEmptyTableMessage() {
        JsStudentsInSchoolClassDisplay.setEmptyTableMessage();
    }

    @Override
        public void setLoadingTableMessage() {
        JsStudentsInSchoolClassDisplay.setLoadingTableMessage();
    }

}
