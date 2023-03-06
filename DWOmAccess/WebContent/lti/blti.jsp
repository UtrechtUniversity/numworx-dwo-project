<html>
<head>
  <title>IMS Basic Learning Tools Interoperability</title>
</head>
<body>
<%@ page import="org.imsglobal.basiclti.BasicLTIUtil" %>
<%@ page import="java.util.Properties" %>
<%@ page import="javax.servlet.http.HttpServletRequest" %>
<%@ include file="blti_util.jsp" %>
<%

  String cur_url = request.getRequestURL().toString();

  String secret = request.getParameter("secret");
  String key = request.getParameter("key");
  if ( key == null ) key = "12345";
  String org_id = request.getParameter("org_id");
  if ( org_id == null ) org_id = "lmsng.school.edu";
  String org_desc = request.getParameter("org_desc");
  if ( org_desc == null ) org_desc = "University of School";
  String org_secret = request.getParameter("org_secret");
  if ( secret == null && org_secret == null) secret = "secret";
  if ( org_secret == null ) org_secret = "";
  String endpoint = request.getParameter("endpoint");
  if ( endpoint == null ) endpoint = cur_url.replace("blti.jsp","provider");
  String urlformatstr = request.getParameter("format");
  boolean urlformat = urlformatstr == null || ! urlformatstr.equals("XML");
  String lmspwstr = request.getParameter("lmspw");
  boolean lmspw = lmspwstr == null || ! lmspwstr.equals("Resource");

  String default_desc = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
    "<basic_lti_link xmlns=\"http://www.imsglobal.org/services/cc/imsblti_v1p0\" \n" +
    "  xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n" +
    "  <title>A Simple Descriptor</title>\n" +
    "  <custom>\n" +
    "    <parameter key=\"Cool:Factor\">120</parameter>\n" +
    "  </custom>\n" +
    "  <launch_url>CUR_URL</launch_url>\n" +
    "</basic_lti_link>\n".replace("CUR_URL",cur_url.replace("blti.jsp", "provider"));

  // To keep roundtrips from adding backslashes to double quotes
  String xmldesc = request.getParameter("xmldesc");
  if ( xmldesc == null ) xmldesc = default_desc;
  // xmldesc = str_replace("\\\"","\"",$_REQUEST["xmldesc"]);   
  // Ignore the organizational info if this is not an LMS password
  if ( ! lmspw ) org_id = null;

  Properties info = new Properties();
  Properties postProp = new Properties();
  if ( urlformat ) {
    getLMSDummyData(postProp);
    postProp = overrideLMSData(postProp,request.getParameterMap());
  } else {
    if ( BasicLTIUtil.parseDescriptor(info, postProp, xmldesc) ) {
      getLMSDummyData(postProp);
      endpoint = info.getProperty("launch_url");
      if ( endpoint == null ) {
        out.println("<p>Error, did not find a launch_url or secure_launch_url in the XML descriptor</p>\n");
        return;
      }
      endpoint = endpoint.replace("CUR_URL",cur_url.replace("blti.jsp", "provider"));
    }
  }

  // Off to the races with BasicLTI...
  if ( org_secret.equals("") ) org_secret = null;
  postProp = BasicLTIUtil.signProperties(postProp, endpoint, "POST", key, secret, org_secret, org_id, org_desc);
  String postData = BasicLTIUtil.postLaunchHTML(postProp, endpoint, false);
  out.println(postData);

%>

