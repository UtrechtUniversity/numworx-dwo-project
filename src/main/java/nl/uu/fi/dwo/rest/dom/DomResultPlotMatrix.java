package nl.uu.fi.dwo.rest.dom;

//import java.util.Formatter;
//import java.util.Locale;
import java.util.Formatter;
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
//
//    @Override
//    public String toString() {
//        return toString(1);
//    }
//
//    public String toString(int divider) {
//        StringBuilder buf = new StringBuilder();
//        Formatter formatter = new Formatter(buf);// Local not in GWT, Locale.getDefault());
//        //build top row containing horizontal labels
//        formatter.format("\n%50s", "vlabels\\hlabels"); //empty field
//        for (int i = 0; i < hIndex.length; i++) {
//            if (hIndex[i] != null && hIndex[i].getLabel() != null) {
//                formatter.format("%50s", hIndex[i].getLabel());
//            } else {
//                formatter.format("%50s", hIndex[i].getLabel());
//            }
//        }
//        buf.append('\n');
//        //build body plus leading vertical labels
//        for (int j = 0; j < vIndex.length; j++) {
//            if (vIndex[j] != null && vIndex[j].getLabel() != null) {
//                formatter.format("%50s", vIndex[j].getLabel());
//            } else {
//                formatter.format("%50s", "null");
//            }
//
//            for (int i = 0; i < hIndex.length; i++) {
//                //row, column 
//                if (marks[j][i] != null && marks[j][i].getScore() != null) {
//                formatter.format("%50f", marks[j][i].getScore());
////                    if (marks[j][i].getScoCount() > 0.0) {
////                        formatter.format("%50f", marks[j][i].getScore() / divider );
////                    } else if (marks[j][i].getStudentScoCount() > 0.0) {
////                        formatter.format("%50f", marks[j][i].getScore() / divider);
////                    } else {
////                        formatter.format("%50s", "null");
////                    }
//                } else {
//                    formatter.format("%50s", "null");
//                }
//            }
//            buf.append('\n');
//        }
//        return buf.toString();
//    }
}
