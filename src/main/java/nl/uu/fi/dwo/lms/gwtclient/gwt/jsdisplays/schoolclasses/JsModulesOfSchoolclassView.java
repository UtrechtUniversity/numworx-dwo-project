package nl.uu.fi.dwo.lms.gwtclient.gwt.jsdisplays.schoolclasses;

import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import fi.dwo.gwt.lib.rest.util.DomCourseOfClassCodec;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import nl.uu.fi.dwo.lms.gwtclient.gwt.schoolclasses.ClassCourseItem;
import nl.uu.fi.dwo.lms.gwtclient.gwt.schoolclasses.ModulesOfSchoolclassPresenter;
import nl.uu.fi.dwo.rest.dom.DomTree;
import nl.uu.fi.dwo.rest.dom.entities.DomCourseOfClass;

/**
 * Mapper to allow java interface implementation.
 *
 * @author G.A.J. van der Plas
 */
public class JsModulesOfSchoolclassView implements ModulesOfSchoolclassPresenter.Display {

    private static final Logger LOG = Logger.getLogger(JsModulesOfSchoolclassView.class.getName());

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
        for (ClassCourseItem item : dataList) {
            object.put(item.getKey(), new JSONString(item.getName()));
        }
        JsModulesOfSchoolclassDisplay.updateTable(object.getJavaScriptObject());
    }

    private void DFSTreePrint(DomTree<DomCourseOfClass> node) {
        DFSTreePrint(node, 0);
    }

    private void DFSTreePrint(DomTree<DomCourseOfClass> node, int depth) {
        // do depth first search       
        if (node.getChildren() != null && !node.getChildren().isEmpty()) {
            depth++;
            for (DomTree<DomCourseOfClass> coc : node.getChildren().values()) {
                LOG.log(Level.FINE, "(" + depth + "," + coc.getObject().getCourse().getId().getIdString() + " " + coc.getObject().getCourse().getName() + ")");
                if (coc.getChildren() != null && !coc.getChildren().isEmpty()) {
                    for (DomTree<DomCourseOfClass> child : node.getChildren().values()) {
                        DFSTreePrint(child, depth);
                    }
                }
            }
            depth--;
        }

    }

    private JSONObject buildSubTree(DomTree<DomCourseOfClass> node) {
        JSONObject json = new JSONObject();
        if (node.getChildren() != null && !node.getChildren().isEmpty()) {
            for (DomTree<DomCourseOfClass> coc : node.getChildren().values()) {
                if (coc.getObject() != null && coc.getObject().getCourse() != null) {
                    if (coc.getChildren() != null && !coc.getChildren().isEmpty()) {
                        for (DomTree<DomCourseOfClass> child : node.getChildren().values()) {
                            json.put(child.getObject().getClassCourse().getId().getIdString(), buildSubTree(child));
                        }
                    } else {
                        LOG.log(Level.INFO, "id: " + new Object[]{coc.getObject().getCourse().getId().getIdString() + " coursename " + coc.getObject().getCourse().getName()});
                        json.put(coc.getObject().getClassCourse().getId().getIdString(), new JSONString(coc.getObject().getCourse().getName()));
                    }
                }
            }
        }
        LOG.log(Level.INFO, "node: " + json.toString());
        return json;
    }

    @Override
    public void setTree(DomTree<DomCourseOfClass> tree) {
        //this.DFSTreePrint(tree);

        JSONObject object = buildSubTree(tree);
        LOG.log(Level.INFO, "tree: " + object.toString());
        JsModulesOfSchoolclassDisplay.setTree(object);
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
