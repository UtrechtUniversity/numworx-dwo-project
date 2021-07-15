package fi.dwo.server.db;

import java.io.CharArrayWriter;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Writer;
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

public class GwtClientWrap implements Filter {

	
	static class MyOutput extends FilterServletOutputStream {

		public MyOutput(OutputStream out) {
			super(out);
		}
		
	}
	
	static class MyWrapper extends HttpServletResponseWrapper {

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
	}



	private static final String SAMLSTART = "<!--SAMLSTART-->";
	private static final String SAMLEND= "<!--SAMLEND-->";
	private boolean saml;
	
	
	final Logger LOG = Logger.getLogger(getClass().getName());
	final SecureRandom random = new SecureRandom();
	
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		saml = System.getProperty("DWO_ENV", "app").contains("saml");
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
			}
			resp.setContentType("text/html;charset=UTF-8");
			resp.setCharacterEncoding("UTF-8");
			resp.getWriter().write(content);
		}

	}

	@Override
	public void destroy() {

	}

}
