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

  double maxScore, correctie;

public double getMaxScore() {
	return maxScore;
}

public void setMaxScore(double maxScore) {
	this.maxScore = maxScore;
}

public double getCorrectie() {
	return correctie;
}

public void setCorrectie(double correctie) {
	this.correctie = correctie;
}

@Override
public void calculateSumOfSubtreeScore() {
  
}
  
  
}
