package nl.uu.fi.dwo.lms.gwtclient.gwt.schoolclasses;

import com.google.gwt.view.client.ProvidesKey;
import java.util.ArrayList;

/**
 *
 * @author Gert van der Plas
 */
public class CourseCellNode extends CourseItem {

    private CourseCell cell;
    private ArrayList<CourseCellNode> list; //nodes childrens
    private CourseCellNode parent; //track internal parent


    //The key provider that provides the unique ID of a CourseCell.
    public static final ProvidesKey<CourseCellNode> KEY_PROVIDER = new ProvidesKey<CourseCellNode>() {
      @Override
      public Object getKey(CourseCellNode item) {
        return item == null ? null : item.getKey();
      }
    };    
    
    public CourseCellNode(String aKey, String aName) {
        super(aKey, aName);
        parent = null;
        list = new ArrayList<CourseCellNode>();
    }

    public void addChild(CourseCellNode m) {
        m.parent = this;
        list.add(m);
    }

    public boolean hasChildrens() {
        return list.size() > 0;
    }

    /**
     * @return the cell
     */
    public CourseCell getCell() {
        return cell;
    }

    /**
     * @param cell the cell to set
     */
    public void setCell(CourseCell cell) {
        this.cell = cell;
    }

    /**
     * @return the list
     */
    public ArrayList<CourseCellNode> getList() {
        return list;
    }

    /**
     * @param list the list to set
     */
    public void setList(ArrayList<CourseCellNode> list) {
        this.list = list;
    }

    /**
     * @return the parent
     */
    public CourseCellNode getParent() {
        return parent;
    }

    /**
     * @param parent the parent to set
     */
    public void setParent(CourseCellNode parent) {
        this.parent = parent;
    }

}
