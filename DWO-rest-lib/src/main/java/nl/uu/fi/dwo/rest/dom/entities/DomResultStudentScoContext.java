package nl.uu.fi.dwo.rest.dom.entities;

/**
 * Student's results on a Sco
 * @author G.A.J. van der Plas  email: G.A.J.vanderPlas@uu.nl
 */
public class DomResultStudentScoContext extends DomResultScore<DomResultStudentScoPage> {
    private DomStudentScoContext studentSco;
    private Double maxScore;

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

    @Override
    public String getId() {
      return getStudentSco().getId().getIdString();
    }

	public Double getMaxScore() {
		return maxScore;
	}

	public void setMaxScore(Double maxScore) {
		this.maxScore = maxScore;
	}
        
}
