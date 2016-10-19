package nl.uu.fi.dwo.rest.dom.entities;

/**
 *
 * @author G.A.J. van der Plas <G.A.J.vanderPlas@uu.nl>
 */
public class DomResultTeacher extends DomResultScore {
    private DomTeacher teacher;

    public DomResultTeacher(DomTeacher aTeacher){
        teacher = aTeacher;
        super.setLabel(teacher.getUniqueDisplayName());
    }
    
    /**
     * A root node has no parent.
     * 
     * @param score
     */
    @Override
    public void setParent(DomResultScore score){
        super.setParent(null);
    }
        
}
