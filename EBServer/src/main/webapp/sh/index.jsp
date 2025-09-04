<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="fi.dwo.server.db.TStamp" %>   
<%@ page import="fi.dwo.server.db.Util" %>
<!DOCTYPE html>
<% 
	int profile = 116;
	long tstamp = TStamp.BOOT;
	String query = request.getQueryString();
	if(Util.illegal(query))
	 	query = "?base=/sh/&locale=en&profile=" + profile + "&t=" + tstamp;
	else 
	  	query = "?base=/sh/&locale=en&profile=" + profile + "&" + query;
	String hash = request.getParameter("hash");
	String player = "/gwtclient/index.html";

	if (!Util.illegal(hash)) // Deeplink
		player = "/dwo/tablet/DWOplayer.jsp";
	else
		hash = "";
%>
<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
		<meta name="apple-mobile-web-app-capable" content="yes">
		<meta name="viewport" content="width=1078">
        <title>SH</title>
		<style type="text/css">
            body, html
            {
                margin: 0; padding: 0; height: 100%; overflow: hidden;
            }

            #content
            {
                position:absolute; left: 0; right: 0; bottom: 0; top: 0px; 
            }
        </style>
		<script type="text/javascript">
	 		function load() {
				var search = "<%=query%>"
				var hash = location.hash || "<%=hash%>";
				var id = search + hash;
				var element = document.getElementsByTagName("iframe")[0];
				element.setAttribute("src", "<%=player%>"+id);
		    }
	 		function logout() {
	 		}
		</script>
    </head>
    <body onload ="load()">
        <div id="content">
            <iframe width="100%" height="100%" frameborder="0" src="" allowfullscreen></iframe>
        </div>
    </body>
</html>
