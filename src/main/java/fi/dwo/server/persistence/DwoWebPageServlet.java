/*
 * Created on Apr 5, 2005
 *
 */
package fi.dwo.server.persistence;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author M.J.B. Kupers
 * @author Wim van Velthoven
 * @web.servlet
 *   name="DwoWebPage"
 *   description="Servlet voor het genereren van de dwo.html pagina"
 * @web.servlet-init-param
 *   name="html_source"
 *   value="/dwo.jsp"
 * @web.servlet-init-param
 *  	name="SERVLET"
 *  	value="/dwo/dbaccess"
 * @web.servlet-mapping
 *     url-pattern="/dwowebpage"
 */
public class DwoWebPageServlet extends HttpServlet {

    private String HTML_SOURCE = "/dwo.jsp";
    private String SERVLET = "dsaccess"; // relatief t.o.v contextpath
    private String language = "nl";
    private Boolean guestUser = Boolean.FALSE;
    private String IDEAS = "/ideas/IdeasServlet";
    private Boolean cookies = Boolean.TRUE;
	private RequestDispatcher dispatch;
    
    /**
     * Haal parameter html_source op.
     * Andere parameters zijn SERVLET etc...
     * @see #HTML_SOURCE
     * @see fi.beans.xmlrpc.Servlet#init(javax.servlet.ServletConfig)
     */

    public void init() throws ServletException
    {
        String param = getInitParameter("html_source");
        if(param!=null)
            HTML_SOURCE = param;
        log("html_source = " + HTML_SOURCE);
        param = getInitParameter("SERVLET");
        if(param!=null)
        	SERVLET = param;
        param = getInitParameter("cookies");
        if(param != null)
        	cookies = Boolean.valueOf(param);
        param = getInitParameter("guestUser");
        if(param != null) 
        	guestUser = Boolean.valueOf(param);
        param = getInitParameter("language");
        if(param != null)
        	language = param;        
        param = getInitParameter("IDEAS");
        if (param != null)
        	IDEAS = param;
        
        dispatch = getServletContext().getRequestDispatcher(HTML_SOURCE);
    }

    /**
     * Maak een relatief path absoluut t.o.v contextPath
     * @param path
     * @param req
     * @return absolute path 
     */
    private String p(String path, HttpServletRequest req) {
    	if(path.startsWith("/"))
    		return path;
    	String contextPath = req.getContextPath();
    	return contextPath + "/" + path;
    }
    
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
// prepare
//		log("doGet started");
		req.setAttribute("SERVLET", p(SERVLET, req) );
		req.setAttribute("IDEAS", p(IDEAS, req));
		req.setAttribute("language", language);
// guestUser is a parameter
		String guestUserStr = req.getParameter("guestUser");
		Boolean guestUser = this.guestUser;
		try {
			if(guestUserStr != null)
				guestUser = Boolean.valueOf(guestUserStr);
		} catch(Exception e) {
			resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
			return;	
		}
		req.setAttribute("guestUser", guestUser);
	
		req.setAttribute("cookies", cookies);
// profile is missing -> NOT_FOUND		
		Object profile = req.getParameter("profile");
		if(profile == null)
		{
			profile = "1";
//			resp.sendError(resp.SC_NOT_FOUND);
//			return;
		} else 
			profile = profile.toString().trim();
		if(profile.toString().isEmpty()) profile=Integer.valueOf(1);
		else
		try {
			profile = new Integer(profile.toString());
		} catch(Exception e) {
			resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
			return;	
		}
		
		req.setAttribute("profile", profile);
		req.setAttribute("extras", "");
//		Enumeration<String> e = req.getAttributeNames();
//		while (e.hasMoreElements()) {
//			String object = e.nextElement();
//			log("doGet  " + object + " : " + req.getAttribute(object));
//		}
		
		if(dispatch == null)
		{
			log(HTML_SOURCE + " not found");
			resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
//		log("doGet dispatch");
		dispatch.forward(req, resp);
	}

}