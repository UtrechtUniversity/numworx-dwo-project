<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!doctype html>
<% String print = "Afdrukken"; // i18n
%>
<html>
  <head>
    <meta http-equiv="content-type" content="text/html; charset=UTF-8">
	<script type="text/javascript" src='deploy.jsp'></script>

    <script>css("PrintPlayer.css")</script>

    <title></title>
    <script>script("scripts/SCORM_2004_APIWrapper.js")</script>
    <script>script("PrintPlayer/PrintPlayer.nocache.js")</script>
  </head>

  <body class='printing'>
	  <p class="screenonly" >
		  <button class="printbutton" onclick="window.print()" ><%=print %></button>
	  </p>
  </body>
</html>
