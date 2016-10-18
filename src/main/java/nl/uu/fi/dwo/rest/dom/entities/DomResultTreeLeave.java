package nl.uu.fi.dwo.rest.dom.entities;

import java.util.List;

/**
 *
 * @author G.A.J. van der Plas <G.A.J.vanderPlas@uu.nl>
 */
public class DomResultTreeLeave extends DomResultTreeNode implements DomResultTreeNodeIF{
    DomCourse course;
    
    @Override
    public String getLabel() {
        return course.getName();
    }
    
    @Override
    public List<DomResultTreeNodeIF> getChildren() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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
