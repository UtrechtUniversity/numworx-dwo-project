package nl.uu.fi.dwo.lms.gwtclient.gwt.schoolclasses;

import com.google.gwt.cell.client.TextCell;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.TreeViewModel;
import nl.uu.fi.dwo.lms.gwtclient.gwt.schoolclasses.CoursesOfSchoolclassView.MyTreeCell;


/**
 * Treeview model
 *
 * @author Gert van der Plas
 */
public class CoursesOfSchoolclassTreeModel implements TreeViewModel {

    private CoursesOfSchoolclassPresenter coursesOfSchoolclassPresenter;

    CoursesOfSchoolclassTreeModel(CoursesOfSchoolclassPresenter sp) {
        coursesOfSchoolclassPresenter = sp;
    }

    @Override
    public <T> NodeInfo<?> getNodeInfo(T value) {

        if (value instanceof CourseItem) {
            CourseItem item = (CourseItem) value;
            /*
       * Create some data in a data provider. Use the parent value as a prefix
       * for the next level.
             */
            ListDataProvider<CourseItem> dataProvider = new ListDataProvider<CourseItem>();
            dataProvider.setList(coursesOfSchoolclassPresenter.getNodeChildren(((CourseItem) value).getKey()));

            // Return a node info that pairs the data with a cell.
            
//            Cell<CourseItem> cell = new Cell<CourseItem>();
//            cell.
            return new DefaultNodeInfo<CourseItem>(dataProvider, new MyTreeCell());
        } else if (value instanceof String) {
             ListDataProvider<String> dataProvider = new ListDataProvider<String>();
             return new DefaultNodeInfo<String>(dataProvider, new TextCell());
        }else{
            return null;
        }
    }

    @Override
    public boolean isLeaf(Object value) {
        if (value instanceof CourseItem) {
            if(((CourseItem) value).getKey()==null) return false;
            return coursesOfSchoolclassPresenter.getNodeChildren(((CourseItem) value).getKey()).size()!=0;
        }else{
            return false;
        }
    }

}
