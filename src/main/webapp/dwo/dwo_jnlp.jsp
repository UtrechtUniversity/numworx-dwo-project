<%@ page language="java" contentType="application/x-java-jnlp-file"
    pageEncoding="UTF-8"%>
<% Object SERVLET = request.getAttribute("SERVLET");
	if(SERVLET == null)
	{
		response.sendError(response.SC_NOT_FOUND);
		return;
	}
	String language = request.getParameter("language");
	if(language == null) language = (String) request.getAttribute("language");
	Object profile = request.getAttribute("profile");
	String scheme = request.getScheme();
	String host = request.getServerName();
	String port = ":" + request.getServerPort();
	if(request.getServerPort() == 80) port = "";  // default http
	if(request.getServerPort() == 443) port = ""; // default https
	String server = scheme + "://" + host + port;
	String contextPath = "/dwo";
	String href = new StringBuffer(server).
// 			append("/dwo/dwojnlppage").			
// 			append('?').append(request.getQueryString()).toString();
		append(contextPath).
		append("/").append(profile).append('/').append(language).append("/dwo.jnlp").toString();
	//href = response.encodeURL(href);
%>
<?xml version="1.0" encoding="UTF-8"?>
<jnlp spec="1.0+" codebase="<%= server %><%=contextPath%>/" href="<%= href %>">
<%
	Object IDEAS   = request.getAttribute("IDEAS");
	Object guestUser = request.getAttribute("guestUser");
	Object extras   = request.getAttribute("extras");
	if(extras == null) extras = "";
// 	String scoViewNr = request.getParameter("scoViewNr");
// 	if(scoViewNr == null) scoViewNr = "";
// 	String courseViewNr = request.getParameter("courseViewNr");
// 	if(courseViewNr == null) courseViewNr = "";
// 	Object cookies = request.getAttribute("cookies");
// 	if(cookies == null) cookies = Boolean.FALSE;

%>
    <information>
        <title>Digitale Wiskunde Omgeving</title>
        <vendor>Universiteit Utrecht</vendor>
    </information>
    <resources>
        <j2se version="1.8+" href="http://java.sun.com/products/autodl/j2se"
        	java-vm-args="-Djava.net.preferIPv4Stack=true -Xmx1024m -Dhttps.protocols=TLSv1"
        	max-heap-size="1024m"
        />
		
        <jar href="DWOJApplet.jar"  main="true" />
		<property name="jnlp.packEnabled" value="false"/>
    </resources>
    <applet-desc  
    	name="DWO" 
    	main-class="fi.dwo.dwojapplet.domain.DWO"  
    	width="1024"  height="700"
    	documentBase="<%=server%><%=contextPath%>/"
    >
             <param name="language" value="<%=language%>"/>                
			 <param name="profile" value="<%= profile %>"/>
			 <param name="CAS" value="local" />
			 <param name="IDEAS" value="<%=IDEAS %>" />
			 <param name="guestUser" value="<%=guestUser %>" />
			 <%=extras %>
     </applet-desc>
	 <security>
		<all-permissions/>
	</security>
    <update check="background"/> 
</jnlp>

