package nl.uu.fi.dwo.lms.gwtclient.gwt.jsdisplays.schoolclasses;

import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.json.client.JSONValue;
import fi.dwo.gwt.lib.rest.util.DomTreeCodec;
import java.util.List;
import nl.uu.fi.dwo.lms.gwtclient.gwt.schoolclasses.ClassCourseItem;
import nl.uu.fi.dwo.lms.gwtclient.gwt.schoolclasses.ModulesOfSchoolclassPresenter;
import nl.uu.fi.dwo.rest.dom.DomTree;
import nl.uu.fi.dwo.rest.dom.entities.DomCourseOfClass;

/**
 * Mapper to allow java interface implementation.
 * 
 * @author G.A.J. van der Plas
 */
public class JsModulesOfSchoolclassView implements ModulesOfSchoolclassPresenter.Display{

    @Override
    public void clear() {
        JsModulesOfSchoolclassDisplay.clear();
    }

    @Override
    public void init() {
        JsModulesOfSchoolclassDisplay.init();
    }


    @Override
    public void updateTable(List<ClassCourseItem> dataList) {
        JSONObject object = new JSONObject();
        for(ClassCourseItem item : dataList){
            object.put(item.getKey(), new JSONString(item.getName()));
        }
        JsModulesOfSchoolclassDisplay.updateTable(object.getJavaScriptObject());
    }

    @Override
    public void setTree(DomTree<DomCourseOfClass> tree) {
        //TODO insert json tree
        //JSONValue object = DomTreeCodec.CODEC.encode(tree);
        JsModulesOfSchoolclassDisplay.setTree(null);
    }

    @Override
    public void setEmptyTableMessageModules() {
        JsModulesOfSchoolclassDisplay.setEmptyTableMessageModules();
    }

    @Override
    public void setLoadingTableMessageModules() {
        JsModulesOfSchoolclassDisplay.setLoadingTableMessageModules();
    }

    @Override
    public void setEmptyTableMessageSelected() {
        JsModulesOfSchoolclassDisplay.setEmptyTableMessageSelected();
    }

    @Override
    public void setLoadingTableMessageSelected() {
        JsModulesOfSchoolclassDisplay.setLoadingTableMessageSelected();
    }
    
}
