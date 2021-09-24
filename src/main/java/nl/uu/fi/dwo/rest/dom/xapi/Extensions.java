package nl.uu.fi.dwo.rest.dom.xapi;

import java.util.List;

import com.owlike.genson.annotation.JsonProperty;

public class Extensions {

  @JsonProperty("http://www.dwo.nl/extensions/objectives")
  @com.fasterxml.jackson.annotation.JsonProperty("http://www.dwo.nl/extensions/objectives")
  public List<String> objectives;

  @JsonProperty("http://www.dwo.nl/extensions/foreknowledge")
  @com.fasterxml.jackson.annotation.JsonProperty("http://www.dwo.nl/extensions/foreknowledge")
  public List<String> foreknowledge;

}
