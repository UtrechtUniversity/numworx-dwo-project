package nl.uu.fi.dwo.rest.dom.xapi;

import java.util.List;

public class Extensions {

  @com.owlike.genson.annotation.JsonProperty("http://www.dwo.nl/extensions/objectives")
  @com.fasterxml.jackson.annotation.JsonProperty("http://www.dwo.nl/extensions/objectives")
  public List<String> objectives;

}
