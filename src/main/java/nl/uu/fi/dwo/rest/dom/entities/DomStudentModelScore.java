package nl.uu.fi.dwo.rest.dom.entities;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * A StudentModelStructure score root node. 
 * 
 * @author plas0006
 * @param <T>
 */
@SuppressWarnings("rawtypes")
@XmlRootElement
public class DomStudentModelScore<T extends DomStudentModelScore> {

    private List<T> children = new ArrayList<T>();
    
    private double redScore, greenScore;
    private long redCount, greenCount;

    
    /**
     * @return the score
     */
    public double getScore() {
        return redScore+greenScore;
    }

    /**
     * @param score the score to set
     */
    public void setScore(double score) {
        if (score < 0.5) { redScore = score;greenScore = 0; redCount = 1; greenCount = 0; }
        else { greenScore = score; redScore = 0; greenCount = 1; redCount = 0; }
    }


    /**
     * @return the count
     */
    public long getCount() {
        return redCount + greenCount;
    }
            
    public void setScore(double greenScore, long greenCount, double redScore, long redCount) {
      this.greenScore = greenScore;
      this.greenCount = greenCount;
      this.redScore = redScore;
      this.redCount = redCount;     
    }
    
    
    /**
     * @return the children
     */
    protected List<T> getChildren() {
        return children;
    }

    /**
     * @param children
     */
    protected void setChildren(List<T> children) {
        this.children = children;
    }

    public double getRedScore() {
      return redScore;
    }

    public double getGreenScore() {
      return greenScore;
    }

    public long getRedCount() {
      return redCount;
    }

    public long getGreenCount() {
      return greenCount;
    }

    public void recalculateAncestors() {
        List<T> children = getChildren();
        if (children != null) {
          int c = children.size();
          if (c > 0) {
            double redScore = 0, greenScore = 0;
            long redCount = 0, greenCount = 0;
            for(DomStudentModelScore child: children) {
              child.recalculateAncestors();
              long rc = child.getRedCount();
              long gc = child.getGreenCount();
              double rs = child.getRedScore();
              double gs = child.getGreenScore();
              if (rc == 0L) rs = 0.5;
              if (gc == 0L) gs = 0.5;
              redScore += rs;
              greenScore += gs;
              redCount += rc;
              greenCount += gc;
            }
            if (greenCount == 0) { greenScore = 0.0; } else { greenCount = c; }
            if (redCount == 0)   { redScore = 0.0; } else { redCount = c; }
            setScore(greenScore, greenCount, redScore, redCount);
          } else {
            setScore(0.0,0L,0.0,0L);
          }
        }
      
    }

}
