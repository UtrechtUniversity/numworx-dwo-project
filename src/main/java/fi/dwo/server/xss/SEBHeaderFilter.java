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

import fi.dwo.server.rest.PublicRestTestManager;

public class SEBHeaderFilter implements Filter {

	private static final String REQUESTHASH = "X-SafeExamBrowser-RequestHash";
	
	private String[] keys;
	
	
	@Override
	public void destroy() {
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		doHttpFilter( (HttpServletRequest) request, (HttpServletResponse) response, chain);

	}

	private void doHttpFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
		String uri = request.getRequestURL().toString();
		if(isSEB(uri))
		{
			String header = request.getHeader(REQUESTHASH);
			boolean result = PublicRestTestManager.verifySEBHeader(header, uri, keys);
			if(!result) {
				response.setStatus(HttpServletResponse.SC_FORBIDDEN);
				return;
			}
		}
		chain.doFilter(request, response);
	}

	private boolean isSEB(String uri) {
		
		return uri.contains("toets.jsp");
	}

	@Override
	public void init(FilterConfig arg0) throws ServletException {
		keys = new String[] { 
				"acd95f0b55edb444702d17a644604459ede2cb0678db8ab43a9d6d3e25dac062", // MAC /toets/leerling.seb
				"dbace2d457dad560309ad4300cc8d2e23ba75ea1cab7c1b9928ad343fab6fb1f"  // WIN /toets/leerling.seb

		};

	}

}
