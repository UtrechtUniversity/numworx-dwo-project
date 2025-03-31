<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="nl.uu.fi.dwo.rest.util.Form" %>
<%@page import="java.net.URLEncoder"%>
<%!
	static final String IPRANGE = System.getProperty("ENV_IPRANGE", "");
	static final boolean needSEB = !Boolean.getBoolean("ENV_NOSEB");
%>
<%
	String useragent = request.getHeader("user-agent");
	String formfactor = Form.getFormFactor(useragent).name();
	String host = request.getRemoteAddr();
	String server = request.getHeader("host");
	String leerling = request.getParameter("id");
	String code = request.getParameter("a");
  	String seb;
  	if (request.isSecure()) seb = "sebs"; else seb = "seb";

  	if (code != null) {
	  code =  (needSEB? "?": "&") + "a=" + URLEncoder.encode(code);
  	} else 
	  code = "";
  	String id = "";
  	try { 
    	id = "?id=" + Long.parseLong(leerling);
  	} catch(Exception e) {
 		leerling = "leerling";
  	}
%> 