package nl.uu.fi.dwo.rest.dom.entities;

/**
 *
 * @author G.A.J. van der Plas  email: G.A.J.vanderPlas@uu.nl
 */
public class DomResultScoContext extends DomResultScore<DomResultStudentScoContext> {
    private DomScoContext scoContext;

    public DomResultScoContext(DomScoContext aSco){
        scoContext = aSco;
        super.setLabel(scoContext.getScoName());
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
        
}
