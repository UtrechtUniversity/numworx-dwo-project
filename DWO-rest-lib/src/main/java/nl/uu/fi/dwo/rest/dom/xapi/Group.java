package nl.uu.fi.dwo.rest.dom.xapi;

import java.util.Set;

public class Group {
  public Account account;
  public String name;
  /*final*/ public String objectType = "Group"; 
  public Set<Agent> member;
}
