package nl.uu.fi.dwo.rest.dom.entities;

/**
 *
 * @author G.A.J. van der Plas  email: G.A.J.vanderPlas@uu.nl
 */
public class DomResultStudent extends DomResultScore<DomResultSchoolClass> {
    private DomStudent student;

    public DomResultStudent(DomStudent aStudent){
        student = aStudent;
        super.setLabel(student.getUniqueDisplayName());
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
