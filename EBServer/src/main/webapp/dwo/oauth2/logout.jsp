<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<link type="text/css" rel="stylesheet" href="/uuindex.css">
<title>Logout</title>
</head>
<body>
<h1>Uitloggen</h1>
<ul>
	<li>
		<a class='logout' href="/dwo/saml/doLogout.jsp?return=/" target="_parent" >Naar het hoofdmenu, sessie afsluiten</a>
	</li>
	<li>
		<a class='nologout' href="/" target="_parent" >Naar het hoofdmenu zonder sessie af te sluiten</a>
	</li>
</ul>

<h1>Logout</h1>
<ul>
	<li>
		<a class='logout' href="/dwo/saml/doLogout.jsp?return=/" target="_parent" >To the main screen, ending your session </a>
	</li>
	<li>
		<a class='nologout' href="/" target="_parent" >To the main screen, keeping your session</a>
	</li>
</ul>
</body>
</html>