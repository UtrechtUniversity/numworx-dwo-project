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
<%@ page import="edu.uoc.lti.deeplink.content.*" %>
<%@ page import="edu.uoc.elc.lti.tool.*" %>
<%@ page import="edu.uoc.elc.lti.platform.ags.*" %>
<%@ page import="edu.uoc.elc.lti.platform.*" %>
<%@ page import="edu.uoc.elc.lti.platform.deeplinking.*" %>
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
  String jwt   = "";
  String url   = "";
  DeepLinkingClient client = tool.getDeepLinkingClient();
  
  boolean valid = tool.validate(token, state);
  if (valid) {
    out.println("valid");
	url = client.getReturnUrl().toExternalForm();
	LtiResourceItem item = LtiResourceItem.builder()
			.title("DWOmAccess configured")
			.url("http://localhost:8081/DWOmAccess/lti/tool13.jsp")
			.build();
	client.addItem(item);
	
	
  	jwt = client.buildJWT();
  
  } else {
    out.println("invalid");
    out.println(tool.getReason());
  }
%>
</pre>


<form method="post" action="<%=url %>" >

<input name="jwt" value="<%=jwt%>" >
<input type="submit">
</form>

</body>
</html>