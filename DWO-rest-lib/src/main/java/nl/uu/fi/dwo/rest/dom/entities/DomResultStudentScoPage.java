package nl.uu.fi.dwo.rest.dom.entities;

import nl.uu.fi.dwo.rest.dom.entities.util.DomResultScoreVisitor;

@SuppressWarnings("rawtypes")
public class DomResultStudentScoPage extends DomResultScore {

  @Override
  public String toString() {
    return "DomResultStudentScoPage [maxScore=" + maxScore + ", correctie=" + correctie
        + ", getScore()=" + getScore() + ", getLabel()=" + getLabel() + "]";
  }

  public DomResultStudentScoPage(String label) {
      setLabel(label);
  }

  @Override
  public String getId() {
    return getLabel();
  }

  Double maxScore, correctie;
  Float maxFactor;

public Double getMaxScore() {
	return maxScore;
}

public void setMaxScore(Double maxScore) {
	this.maxScore = maxScore;
}

public Double getCorrectie() {
	return correctie;
}

public void setCorrectie(Double correctie) {
	this.correctie = correctie;
}

public Float getMaxFactor() {
	return maxFactor;
}

public void setMaxFactor(Float float1) {
	this.maxFactor = float1;
}

@Override
public void calculateSumOfSubtreeScore() {
  
}

@Override
public void visit(DomResultScoreVisitor v) {
	v.visitStudentScoPage(this);
}
  
  
}
