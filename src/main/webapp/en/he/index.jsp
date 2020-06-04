<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import='java.util.regex.*' %>
<!DOCTYPE html>
<% 
	int profile = 100;
	String locale = "en";
	String base = "/en/he/";
%>
<%@ include file="/dwo/index_util.jsp" %>
<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
		<meta name="apple-mobile-web-app-capable" content="yes">
		<meta name="viewport" content="width=1024">
        <title>Numworx Higher Education</title>
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
				var re = /^#[a-z]+:\d*$/;
				if (!re.test(hash)) hash = "";
				var id = search + hash;
				var element = document.getElementsByTagName("iframe")[0];
				element.setAttribute("src", "<%=player%>"+id);
		    }
	 		function logout() {
	 			window.location = "/dwo/saml/logout.jsp"
	 		}
		</script>
    </head>
    <body onload ="load()">
        <div id="content">
            <iframe width="100%" height="100%" frameborder="0" src="" allowfullscreen></iframe>
        </div>
    </body>
</html>
