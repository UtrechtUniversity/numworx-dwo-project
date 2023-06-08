<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<% Object SERVLET = request.getAttribute("SERVLET");
	if(SERVLET == null)
	{
		response.sendError(response.SC_NOT_FOUND);
		return;
	}
%>
<HTML>
<HEAD>
</HEAD>
<BODY	bgcolor="#E6E7E9"
	leftmargin="0"
	topmargin="0"
	>
<center>
	Helaas, applets worden niet ondersteund in deze browser.
	Ga naar <a href="https://www.numworx.nl/help/downloads">
	numworx</a> om de NumworxAuthor applicatie te downloaden.
</center>
</BODY>
</HTML>
