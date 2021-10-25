<html>
<head>
  <title>IMS Basic Learning Tools Interoperability</title>
</head>
<body style="font-family:sans-serif">
<img src="http://www.sun.com/images/l2/l2_duke_java.gif" align="right">
<p><b>IMS BasicLTI Java Provider</b></p>
<p>This is a very simple reference implementaton of the tool side (i.e. provider) for IMS BasicLTI.</p>
<p>This tool is configured with an LMS-wide guid of "lmsng.school.edu" protected by a secret of "secret".
For this tool, all resource level secrets are also "secret".</p>
</p>
<%@ page import="javax.servlet.http.HttpServletRequest" %>
<%@ page import="java.util.Enumeration" %>
<%@ page import="io.jsonwebtoken.*" %>
<%@ page import="fi.servlet.lti.*" %>
<%@ page import="edu.uoc.elc.lti.tool.*" %>
<%@ page import="edu.uoc.elc.lti.platform.ags.*" %>
<%@ page import="edu.uoc.elc.lti.platform.*" %>
<pre>

<%! 

		
	void println(JspWriter out, Object o ) {
		try {
			out.println(o);
		} catch(Exception io) {
			
		}
}
%>
<%
  Tool tool = (Tool) session.getAttribute("tool");

  Enumeration<String> en = request.getParameterNames();
  while (en.hasMoreElements()) {
    String paramName = (String) en.nextElement();
    out.println(paramName + " = " + request.getParameter(paramName) );
  }

  String token = request.getParameter("id_token");
  String state = request.getParameter("state");
  boolean valid = tool.validate(token, state);
  if (valid) {
    out.println("valid");
    if (tool.isDeepLinkingRequest()) {
    	
    } else {
    	out.println(tool.getAccessToken().getAccessToken());
    	AgsClientFactory ags = tool.getAssignmentGradeServiceClientFactory();
    	NamesRoleService nameroles = tool.getNameRoleService();
    	out.println(nameroles.getContext_memberships_url());
    }
  } else {
    out.println("invalid");
    out.println(tool.getReason());
  }
  String return_url = tool.getPresentation().getReturnUrl();
  String provider   = tool.getPlatform().getName();
  String sco  = tool.getCustomParameter("sco");
  String course = tool.getCustomParameter("course");
%>
</pre>
<p> return to <a href='<%=return_url%>'><%=provider%></a>

<p>display course <%=course %> and sco <%=sco %> here ....
