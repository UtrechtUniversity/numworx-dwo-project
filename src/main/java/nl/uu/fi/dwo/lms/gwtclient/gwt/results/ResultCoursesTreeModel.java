package nl.uu.fi.dwo.lms.gwtclient.gwt.results;

import nl.uu.fi.dwo.lms.gwtclient.gwt.schoolclasses.old.CourseCell;
import nl.uu.fi.dwo.lms.gwtclient.gwt.schoolclasses.old.CourseCellNode;
import nl.uu.fi.dwo.lms.gwtclient.gwt.schoolclasses.old.CourseItem;
import nl.uu.fi.dwo.lms.gwtclient.gwt.schoolclasses.*;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.SingleSelectionModel;
import com.google.gwt.view.client.TreeViewModel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Treeview model
 *
 * @author Gert van der Plas
 */
public class ResultCoursesTreeModel implements TreeViewModel {

    private static final Logger LOG = Logger.getLogger(ResultCoursesTreeModel.class.getName());

    private CoursesOfSchoolclassPresenter coursesOfSchoolclassPresenter;
    private SingleSelectionModel<CourseCellNode> selectionModelCellTree;
    private Map<String, ListDataProvider<CourseCellNode>> mapDataProviders;

    ResultCoursesTreeModel(CoursesOfSchoolclassPresenter sp) {
        coursesOfSchoolclassPresenter = sp;
        selectionModelCellTree = new SingleSelectionModel<CourseCellNode>();
        mapDataProviders = new HashMap<String, ListDataProvider<CourseCellNode>>();
    }
//
//    public void add(CourseCellNode aParent, String key, String name) {
//
//        CourseCellNode child = new CourseCellNode(key, name);
//
//        //root-node
//        if (aParent == null) {
//            rootDataProvider.getList().add(child);
//            mapDataProviders.put(child, rootDataProvider);
//        } else {
//            ListDataProvider<CourseCellNode> dataprovider = mapDataProviders.get(aParent);
//            aParent.childs.add(child);
//            child.parent = aParent;
//            dataprovider.refresh();
//        }
//    }
//
//    public void remove(CourseCellNode node) {
//
//        ListDataProvider<CourseCellNode> dataprovider = mapDataProviders.get(node);
//        dataprovider.getList().remove(node);
//        //                 mapDataProviders.remove(objToRemove);
//        dataprovider.refresh();
//        dataprovider.flush();
//
//        if (node.parent != null) {
//            ListDataProvider<CourseCellNode> dataproviderParent = mapDataProviders.get(node.parent);
//            node.parent.childs.remove(node);
//            dataproviderParent.refresh();
//            dataproviderParent.flush();
//        } else {
//            rootDataProvider.refresh();
//            rootDataProvider.flush();
//        }
//    }

    @Override
    public <T> NodeInfo<?> getNodeInfo(T value) {
        //empty list
        if (value == null) {
            //Loading
            ListDataProvider<CourseCellNode> dataProvider;
            if (!mapDataProviders.containsKey("root")) {
                dataProvider = new ListDataProvider<CourseCellNode>();
                mapDataProviders.put("root", dataProvider);
            } else {
                dataProvider = mapDataProviders.get("root");
            }
            List<CourseCellNode> list = new ArrayList<CourseCellNode>();
            CourseCellNode item = new CourseCellNode("root", "loading...");
            list.add(item);
            item.setCell(new CourseCell(dataProvider));

            dataProvider.setList(list);
            return new DefaultNodeInfo<CourseCellNode>(dataProvider, (Cell) item.getCell());
        } else if (value instanceof CourseCellNode) {
            CourseCellNode item = (CourseCellNode) value;
            ListDataProvider<CourseCellNode> dataProvider;
            if (!mapDataProviders.containsKey(item.getKey())) {
                dataProvider = new ListDataProvider<CourseCellNode>();
                mapDataProviders.put(item.getKey(), dataProvider);
            } else {
                dataProvider = mapDataProviders.get(item.getKey());
            }
            LOG.log(Level.INFO, "node " + item.getName());

            ListDataProvider<CourseCellNode> childDataProvider = new ListDataProvider<CourseCellNode>();
            List<CourseCellNode> list = new ArrayList<CourseCellNode>();
            for (CourseItem it : coursesOfSchoolclassPresenter.getNodeChildren(item.getKey())) {
                LOG.log(Level.INFO, "children " + it.getName());
                CourseCellNode itm = new CourseCellNode(it.getKey(), it.getName());
                item.addChild(itm);
                CourseCell cc = new CourseCell(new ListDataProvider<CourseCellNode>());
                item.setCell(cc);
                list.add(itm);
            }
            childDataProvider.setList(list);
            CourseCell thisCell = new CourseCell(childDataProvider);
            item.setCell(thisCell);
            // Return a node info that pairs the data with a cell.
//            Cell<CourseItem> cell = new Cell<CourseItem>();
//            cell.
            return new DefaultNodeInfo<CourseCellNode>(childDataProvider, (Cell) item.getCell());

        } else if (value instanceof String) {
            ListDataProvider<String> dataProvider = new ListDataProvider<String>();
            return new DefaultNodeInfo<String>(dataProvider, new TextCell());
        } else {//null
            return null;
        }
    }

    @Override
    public boolean isLeaf(Object value) {
        if (value instanceof CourseItem || value instanceof CourseCellNode) {
            LOG.log(Level.INFO, "leaf " + ((CourseItem) value).getName());
            if (((CourseItem) value).getKey() == null) {
                return true;
            }
            int size = coursesOfSchoolclassPresenter.getNodeChildren(((CourseItem) value).getKey()).size();
            return size == 0;
        } else {
            return true;
        }
    }

}
