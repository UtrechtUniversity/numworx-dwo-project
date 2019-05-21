package nl.uu.fi.dwo.rest.dom.entities;

import javax.xml.bind.annotation.XmlRootElement;

import nl.uu.fi.dwo.rest.dom.xapi.Agent;

@XmlRootElement
public class DomLRS {
  private String endpoint;
  private String auth;
  private Agent agent;

  public String getEndpoint() {
    return endpoint;
  }
  public void setEndpoint(String endpoint) {
    this.endpoint = endpoint;
  }
  public String getAuth() {
    return auth;
  }
  public void setAuth(String auth) {
    this.auth = auth;
  }
  public Agent getAgent() {
    return agent;
  }
  public void setAgent(Agent agent) {
    this.agent = agent;
  }
  
}
