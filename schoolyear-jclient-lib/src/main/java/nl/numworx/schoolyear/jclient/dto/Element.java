package nl.numworx.schoolyear.jclient.dto;

public class Element {
	public String type;
	public String origin;
// one of:	
	public WebPageUrl url;
	public WebPageEntireDomain url_entire_domain;
	public WebPageUrlRegex url_regex;
}
