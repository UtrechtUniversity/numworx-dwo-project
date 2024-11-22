package nl.uu.fi.dwo.rest.dom.entities;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * A StudentModelStructure objective score node.
 *
 * @author plas0006
 */
@XmlRootElement
public class DomStudentModelObjectiveScore extends DomStudentModelScore<DomStudentModelObjectiveScore> {

   @Override
  public List<DomStudentModelObjectiveScore> getChildren() {
    return super.getChildren();
  }

  @Override
  public void setChildren(List<DomStudentModelObjectiveScore> children) {
    super.setChildren(children);
  }
    
  // Only if leaf.
  private Map<String, Double> variants = new HashMap<>();

	public DomStudentModelObjectiveScore() {
	super();
	defaultVariant();
}

	private void defaultVariant() {
		variants.put("", getScore());
	}	
	

	@Override
	public void setScore(double score) {
		super.setScore(score);
		defaultVariant();
	}

	@Override
	public void setScore(double greenScore, long greenCount, double redScore, long redCount) {
		super.setScore(greenScore, greenCount, redScore, redCount);
		defaultVariant();
	}

	@Override
	public void setScore(double greenScore, long greenCount, double redScore, long redCount, long totalCount) {
		super.setScore(greenScore, greenCount, redScore, redCount, totalCount);
		defaultVariant();
	}

	@Override
	public void setRedScore(double redScore) {
		super.setRedScore(redScore);
		defaultVariant();
	}

	@Override
	public void setGreenScore(double greenScore) {
		super.setGreenScore(greenScore);
		defaultVariant();
	}

	@Override
	public void setRedCount(long redCount) {
		super.setRedCount(redCount);
		defaultVariant();
	}

	@Override
	public void setGreenCount(long greenCount) {
		super.setGreenCount(greenCount);
		defaultVariant();
	}

	@Override
	public void setTotalCount(long totalCount) {
		super.setTotalCount(totalCount);
		defaultVariant();
	}

	public Map<String, Double> getVariants() {
		return variants;
	}
	
	public void setVariants(Map<String, Double> variants) {
		this.variants = variants;
	}
    
}
