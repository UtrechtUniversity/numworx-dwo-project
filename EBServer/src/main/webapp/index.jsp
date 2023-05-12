<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<title>DWO: Digitale Wiskunde Omgeving | Home</title>
<link type="text/css" rel="stylesheet" href="index.css">
</head>
<body>
    <div id="header" >
    <a href="https://www.numworx.nl" ><img src="logo-Numworx-grijs2.svg" alt="Numworx"></a>
    </div>
    <p>
    Ga naar/Go to:
    <ul>
	<li><a href="/vo/">VO</a> voor leerling en voor docent/schooladmin</li>
	<li><a href="/ho/">HO</a> voor student en voor  docent/schooladmin</li>
	<li><a href="/en/se/">SE (English)</a> for pupil and teacher or school admin</li>
	<li><a href="/en/he/">HE (English)</a> for student and teacher or school admin</li>
    </ul>
    <p>Apps:</p>
    <ul>
	<li><a href="/dwo/rest/public/user/requestNewPassword?language=nl&back=/">Wachtwoord vergeten</a></li>
	<li><a href="/dwo/rest/public/user/requestNewPassword?language=en&back=/">Forgotten password</a></li>
	<li>De auteursomgeving als <a href='https://www.numworx.nl/help/downloads/'>applicatie</a> downloaden</li>
    </ul>
    <div id='footer'>
        <iframe src="/dwo/rest/public/status/version" height="34" width="450" >
    </div>

</body>
</html>