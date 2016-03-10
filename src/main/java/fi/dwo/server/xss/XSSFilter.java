package fi.dwo.server.xss;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class XSSFilter implements Filter {

	@Override
	public void destroy() {
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		String method;
		Logger logger = java.util.logging.Logger.getLogger(getClass().getName());
		logger.info("doFilter called");
		if(request instanceof HttpServletRequest)
		{
			method = ((HttpServletRequest) request).getMethod();
			if("OPTIONS".equals(method) && response instanceof HttpServletResponse)
			{
				doOptions((HttpServletRequest)request, (HttpServletResponse) response);
				return;
			}
		}
		if(response instanceof HttpServletResponse)
		{
			HttpServletResponse res = (HttpServletResponse) response;
			HttpServletRequest  req = (HttpServletRequest) request;
			String origin = req.getHeader("Origin");
			if(origin == null) origin = "*";
			res.setHeader("Access-Control-Allow-Origin", origin);
			res.setHeader("Access-Control-Expose-Headers", "content-type");
			res.setHeader("Access-Control-Allow-Credentials", "true");
		}
		
		chain.doFilter(request, response);

	}

	@Override
	public void init(FilterConfig config) throws ServletException {
	}

	private void doOptions(HttpServletRequest request, HttpServletResponse response) throws IOException
	{
		response.setHeader("Access-Control-Allow-Origin", "*");
		response.setHeader("Access-Control-Allow-Methods", "GET, PUT, POST, OPTIONS");
		response.setHeader("Access-Control-Expose-Headers", "content-type");
		response.setHeader("Access-Control-Allow-Headers", "origin, content-type");
		response.setHeader("Access-Control-Allow-Credentials", "true");

		response.setContentType("text/plain");
		response.getOutputStream().close();
	}
	
	
}
