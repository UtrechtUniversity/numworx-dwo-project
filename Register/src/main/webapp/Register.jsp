<%@ page import="java.net.URLEncoder"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="javax.servlet.http.Cookie" %>
<%@ page import="javax.servlet.http.HttpServletResponse" %>
<%@ page import="nl.uu.fi.dwo.lms.jclient.lib.rest.managers.*" %>
<%@ page import="nl.uu.fi.dwo.register.server.Manager" %>
<%@ page import="nl.uu.fi.dwo.rest.dom.entities.*" %>
<!DOCTYPE html>
<html>
  <head>
<%!
static final String DWO_SAML_ORGANIZATION_ID = "dwoSAMLOrganizationID";
static final String DWO_SAML_USER_ID = "dwoSAMLUserID";
private void cookie(String name, Object value, HttpServletResponse response) {
  if (value != null) {
	Cookie cookie = new Cookie(name, value.toString());
    response.addCookie(cookie);
  }
}

private Object u(Object value) {
	  try {
		  	value = URLEncoder.encode(value.toString(), "UTF-8").replaceAll("\\+", "%20");
		  } catch(Exception e) {}
	  return value;
}
%>  
<%

Manager manager = new Manager(getServletContext());
Object name_given = request.getAttribute("givenName");
Object name_family = request.getAttribute("sn");
Object name_prefix = request.getAttribute("insertion");
Object email = request.getAttribute("mail");
Object org_id  = request.getAttribute("nlEduPersonHomeOrganizationId");
// quickfix voor idptestbed
if (org_id == null) {
	  String schoolid = System.getProperty("ENV_ORGID", "project");
	  org_id = schoolid;
}

Object user_id = request.getAttribute("uid");
Object student_id = request.getAttribute("studentNumber");
// if (student_id == null) {
// 	student_id = user_id;
// }


String roles = String.valueOf(request.getAttribute("unscoped-affiliation"));
String role = "STUDENT";
if(roles != null && roles.toLowerCase().contains("employee"))
    role = "TEACHER";
String schoolCode = manager.getSchoolCode(org_id.toString(), role);

cookie("givenName", u(name_given), response);
cookie("familyName",u(name_family), response);
cookie("insertion", u(name_prefix), response);
cookie("email", u(email), response);
cookie("schoolLogin", schoolCode == null ? null : u(org_id), response);
cookie("suggestion", u(student_id), response); // XXX moet de nlEduPersonRealId zijn, zonder @suffix.
cookie("schoolGroup", role, response);
cookie("schoolCode", u(schoolCode), response);

cookie(DWO_SAML_ORGANIZATION_ID, org_id, response);
cookie(DWO_SAML_USER_ID, user_id, response);

String dwo_env = System.getProperty("DWO_ENV", "saml");

%>
    <meta http-equiv="content-type" content="text/html; charset=UTF-8">
    <link type="text/css" rel="stylesheet" href="Register.css">
    <meta name="gwt:property" content="locale=nl">
    <script>
    	free = false;
    	dwo_env = "<%=dwo_env%>"
    </script>
    <title>Registratie nieuwe gebruiker</title>
    <script type="text/javascript" language="javascript" src="registergwt/registergwt.nocache.js"></script>
  </head>

  <body>

  </body>
</html>