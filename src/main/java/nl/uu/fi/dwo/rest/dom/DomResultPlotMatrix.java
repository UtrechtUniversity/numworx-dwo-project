package nl.uu.fi.dwo.rest.dom;

import nl.uu.fi.dwo.rest.dom.entities.DomResultScore;
import nl.uu.fi.dwo.rest.dom.entities.DomResultStudentSco;

/**
 * PlotMatrix of results. The matrix has two arrays containing the axis labels
 * and an 2D-array filled with a subclass of the DomResultScore class or null.
 *
 * @author Gert van der Plas
 */
public class DomResultPlotMatrix {

    private DomResultStudentSco marks[][] = null;
    private DomResultScore[] vIndex; //uses label property for display
    private DomResultScore[] hIndex; //uses label property for display

    DomResultPlotMatrix(DomResultScore[] theVIndex, DomResultScore[] theHIndex) {
        vIndex = theVIndex;
        hIndex = theHIndex;
        marks = new DomResultStudentSco[hIndex.length][vIndex.length];
    }

    /**
     * @return the marks
     */
    public DomResultStudentSco getMark(int i, int j) {
        return marks[i][j];
    }

    /**
     * @param marks the marks to set
     */
    public void setMarks(int i, int j, DomResultStudentSco mark) {
        this.marks[i][j] = mark;
    }

    public int getvSize(){
        return vIndex.length;
    }

    public int gethSize(){
        return hIndex.length;
    }

    /**
     * @return the vIndex
     */
    public DomResultScore getvIndex(int i) {
        return vIndex[i];
    }

    /**
     * @return the hIndex
     */
    public DomResultScore gethIndex(int j) {
        return hIndex[j];
    }

}
