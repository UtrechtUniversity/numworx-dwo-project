package nl.uu.fi.dwo.rest.dom.entities;

/**
 *
 * @author G.A.J. van der Plas  email: G.A.J.vanderPlas@uu.nl
 */
@SuppressWarnings("rawtypes")
public class DomResultTeacher<T extends DomResultScore> extends DomResultScore<DomResultSchoolClass<T>> {
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
