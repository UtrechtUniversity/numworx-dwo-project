package nl.uu.fi.dwo.rest.dom;

import java.util.Formatter;

import nl.uu.fi.dwo.rest.dom.entities.DomResultScore;

public class MatrixPlotter extends DomResultPlotMatrix {

	DomResultPlotMatrix delegate;
	
	public DomResultScore getMark(int i, int j) {
		return delegate.getMark(i, j);
	}
	public int getvSize() {
		return delegate.getvSize();
	}
	public int gethSize() {
		return delegate.gethSize();
	}
	public DomResultScore getvIndex(int i) {
		return delegate.getvIndex(i);
	}
	public DomResultScore gethIndex(int i) {
		return delegate.gethIndex(i);
	}
	public MatrixPlotter(DomResultPlotMatrix org) {
		super(new DomResultScore[0], new DomResultScore[0]);
		delegate = org;
	}
	//
  @Override
  public String toString() {
      return toString(1);
  }

  public String toString(int divider) {
      StringBuilder buf = new StringBuilder();
      Formatter formatter = new Formatter(buf);// Local not in GWT, Locale.getDefault());
      //build top row containing horizontal labels
      formatter.format("\n%50s", "vlabels\\hlabels"); //empty field
      for (int i = 0; i < gethSize(); i++) {
          if (gethIndex(i) != null && gethIndex(i).getLabel() != null) {
              formatter.format("%50s", gethIndex(i).getLabel());
          } else {
              formatter.format("%50s", gethIndex(i).getLabel());
          }
      }
      buf.append('\n');
      //build body plus leading vertical labels
      for (int j = 0; j < getvSize(); j++) {
          if (getvIndex(j) != null && getvIndex(j).getLabel() != null) {
              formatter.format("%50s", getvIndex(j).getLabel());
          } else {
              formatter.format("%50s", "null");
          }

          for (int i = 0; i < gethSize(); i++) {
              //row, column 
              final DomResultScore mark = getMark(j,i);
              if (mark != null && mark.getScore() != null) {
              formatter.format("%50f", mark.getScore());
//                  if (getMark(i,j).getScoCount() > 0.0) {
//                      formatter.format("%50f", getMark(i,j).getScore() / divider );
//                  } else if (getMark(i,j).getStudentScoCount() > 0.0) {
//                      formatter.format("%50f", getMark(i,j).getScore() / divider);
//                  } else {
//                      formatter.format("%50s", "null");
//                  }
              } else {
                  formatter.format("%50s", "null");
              }
          }
          buf.append('\n');
      }
      return buf.toString();
  }
}
