package nl.uu.fi.dwo.rest.dom.entities;

import java.util.List;

/**
 *
 * @author G.A.J. van der Plas <G.A.J.vanderPlas@uu.nl>
 */
public class DomResultTreeRoot extends DomResultTreeNode{
    DomTeacher teacher;
    List<DomResultTreeNodeIF> schoolClassNodes;

    @Override
    public String getLabel() {
        return teacher.getUniqueDisplayName();
    }

    @Override
    public List<DomResultTreeNodeIF> getChildren() {
        return schoolClassNodes;
    }

    @Override
    public DomResultTreeNodeIF getParent() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Double getScore() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
