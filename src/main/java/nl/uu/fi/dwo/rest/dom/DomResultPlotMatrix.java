package nl.uu.fi.dwo.rest.dom;

import com.sun.javafx.binding.StringFormatter;
import nl.uu.fi.dwo.rest.dom.entities.DomResultScore;

/**
 * PlotMatrix of results. The matrix has two arrays containing the axis labels
 * and an 2D-array filled with a subclass of the DomResultScore class or null.
 *
 * @author Gert van der Plas
 */
public class DomResultPlotMatrix {

    private DomResultScore marks[][] = null;
    private DomResultScore[] vIndex; //uses label property for display
    private DomResultScore[] hIndex; //uses label property for display

    DomResultPlotMatrix(DomResultScore[] theVIndex, DomResultScore[] theHIndex) {
        vIndex = theVIndex;
        hIndex = theHIndex;
        marks = new DomResultScore[vIndex.length][hIndex.length];
    }

    /**
     * @param i row
     * @param j column
     * @return the marks
     */
    public DomResultScore getMark(int i, int j) {
        return marks[i][j];
    }

    /**
     * @param i row index
     * @param j column index
     * @param mark the mark to set
     */
    public void setMarks(int i, int j, DomResultScore mark) {
        this.marks[i][j] = mark;
    }

    public int getvSize() {
        return vIndex.length;
    }

    public int gethSize() {
        return hIndex.length;
    }

    /**
     * @param i row index
     * @return the vIndex value
     */
    public DomResultScore getvIndex(int i) {
        return vIndex[i];
    }

    /**
     * @param i column index
     * @return the hIndex value
     */
    public DomResultScore gethIndex(int i) {
        return hIndex[i];
    }

    
    @Override
    public String toString() {
        return toString(1);
    }    
    
    public String toString(int divider) {
        StringBuilder buf = new StringBuilder();
        //build header
        buf.append(StringFormatter.format("\n%50s", "vlabels\\hlabels").getValue()); //empty field
        for (int i = 0; i < hIndex.length; i++) {
        if(hIndex[i]!=null && hIndex[i].getLabel()!=null){
                buf.append(StringFormatter.format("%50s", hIndex[i].getLabel()).getValue());
                }else{
                    buf.append(StringFormatter.format("%50s", hIndex[i].getLabel()).getValue());
                }
        }
        buf.append('\n');
        //build body plus vertical labels
        for (int j = 0; j < vIndex.length; j++) {
        if(vIndex[j]!=null && vIndex[j].getLabel()!=null){
                buf.append(StringFormatter.format("%50s", vIndex[j].getLabel()).getValue());
                }else{
                    buf.append(StringFormatter.format("%50s", "null").getValue());
                }

            for (int i = 0; i < hIndex.length; i++) {
                //row, column 
                if(marks[j][i]!=null && marks[j][i].getScore()!=null 
                        && marks[j][i].getScoCount()>0.0){
                buf.append(StringFormatter.format("%50f", marks[j][i].getScore()/divider/marks[j][i].getScoCount()).getValue());
                }else{
                    buf.append(StringFormatter.format("%50s", "null").getValue());
                }
            }
            buf.append('\n');
        }
        return buf.toString();
    }
}
