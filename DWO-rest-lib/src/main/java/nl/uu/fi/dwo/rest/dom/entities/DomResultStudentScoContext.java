package nl.uu.fi.dwo.rest.dom.entities;

import nl.uu.fi.dwo.rest.dom.entities.util.DomResultScoreVisitor;
import nl.uu.fi.dwo.rest.dom.entities.util.ScoType;
import nl.uu.fi.dwo.rest.persistence.PersistenceId;

/**
 * Student's results on a Sco
 * @author G.A.J. van der Plas  email: G.A.J.vanderPlas@uu.nl
 */
public class DomResultStudentScoContext extends DomResultScore<DomResultStudentScoPage> {
    private DomStudentScoContext studentSco;
    private Double maxScore;
	private ScoType scoType;

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
      PersistenceId id = getStudentSco().getId();
	return id == null ? getSyntheticId() : id.getIdString();
    }

	public String getSyntheticId() {
		return getStudentSco().getUserID().getIdString();
	}

	public Double getMaxScore() {
		return maxScore;
	}

	public void setMaxScore(Double maxScore) {
		this.maxScore = maxScore;
	}

	@Override
	public void visit(DomResultScoreVisitor v) {
		v.visitStudentScoContext(this);
	}

	public ScoType getScoType() {
		return scoType;
	}

	public void setScoType(ScoType scoType) {
		this.scoType = scoType;	
	}
        
}
