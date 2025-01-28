<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="nl.uu.fi.dwo.rest.util.Form" %>
<%!
	static final String IPRANGE = System.getProperty("ENV_IPRANGE", "");
	static final boolean needSEB = !Boolean.getBoolean("ENV_NOSEB");
%>
<%
	String useragent = request.getHeader("user-agent");
	String formfactor = Form.getFormFactor(useragent).name();
%> 