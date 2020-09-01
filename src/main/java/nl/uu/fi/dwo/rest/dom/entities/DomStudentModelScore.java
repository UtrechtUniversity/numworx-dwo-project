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
    private long totalCount;
    
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
        totalCount = 1;
    }


    /**
     * @return the count
     */
    public long getCount() {
        return redCount + greenCount;
    }
    
    public long getTotalCount() {
    	return totalCount;
    }
            
    public void setScore(double greenScore, long greenCount, double redScore, long redCount) {
      this.greenScore = greenScore;
      this.greenCount = greenCount;
      this.redScore = redScore;
      this.redCount = redCount;
      totalCount = redCount + greenCount;
    }

    public void setScore(double greenScore, long greenCount, double redScore, long redCount, long totalCount) {
        this.greenScore = greenScore;
        this.greenCount = greenCount;
        this.redScore = redScore;
        this.redCount = redCount;
        this.totalCount = totalCount;
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
            long redCount = 0, greenCount = 0, totalCount = 0;
            for(DomStudentModelScore child: children) {
              child.recalculateAncestors();
              long rc = child.getRedCount();
              long gc = child.getGreenCount();
              long tc = child.getTotalCount();
              double rs = child.getRedScore();
              double gs = child.getGreenScore();
              redScore += rs;
              greenScore += gs;
              redCount += rc;
              greenCount += gc;
              totalCount += tc;
            }
            if (greenCount == 0) { greenScore = 0.0; }
            if (redCount == 0)   { redScore = 0.0; }
            setScore(greenScore, greenCount, redScore, redCount, totalCount);
          } else {
            setScore(0.0,0L,0.0,0L);
          }
        } else {
        	this.totalCount = 1;
        }
      
    }

}
