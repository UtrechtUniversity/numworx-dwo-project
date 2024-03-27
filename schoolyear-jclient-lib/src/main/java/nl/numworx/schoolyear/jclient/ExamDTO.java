package nl.numworx.schoolyear.jclient;

import java.util.Date;

public class ExamDTO {
	public String id;
	public Object shortcode;
	public String display_name;
	public Date start_time, end_time;
	public Boolean archived;
	public Integer expected_workspaces;
	public Workspace workspace;
	public Boolean pre_onboarding;
	public Object app_assignment_config;
	public String pin;
	
}
