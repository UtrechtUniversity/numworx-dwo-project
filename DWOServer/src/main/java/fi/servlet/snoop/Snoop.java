package fi.servlet.snoop;

/* $Id: SnoopServlet.java,v 1.5 2004/02/22 22:57:59 billbarker Exp $
*
*/
/*   
*  Copyright 1999-2004 The Apache Software Foundation
*
*  Licensed under the Apache License, Version 2.0 (the "License");
*  you may not use this file except in compliance with the License.
*  You may obtain a copy of the License at
*
*      http://www.apache.org/licenses/LICENSE-2.0
*
*  Unless required by applicable law or agreed to in writing, software
*  distributed under the License is distributed on an "AS IS" BASIS,
*  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
*  See the License for the specific language governing permissions and
*  limitations under the License.
*/

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.kstruct.gethostname4j.Hostname;
/**
*
*
* @author James Duncan Davidson 
* @author Jason Hunter 
*/
@SuppressWarnings("serial")
public class Snoop extends HttpServlet {

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		PrintWriter out = response.getWriter();
		response.setContentType("text/plain");
		String hostname = "localhost";
		try {
			hostname = Hostname.getHostname();
		} catch(Throwable oops) {
			
		}
		out.println("Snoop Servlet for " + hostname);
		Runtime runtime = Runtime.getRuntime();
		long max = r(runtime.maxMemory());
		long free = r(runtime.freeMemory());
		long total = r(runtime.totalMemory());
		long cpus = runtime.availableProcessors();
		out.println("total " + total + "k, max " + max + "k, free " + free + "k, cpu " + cpus);
		out.println();
		out.println("Servlet init parameters:");
		Enumeration<?> e = getInitParameterNames();
		while (e.hasMoreElements()) {
			String key = (String) e.nextElement();
			String value = getInitParameter(key);
			out.println("   " + key + " = " + value);
		}
		out.println();

		out.println("Context init parameters:");
		ServletContext context = getServletContext();
		Enumeration<?> enum1 = context.getInitParameterNames();
		while (enum1.hasMoreElements()) {
			String key = (String) enum1.nextElement();
			Object value = context.getInitParameter(key);
			if(key.toLowerCase().contains("password")) value = "**********";
			out.println("   " + key + " = " + value);
		}
		out.println();

/*		out.println("Context attributes:");
		enum1 = context.getAttributeNames();
		while (enum1.hasMoreElements()) {
			String key = (String) enum1.nextElement();
			Object value = context.getAttribute(key);
			out.println("   " + key + " = " + value);
		}
		out.println();
*/
		out.println("Request attributes:");
		e = request.getAttributeNames();
		while (e.hasMoreElements()) {
			String key = (String) e.nextElement();
			Object value = request.getAttribute(key);
			out.println("   " + HTMLFilter.filter(key) + " = " + value);
		}
//// AJP stuff
//		Collection<String> ajp = Arrays.asList(
//		  "Shib-Handler",
//		  "Shib-Application-ID",
//		  "Shib-Session-ID",
//		  "Shib-Identity-Provider",
//		  "Shib-Authentication-Instant",
//		  "Shib-Authentication-Method",
//		  "Shib-AuthnContext-Class",
//		  "Shib-Session-Index",
//		  "givenName",
//		  "mail",
//		  "sn",
//		  "uid",
//		  "insertion",
//		  "unscoped-affiliation",
//		  "studentNumber",
//		  "nlEduPersonHomeOrganizationId",
//		  "nlEduPersonHomeOrganization"
//		  
//		    );
//		for(String key: ajp) {
//          Object value = request.getAttribute(key);
//          out.println("   " + HTMLFilter.filter(key) + " = " + HTMLFilter.filter(String.valueOf(value)));
//		}
		
		
		out.println();
		out.println("Servlet Name: " + getServletName());
		out.println("Protocol: " + request.getProtocol());
		out.println("Scheme: " + request.getScheme());
		out.println("Server Name: " + HTMLFilter.filter(request.getServerName()));
		out.println("Server Port: " + request.getServerPort());
		out.println("Server Info: " + context.getServerInfo());
		out.println("Remote Addr: " + request.getRemoteAddr());
		out.println("Remote Host: " + request.getRemoteHost());
		out.println("Character Encoding: " + HTMLFilter.filter(request.getCharacterEncoding()));
		out.println("Content Length: " + request.getContentLength());
		out.println("Content Type: " + HTMLFilter.filter(request.getContentType()));
		out.println("Locale: " + HTMLFilter.filter(request.getLocale().toString()));
		out.println("Default Response Buffer: " + response.getBufferSize());
		out.println();
		out.println("Parameter names in this request:");
		e = request.getParameterNames();
		while (e.hasMoreElements()) {
			String key = (String) e.nextElement();
			String[] values = request.getParameterValues(key);
			out.print("   " + HTMLFilter.filter(key) + " = ");
			for (int i = 0; i < values.length; i++) {
				out.print(HTMLFilter.filter(values[i]) + " ");
			}
			out.println();
		}
		out.println();
		out.println("Headers in this request:");
		e = request.getHeaderNames();
		while (e.hasMoreElements()) {
			String key = (String) e.nextElement();
			String value = request.getHeader(key);
			out.println(HTMLFilter.filter("   " + key + ": " + value));
		}
		out.println();
		out.println("Cookies in this request:");
		Cookie[] cookies = request.getCookies();
		if (cookies != null)
			for (int i = 0; i < cookies.length; i++) {
				Cookie cookie = cookies[i];
				out.println(HTMLFilter.filter("   " + cookie.getName() + " = " + cookie.getValue()));
			}
		out.println();

		out.println("Request Is Secure: " + request.isSecure());
		out.println("Auth Type: " + request.getAuthType());
		out.println("HTTP Method: " + request.getMethod());
		out.println("Remote User: " + request.getRemoteUser());
		out.println("Request URI: " + request.getRequestURI());
		out.println("Context Path: " + request.getContextPath());
		out.println("Servlet Path: " + request.getServletPath());
		out.println("Path Info: " + HTMLFilter.filter(request.getPathInfo()));
		out.println("Path Trans: " + request.getPathTranslated());
		out.println("Query String: " + HTMLFilter.filter(request.getQueryString()));

		out.println();
		HttpSession session = request.getSession(true);
		out.println("Requested Session Id: " + HTMLFilter.filter(request.getRequestedSessionId()));
		if (session != null) {
			out.println("Current Session Id: " + session.getId());
			out.println("Session Created Time: " + session.getCreationTime());
			out.println("Session Last Accessed Time: " + session.getLastAccessedTime());
			out.println("Session Max Inactive Interval Seconds: " + session.getMaxInactiveInterval());
			Long counter = (Long) session.getAttribute("fi.servlet.snoop.counter");
			if (counter == null) counter = Long.valueOf(0L);
			else counter = Long.valueOf(counter.longValue()+1);
			session.setAttribute("fi.servlet.snoop.counter", counter);
			out.println();
			out.println("Session values: ");
			Enumeration<?> names = session.getAttributeNames();
			while (names.hasMoreElements()) {
				String name = (String) names.nextElement();
				out.println(HTMLFilter.filter("   " + name + " = " + session.getAttribute(name)));
			}
		}
	}

	private static long r(long n) {
		return (n + 512L)/1024L;
	}
}

final class HTMLFilter {


    /**
     * Filter the specified message string for characters that are sensitive
     * in HTML.  This avoids potential attacks caused by including JavaScript
     * codes in the request URL that is often reported in error messages.
     *
     * @param message The message string to be filtered
     */
    public static String filter(String message) {

        if (message == null)
            return (null);

        char content[] = new char[message.length()];
        message.getChars(0, message.length(), content, 0);
        StringBuffer result = new StringBuffer(content.length + 50);
        for (int i = 0; i < content.length; i++) {
            switch (content[i]) {
            case '<':
                result.append("&lt;");
                break;
            case '>':
                result.append("&gt;");
                break;
            case '&':
                result.append("&amp;");
                break;
            case '"':
                result.append("&quot;");
                break;
            default:
                result.append(content[i]);
            }
        }
        return (result.toString());

    }


}


