package nl.uu.fi.dwo.rest.dom.entities;

/**
 * Student's results on a Sco
 * @author G.A.J. van der Plas <G.A.J.vanderPlas@uu.nl>
 */
public class DomResultStudentSco extends DomResultScore{
    private DomStudentScoContext studentSco;

    public DomResultStudentSco(DomStudentScoContext aSco, DomStudent student){
        studentSco = aSco;
        super.setLabel(student.getUniqueDisplayName());
    }
        
}
