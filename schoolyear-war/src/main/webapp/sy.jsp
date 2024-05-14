<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%
Cookie cookie = new Cookie("dwoSAMLUserID","guest");
cookie.setMaxAge(60*60*24);
response.addCookie(cookie);
cookie = new Cookie("dwoSAMLOrganizationID","uu.nl");
cookie.setMaxAge(60*60*24);
response.addCookie(cookie);
%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Numworx</title>
<link type="text/css" rel="stylesheet" href="/dwo/oauth2client/OAuth2Client.css">
    <script>
    	endpoint = "/sy/inner.jsp"
    	search = "?locale=en&base=/en/he/"
    	hash= ""
        clientId = "f9af29c4-cfc5-11ea-87d0-0242ac130003"
        token="/dwo/oauth2/uulogin"
    </script>
    <script type="text/javascript" src="/dwo/oauth2client/oauth2client/oauth2client.nocache.js"></script>
</head>
<body>

</body>
</html>