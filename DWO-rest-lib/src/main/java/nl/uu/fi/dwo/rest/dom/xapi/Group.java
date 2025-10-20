package nl.uu.fi.dwo.rest.dom.xapi;

import java.util.Set;

public class Group {
  public static String GROUP = "Group";
  public Account account;
  public String name;
  /*final*/ public String objectType = GROUP; 
  public Set<Agent> member;
}
