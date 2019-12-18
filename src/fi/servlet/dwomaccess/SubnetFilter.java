package fi.servlet.dwomaccess;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class SubnetFilter implements Filter {

  String IPRANGE = System.getProperty("ENV_IPRANGE", "");
  boolean needSEB = !Boolean.getBoolean("ENV_NOSEB");
  private String subPathFilter = ".*";
  private Pattern pattern;

  @Override
  public void init(FilterConfig filterConfig) throws ServletException {
    IPRANGE = System.getProperty("ENV_IPRANGE", "");
    boolean needSEB = !Boolean.getBoolean("ENV_NOSEB");
    String subPathFilter = filterConfig.getInitParameter("subPathFilter");
    if (subPathFilter != null) {
      this.subPathFilter = subPathFilter;
    }
    pattern = Pattern.compile(this.subPathFilter);
    
  }

  public static String getFullURL(HttpServletRequest request) {
    // Implement this if you want to match query parameters, otherwise 
    // servletRequest.getRequestURI() or servletRequest.getRequestURL 
    // should be good enough. Also you may want to handle URL decoding here.
    return request.getRequestURI();
  }

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws IOException, ServletException {
    Matcher m = pattern.matcher(getFullURL((HttpServletRequest) request));
    if (m.matches()) {
        
      String host = request.getRemoteAddr();
      if ( ! Subnet.netMatchRange(IPRANGE, host) ) {
        forbidden(response);
        return;
      }
      if (needSEB) {
        HttpServletRequest req = (HttpServletRequest) request;
        String requestHash = req.getHeader("X-SafeExamBrowser-RequestHash"); 
        if (requestHash == null || requestHash.isEmpty()) {
          forbidden(response);
        }
        // Calculate hashes.
      }
      
    }
    chain.doFilter(request, response);
  }

  protected void forbidden(ServletResponse response) throws IOException {
    HttpServletResponse resp = (HttpServletResponse) response;
    resp.sendError(HttpServletResponse.SC_FORBIDDEN);
  }

  @Override
  public void destroy() {
  }

}
