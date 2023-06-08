/**
 * 
 */
package fi.dwo.server.xss;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

/**
 * @author Wim van Velthoven
 *
 */
public class SecFilter implements Filter {

	public static final String USER_ID = "fi.dwo.server.xss.SecFilter.userId";
	public static final String SCHOOLGROUP_ID = "fi.dwo.server.xss.SecFilter.schoolGroupId";
	
	
	/* (non-Javadoc)
	 * @see javax.servlet.Filter#init(javax.servlet.FilterConfig)
	 */
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
	}

	/* (non-Javadoc)
	 * @see javax.servlet.Filter#doFilter(javax.servlet.ServletRequest, javax.servlet.ServletResponse, javax.servlet.FilterChain)
	 */
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		try {
			HttpServletRequest req  = (HttpServletRequest) request;
			String path = req.getRequestURI();
			int start = path.indexOf("/sec:1-");
			if (start >=0) {
				int end = path.indexOf("/", start+3);
				if (end < 0) end = path.length();
				path = path.substring(start, end);
				String[] split = path.split("-");
				Long userid = Long.valueOf(split[1]);
				Long sgid = Long.valueOf(split[2]);
				request.setAttribute(USER_ID, userid);
				request.setAttribute(SCHOOLGROUP_ID, sgid);
			}
		} catch (Exception e) {
		}
		chain.doFilter(request, response);
	}

	/* (non-Javadoc)
	 * @see javax.servlet.Filter#destroy()
	 */
	@Override
	public void destroy() {
	}

}
