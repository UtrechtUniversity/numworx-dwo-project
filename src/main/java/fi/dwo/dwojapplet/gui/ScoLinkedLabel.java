/*
 * Created on Mar 2, 2005
 *
 */
package fi.dwo.dwojapplet.gui;

import fi.dwo.dwojapplet.domain.Sco;

/**
 * This class is a linked label for a sco.
 * 
 * @author M.J.B. Kupers
 *  
 */
public class ScoLinkedLabel extends LinkedLabel {

    private Sco sco;

    /**
     * Creates a new ScoLinkedLabel. It is a normal linked label with the
     * sconame as caption.
     * 
     * @param s The sco of the Scolabel
     */
    public ScoLinkedLabel(Sco s) {
        super(s.getSequencenr() + ".  " + s.getScoName());
        sco = s;
        this.setToolTipText(sco.getDescription());
    }

    /**
     * Returns the current sco.
     * 
     * @return The current sco.
     */
    public Sco getSco() {
        return sco;
    }
}