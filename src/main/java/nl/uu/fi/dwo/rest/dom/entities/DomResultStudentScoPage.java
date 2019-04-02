package nl.uu.fi.dwo.rest.dom.entities;

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

@Override
public void calculateSumOfSubtreeScore() {
  
}
  
  
}
