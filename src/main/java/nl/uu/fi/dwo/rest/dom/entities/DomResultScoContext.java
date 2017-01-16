package nl.uu.fi.dwo.rest.dom.entities;

/**
 *
 * @author G.A.J. van der Plas  email: G.A.J.vanderPlas@uu.nl
 */
public class DomResultScoContext extends DomResultScore<DomResultStudentSco> {
    private DomScoContext scoContext;

    public DomResultScoContext(DomScoContext aSco){
        scoContext = aSco;
        super.setLabel(scoContext.getScoName());
    }
        
}
