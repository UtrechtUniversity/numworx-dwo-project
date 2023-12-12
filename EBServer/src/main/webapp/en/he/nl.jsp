<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import='java.util.regex.*' %>
<!DOCTYPE html>
<html>
<head>
<%
String hash = request.getParameter("hash");
String player = "/gwtclient/index.html";
String token = "/dwo/saml/login";
String dwo_env = System.getProperty("DWO_ENV","app");
if (dwo_env.contains("uu")) token = "/dwo/oauth2/mfalogin";

if ( hash != null && (hash.isEmpty() || Pattern.matches("#[a-z]+:\\d*", hash))) // Deeplink
	player = "/dwo/tablet/DWOplayer.jsp";
else
	hash = "";
%>
<meta charset="UTF-8">
<meta name="apple-mobile-web-app-capable" content="yes">
<meta name="viewport" content="width=1024">
<title>Numworx Higher Education (NL)</title>
    <link type="text/css" rel="stylesheet" href="/dwo/oauth2client/OAuth2Client.css">
    <script>
    	endpoint = "<%=player%>"
    	search = "&base=/en/he/&profile=100&locale=nl"
    	hash= "<%=hash%>"
        clientId = "f9af29c4-cfc5-11ea-87d0-0242ac130003"
        token="<%=token%>"
    </script>
    <script type="text/javascript" src="/dwo/oauth2client/oauth2client/oauth2client.nocache.js"></script>
</head>
<body>

</body>
</html>