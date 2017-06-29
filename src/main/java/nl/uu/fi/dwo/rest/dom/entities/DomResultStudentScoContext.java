package nl.uu.fi.dwo.rest.dom.entities;

/**
 * Student's results on a Sco
 * @author G.A.J. van der Plas  email: G.A.J.vanderPlas@uu.nl
 */
public class DomResultStudentScoContext extends DomResultScore<DomResultScore> {
    private DomStudentScoContext studentSco;

    public DomResultStudentScoContext(DomStudentScoContext aSco, DomStudent student){
        studentSco = aSco;
        super.setLabel(student.getUniqueDisplayName());
    }

    /**
     * @return the studentSco
     */
    public DomStudentScoContext getStudentSco() {
        return studentSco;
    }

    /**
     * @param studentSco the studentSco to set
     */
    public void setStudentSco(DomStudentScoContext studentSco) {
        this.studentSco = studentSco;
    }
        
}
