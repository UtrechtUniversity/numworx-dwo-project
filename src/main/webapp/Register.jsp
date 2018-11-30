<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="javax.servlet.http.Cookie" %>
<%@ page import="javax.servlet.http.HttpServletResponse" %>
<!DOCTYPE html>
<html>
  <head>
<%!
private void cookie(String name, Object value, HttpServletResponse response) {
  if (value != null) {
    Cookie cookie = new Cookie(name, value.toString());
    response.addCookie(cookie);
  }
}
%>  
<%
Object name_given = request.getAttribute("givenName");
Object name_family = request.getAttribute("sn");
Object name_prefix = request.getAttribute("insertion");
Object email = request.getAttribute("mail");
Object org_id  = request.getAttribute("nlEduPersonHomeOrganizationId");	   

cookie("givenName", name_given, response);
cookie("familyName", name_family, response);
cookie("insertion", name_prefix, response);
cookie("email", email, response);
cookie("schoolLogin", org_id, response);

%>
    <meta http-equiv="content-type" content="text/html; charset=UTF-8">
    <link type="text/css" rel="stylesheet" href="Register.css">
    <meta name="gwt:property" content="locale=nl">
    <script>
    	free = false;
    </script>
    <title>Registratie nieuwe gebruiker</title>
    <script type="text/javascript" language="javascript" src="registergwt/registergwt.nocache.js"></script>
  </head>

  <body>

  </body>
</html>