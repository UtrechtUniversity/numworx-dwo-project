package nl.uu.fi.dwo.rest.dom.xapi;

import java.util.List;

import com.owlike.genson.annotation.JsonProperty;

public class Extensions {
  public final static String OBJECTIVES = "http://www.dwo.nl/extensions/objectives";
  public final static String FOREKNOWLEDGE = "http://www.dwo.nl/extensions/foreknowledge";
  public final static String ASSESSMENT_TYPE = "http://id.tincanapi.com/extension/assessment-type";
  public final static String FEEDBACK = "http://id.tincanapi.com/extension/feedback";
  public final static String GUESS = "http://www.dwo.nl/extensions/guess";
  
  @JsonProperty(OBJECTIVES)
  @com.fasterxml.jackson.annotation.JsonProperty(OBJECTIVES)
  public List<String> objectives;

  @JsonProperty(FOREKNOWLEDGE)
  @com.fasterxml.jackson.annotation.JsonProperty(FOREKNOWLEDGE)
  public List<String> foreknowledge;
  
  @JsonProperty(ASSESSMENT_TYPE)
  @com.fasterxml.jackson.annotation.JsonProperty(ASSESSMENT_TYPE)
  public String assessmentType;

  @JsonProperty(FEEDBACK)
  @com.fasterxml.jackson.annotation.JsonProperty(FEEDBACK)
  public String feedback;
  
  @JsonProperty(GUESS)
  @com.fasterxml.jackson.annotation.JsonProperty(GUESS)
  public Double guess;

}
