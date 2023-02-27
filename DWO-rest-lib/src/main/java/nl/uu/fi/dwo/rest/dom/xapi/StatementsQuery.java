package nl.uu.fi.dwo.rest.dom.xapi;

public class StatementsQuery {
  public enum QueryResultFormat {
    IDS,
    EXACT,
    CANONICAL
}
  public Agent agent;
  public String verbID;
  public String activityID;
  public String registration;
  public Boolean relatedActivities;
  public Boolean relatedAgents;
  public String since;
  public String until;
  public Integer limit;
  public QueryResultFormat format;
  public Boolean attachments;
  public Boolean ascending;

}
