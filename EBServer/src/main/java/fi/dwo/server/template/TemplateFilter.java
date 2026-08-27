/**
 * 
 */
package fi.dwo.server.template;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import fi.dwo.commons.persistence.MySQLPersistenceId;
import fi.dwo.server.db.SEBHosting;
import nl.uu.fi.dwo.lms.jclient.lib.rest.cache.PublicProfileCache;
import nl.uu.fi.dwo.rest.dom.entities.DomDwoProfileFull;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;

/**
 * 
 */
public class TemplateFilter implements Filter {
    final private Logger LOG = Logger.getLogger(getClass().getName());
    private String replacement = SEBHosting.HTTPS_APP_DWO_NL;

    class ResponseWrap extends HttpServletResponseWrapper {
    	
    	boolean notfound;
    	boolean output;

		public ResponseWrap(HttpServletResponse response) {
			super(response);
		}

		@Override
		public void sendError(int sc, String msg) throws IOException {
			notfound = sc == HttpServletResponse.SC_NOT_FOUND;
			if (!notfound)
				super.sendError(sc, msg);
		}

		@Override
		public void sendError(int sc) throws IOException {
			notfound = sc == HttpServletResponse.SC_NOT_FOUND;
			if (!notfound)
				super.sendError(sc);
		}

		@Override
		public void setStatus(int sc) {
			notfound = sc == HttpServletResponse.SC_NOT_FOUND;
			if (!notfound)
				super.setStatus(sc);
		}

		@Override
		public void setStatus(int sc, String sm) {
			notfound = sc == HttpServletResponse.SC_NOT_FOUND;
			if (!notfound)
				super.setStatus(sc, sm);
		}

		@Override
		public ServletOutputStream getOutputStream() throws IOException {
			output = true;
			return super.getOutputStream();
		}

		@Override
		public PrintWriter getWriter() throws IOException {
			output = true;
			return super.getWriter();
		}
    	
    }
    
    
	/**
	 * 
	 */
	public TemplateFilter() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		String pattern = filterConfig.getInitParameter("legal");
		if (pattern == null) pattern = "/([a-z]+\\/)*";
		legal = Pattern.compile(pattern);
		replacement = System.getProperty("ALLOW_ORIGIN", replacement);
		replacement = replacement.split("\\s+")[0];
		if ("*".equals(replacement)) replacement = SEBHosting.HTTPS_APP_DWO_NL;

	}

	Pattern legal;
	
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest) request;
		String url = req.getRequestURI();
		if (url.startsWith("/dwo/") || url.equals("/") || url.startsWith("/gwtclient/"))
		{
			chain.doFilter(request, response);
			return;
		}
		ResponseWrap wrap = new ResponseWrap((HttpServletResponse) response);
		chain.doFilter(request, wrap);
		if (wrap.notfound) {
			if (wrap.output) {
				notfound(wrap);
			} else {
				int index = url.lastIndexOf('/')+1;
				String prefix = url.substring(0, index);
				String suffix = url.substring(index);
				if (suffix.isEmpty()) suffix = "index.jsp";
				if (prefix.endsWith("/exam/")) {
					suffix = "exam/" + suffix;
					prefix = prefix.substring(0, prefix.length()-5);
				} else if ("exam".equals(suffix)) {
					wrap.sendRedirect(prefix + suffix + "/");
					return;
				}
				if (!legal.matcher(prefix).matches())
				{
					LOG.fine("Not found " + prefix);
					notfound(wrap);
					return;
				}
				
				
				try {
					DomDwoProfileFull profile = PublicProfileCache.get(prefix.substring(0, prefix.length()-1)); // bij voorbeeld....
					if (profile == null) {
						if (!legal.matcher(prefix + suffix + "/").matches())
						{
							notfound(wrap);
							return;
						}
						profile = PublicProfileCache.get(prefix + suffix);
						if (profile != null) {
							wrap.sendRedirect(prefix + suffix + "/");
						} else {
							notfound(wrap);						
						}
						return;
					}
					req.setAttribute("template.profile", profile);
					req.setAttribute("template.profile.id", MySQLPersistenceId.getNativeId(profile));
					req.setAttribute("template.url", url);
					req.setAttribute("template.prefix", prefix);
					req.setAttribute("template.locale", profile.getLanguage());
					req.setAttribute("template.title", profile.getTitle());
					req.setAttribute("template.server", replacement);
							
				} catch (Dwo2Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				String forward = "/WEB-INF/template/" + suffix;
				RequestDispatcher dispatch = req.getRequestDispatcher(forward);
				dispatch.forward(req, response);
			}
			
		}
		
		
	}

	protected void notfound(ResponseWrap wrap) throws IOException {
		((HttpServletResponse) wrap.getResponse()).sendError(HttpServletResponse.SC_NOT_FOUND);
	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub

	}

}
