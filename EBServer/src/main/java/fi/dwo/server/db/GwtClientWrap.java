package fi.dwo.server.db;

import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.logging.Logger;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import org.tuckey.web.filters.urlrewrite.gzip.FilterServletOutputStream;

import nl.uu.fi.dwo.lms.jclient.lib.rest.cache.PublicProfileCache;
import nl.uu.fi.dwo.rest.dom.entities.DomDwoProfile;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;

public class GwtClientWrap implements Filter {

	
	static class MyOutput extends FilterServletOutputStream {

		public MyOutput(OutputStream out) {
			super(out);
		}
		
	}
	
	class MyWrapper extends HttpServletResponseWrapper {

//		MyOutput out;
		Writer writer;
		
		public MyWrapper(HttpServletResponse response) {
			super(response);
		}

		@Override
		public synchronized ServletOutputStream getOutputStream() throws IOException {
			throw new IllegalStateException(); // force use of getWriter
//			if (out == null) {
//				out = new MyOutput(super.getOutputStream());
//			}
//			return out;
		}

		@Override
		public synchronized PrintWriter getWriter() throws IOException {
			if (writer == null) 
				writer = new CharArrayWriter(10240*15);
			return new PrintWriter(writer);
		}

		@Override
		public void setContentLength(int len) {
//			LOG.info("discard content length " + len);
		}

		@Override
		public void setContentLengthLong(long len) {
//			LOG.info("discard content length long " + len);
		}
	}

	private static final String ENTREESTART = "<!--ENTREESTART-->";
	private static final String ENTREEEND = "<!--ENTREEEND-->";

	private static final String SAMLSTART = "<!--SAMLSTART-->";
	private static final String SAMLEND= "<!--SAMLEND-->";
	private static final String PROFILE= "<!--PROFILE_CSS-->";
	private static final String NEEDLOGOUT = "const needLogout = false";
	private static final String DOLOGOUT = "const needLogout = true";
	private static final String SEBHASH = "X-SafeExamBrowser-RequestHash";
	private boolean saml;
	private boolean entree;
	
	final Logger LOG = Logger.getLogger(getClass().getName());
	final SecureRandom random = new SecureRandom();
	
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		String env = System.getProperty("DWO_ENV", "app");
		saml = env.contains("saml");
		entree = env.contains("entree");
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		
		// post niet toegestaan
		HttpServletRequest http = (HttpServletRequest) request;
		HttpServletResponse resp = (HttpServletResponse) response;
		String method = http.getMethod();
		if ("POST".equals(method)) {
			resp.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
			return;
		}
		MyWrapper wrapper;
		wrapper = new MyWrapper(resp);

		String profile = request.getParameter("profile");
		if (profile == null) profile = "77"; // de default
		String name = profile;
		String cdn = System.getProperty("CDNURL", "http://cdn.dwo.nl");
		if (profile != null) {
			try {
				String key = profile;
				DomDwoProfile p = get(profile);
				
				if (p.getDwoProfileRights().contains("c"))
					name = p.getDwoProfileName();
				else
					name = null;
			} catch(Exception oops) {
				name = null;
			}
		}
		
		
		chain.doFilter(request, wrapper);
		if (wrapper.writer != null) {
			String content = wrapper.writer.toString();
			wrapper = null;
			content = content.replace("<form ", "<form method='post' ");
			content = content.replace("</form>", "<input type='hidden' value='" + random.nextLong()
					+ "' name='antiCSRFtoken' ></form>");
			if (saml) {			
				int index = content.indexOf(SAMLSTART);
				while (index >= 0) {
					int end = content.indexOf(SAMLEND, index);
					if (end >=0 ) content = content.substring(0, index) + content.substring(end);
					index = content.indexOf(SAMLSTART, index+1);
				}
				content = content.replace("type=\"password\"", "type=\"text\"");
				content = content.replace(NEEDLOGOUT, DOLOGOUT);
			} else if (null != http.getHeader(SEBHASH)) {
				content = content.replace(NEEDLOGOUT, DOLOGOUT);				
			}
			boolean off = !entree;
			boolean ho = false;
			if (!off) {
				try {
					DomDwoProfile p = get(profile);
					String rights = p.getDwoProfileRights();
					if (!rights.contains("O")) off = true;
					if (rights.contains("H")) ho = true;					
				} catch (Exception e) {
					off = true; // even niet
				}
				
			}
			if (off) {
				int index = content.indexOf(ENTREESTART);
				while (index >= 0) {
					int end = content.indexOf(ENTREEEND, index);
					if (end >=0 ) content = content.substring(0, index) + content.substring(end);
					index = content.indexOf(ENTREESTART, index+1);
				}				
			} else if (ho) {
				int index = content.indexOf(ENTREESTART);
				while (index >= 0) {
					int end = content.indexOf(ENTREEEND, index);
					if (end >=0 ) {
						String mid = content.substring(index, end);
// je kan altijd met alle hints inloggen
//						mid = mid.replace("entree", "conext"); // HO idphint
//						mid = mid.replace("entree-button-klein-donker", "SURFconext-logo");
						content = content.substring(0, index) + mid + content.substring(end);
					}
					index = content.indexOf(ENTREESTART, index+1);
				}				
				
				
			}
			if (name != null) {
				CharSequence replacement = "<link type=\"text/css\" rel=\"stylesheet\" href=\""+cdn+"/apps/css/"+name+".css\" >";
				content = content.replace(PROFILE, replacement );
			}
			
			resp.setContentType("text/html;charset=UTF-8");
			resp.setCharacterEncoding("UTF-8");
			byte[] utf8 = content.getBytes(StandardCharsets.UTF_8);
			resp.setContentLength(utf8.length);
			resp.getOutputStream().write(utf8);
		}

	}

	private DomDwoProfile get(String profile2) throws Dwo2Exception {
		DomDwoProfile p;
		p = PublicProfileCache.get(profile2);
		if (p == null) throw new Dwo2Exception();
		return p;
	}

	@Override
	public void destroy() {
		PublicProfileCache.clear();
	}

}
