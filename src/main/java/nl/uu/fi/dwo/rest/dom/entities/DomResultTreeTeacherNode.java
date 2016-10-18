package nl.uu.fi.dwo.rest.dom.entities;

import java.util.List;

/**
 *
 * @author G.A.J. van der Plas <G.A.J.vanderPlas@uu.nl>
 */
public class DomResultTreeTeacherNode extends DomResultTreeNode implements DomResultTreeNodeIF{
    private DomTeacher teacher;
    private DomResultTreeNodeIF parent;
    private List<DomResultTreeNodeIF> children;

    public DomResultTreeTeacherNode(){
        init();
    }
    
    public DomResultTreeTeacherNode(DomTeacher aTeacher){
        init(aTeacher);
    }
    
    private void init(){
        this.parent = null;
    }

    public void init(DomTeacher aTeacher) {
        teacher = aTeacher;
        init();
    }

    
    
    /**
     * @return the label
     */
    public String getLabel() {
        return teacher.getUniqueDisplayName();
    }

    /**
     * @return the children
     */
    public List<DomResultTreeNodeIF> getChildren() {
        return children;
    }

    /**
     * @param children the children to set
     */
    public void setChildren(List<DomResultTreeNodeIF> children) {
        this.children = children;
    }

    /**
     * @return the parent
     */
    public DomResultTreeNodeIF getParent() {
        return parent;
    }

    /**
     * @param parent the parent to set
     */
    public void setParent(DomResultTreeNodeIF parent) {
        this.parent = parent;
    }

    /**
     * @return the teacher
     */
    public DomTeacher getTeacher() {
        return teacher;
    }

    /**
     * @param teacher the teacher to set
     */
    public void setTeacher(DomTeacher teacher) {
        this.teacher = teacher;
    }

    @Override
    public Double getScore() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
