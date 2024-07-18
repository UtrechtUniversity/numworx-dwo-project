package nl.uu.fi.dwo.rest.dom.entities;

import nl.uu.fi.dwo.rest.dom.entities.util.DomResultScoreVisitor;
import nl.uu.fi.dwo.rest.dom.entities.util.ScoType;

/**
 *
 * @author G.A.J. van der Plas  email: G.A.J.vanderPlas@uu.nl
 */
public class DomResultScoContext extends DomResultScore<DomResultStudentScoContext> {
    private DomScoContext scoContext;
    private Double maxScore;

    public DomResultScoContext(DomScoContext aSco){
        scoContext = aSco;
        if (aSco.getScoType() == ScoType.INFO)
        	maxScore = 0.0;
        else
        	maxScore = 100.0; // by default
        super.setLabel(scoContext.getScoName());
    }
    
    public DomResultScoContext(DomResultScoContext copy) {
    	this(copy.getScoContext());
    }

    /**
     * @return the scoContext
     */
    public DomScoContext getScoContext() {
        return scoContext;
    }

    /**
     * @param scoContext the scoContext to set
     */
    public void setScoContext(DomScoContext scoContext) {
        this.scoContext = scoContext;
    }

    @Override
    public String getId() {
      return getScoContext().getId().getIdString();
    }

	public Double getMaxScore() {
		return maxScore;
	}

	public void setMaxScore(Double maxScore) {
		this.maxScore = maxScore;
	}

	@Override
	public void visit(DomResultScoreVisitor v) {
		v.visitScoContext(this);
	}
        
}
