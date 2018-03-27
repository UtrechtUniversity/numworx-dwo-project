package fi.dwo.server.xss;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class HTTPFilter implements Filter {

	@Override
	public void destroy() {
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		if(request.isSecure() || request.getLocalAddr().equals("127.0.0.1"))
		{
			chain.doFilter(request, response);
			return;
		}
		HttpServletRequest req = (HttpServletRequest) request;		
		String uri = req.getRequestURI();
		if(!isException(uri)) {
			StringBuffer sb = new StringBuffer("https://");
			sb.append(req.getServerName());
			sb.append(uri);			
			HttpServletResponse res = (HttpServletResponse) response;
			res.sendRedirect(sb.toString());
			return;
		}
		chain.doFilter(request, response);
	}

	private boolean isException(String uri) {
		return uri.contains("rest")||uri.contains("xmlrpc");
	}

	@Override
	public void init(FilterConfig arg0) throws ServletException {
	}

}
