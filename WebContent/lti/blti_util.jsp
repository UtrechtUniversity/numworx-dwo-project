<%@ page import="java.util.Properties" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.Iterator" %>

<%!
  // Setup some fake data from the LMS
  private java.util.Properties getLMSDummyData(Properties postProp) {
    postProp.setProperty("resource_link_id","120988f929-274612");
    postProp.setProperty("resource_link_title", "Weekly Blog");
    postProp.setProperty("resource_link_description", "A weekly blog");

    postProp.setProperty("user_id","292832126");
    postProp.setProperty("roles","Instructor");
    postProp.setProperty("lis_person_name_full","Jane Q. Public");
    postProp.setProperty("lis_person_name_given", "Jane");
    postProp.setProperty("lis_person_name_family", "Public");    
    postProp.setProperty("lis_person_contact_email_primary","user@school.edu");
    postProp.setProperty("lis_person_sourcedid","school.edu:user");

    postProp.setProperty("lis_outcome_service_url","http://www.staff.science.uu.nl/~velth101/lti/common/tool_consumer_outcome.php?b64=MTIzNDU6OjpzZWNyZXQ=");
    postProp.setProperty("lis_result_sourcedid"," feb-123-456-2929::28883");
    
    
    
    postProp.setProperty("context_id","456434513");
    postProp.setProperty("context_title","Design of Personal Environments");
    postProp.setProperty("context_label","SI182");
    
    postProp.setProperty("tool_consumer_info_product_family_code","ims");
    postProp.setProperty("tool_consumer_info_version","1.1");
    postProp.setProperty("tool_consumer_instance_guid","lmsng.school.edu");
    postProp.setProperty("tool_consumer_instance_description","University of School (LMSng)");
 
    postProp.setProperty("launch_presentation_locale","nl-NL");
    postProp.setProperty("launch_presentation_document_target","frame");
    postProp.setProperty("launch_presentation_width","800");
    postProp.setProperty("launch_presentation_height","600");
    postProp.setProperty("launch_presentation_css_url","http://www.staff.science.uu.nl/~velth101/lti/lms.css");
    postProp.setProperty("launch_presentation_return_url","http://www.staff.science.uu.nl/~velth101/lti/lms_return.php");
    
    
    
    return postProp;
  }


  private Properties overrideLMSData(Properties postProp, Map<String,String[]> parameters)
  {
	  Properties result = new Properties();
	  result.putAll(postProp);
	  Iterator<String> keys = postProp.stringPropertyNames().iterator();
	  while(keys.hasNext())
	  {
		  String key = keys.next();
		  String[] value = parameters.get(key);
		  if(value != null && value.length>0) {
			  result.setProperty(key, value[0]); // Single parameter!
		  }
	  }
	  String[] custom = parameters.get("custom");
	  if(custom != null)
	  {
		  for(int i = 0; i < custom.length; i++) {
			  String[] split = custom[i].split("\n");
			  for(int j = 0; j < split.length; j++) {
				  String[] kv = split[j].split("=",2);
				  result.setProperty("custom_" + kv[0], kv[1]);
			  }
		  }
	  }
	  return result;
  }


%>
