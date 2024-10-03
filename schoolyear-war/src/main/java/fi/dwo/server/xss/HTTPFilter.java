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
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpUtils;

public class HTTPFilter implements Filter {

  Logger LOG = Logger.getLogger(HTTPFilter.class.getName());

  /**
   * prefix of load balancer address. E.g. 172.
   */
  private String prefix;
  private boolean redirect = true;


  static class BalancedServletRequest extends HttpServletRequestWrapper {

    private String remoteAddr, scheme;
    private int serverPort;
    private boolean secure;

    public BalancedServletRequest(HttpServletRequest request) {
      super(request);
      String remoteAddr = request.getHeader("x-forwarded-for");
      if (remoteAddr == null)
        remoteAddr = request.getRemoteAddr();
      else {
        int index = remoteAddr.lastIndexOf(',');
        if (index >= 0) {
          remoteAddr = remoteAddr.substring(index + 1);
        }
// Microsoft IIS adds :port to this remote addr, geen idee hoe een ipv6 port er uit ziet
        index = remoteAddr.lastIndexOf(':');
        if (index >= 0) {
      	  remoteAddr = remoteAddr.substring(0, index);
        }        
     }
      String serverPort = request.getHeader("x-forwarded-port");
      String scheme = request.getHeader("x-forwarded-proto"); // missing x-forwared-port
      if (serverPort == null && "https".equals(scheme)) {
    	  serverPort = "443";
      }

      this.remoteAddr = remoteAddr;
      this.serverPort = serverPort != null ? Integer.parseInt(serverPort) : request.getServerPort();
      this.scheme = scheme != null ? scheme : request.getScheme();
      this.secure = scheme != null ? "https".equals(scheme) : request.isSecure();
    }

    @Override
    public String getRemoteAddr() {
      return remoteAddr;
    }

    @Override
    public String getRemoteHost() {
      return remoteAddr;
    }


    @Override
    public String getScheme() {
      return scheme;
    }

    @Override
    public int getServerPort() {
      return serverPort;
    }

    @Override
    public boolean isSecure() {
      return secure;
    }

    @SuppressWarnings("deprecation")
    @Override
    public StringBuffer getRequestURL() {
      return HttpUtils.getRequestURL(this);
    }

  }

  @Override
  public void destroy() {}

  private boolean buggyUserAgent(ServletRequest req) {
    String ua = ((HttpServletRequest) req).getHeader("User-Agent");
    if(ua == null) return false;
    return ua.contains("JavaFX");
  }

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws IOException, ServletException {
    HttpServletRequest req = (HttpServletRequest) request;
    LOG.fine(
        "HTTP  " + req.getRequestURL() + " " + req.isSecure() + " from " + req.getRemoteAddr());
    req = balanced(req);

    LOG.fine("Balanced " + req.getRequestURL() + " " + req.isSecure() + " from "
        + req.getRemoteAddr() + " " + req.getClass().getName());
    request.setAttribute("remoteAddr", req.getRemoteAddr());

    if (req.isSecure() || req.getRemoteAddr().equals("127.0.0.1")
        || req.getRemoteAddr().equals("0:0:0:0:0:0:0:1") || buggyUserAgent(req)) {
      chain.doFilter(req, response);
      return;
    }

    String uri = req.getRequestURI();
    if (!isException(uri) && redirect) {
      StringBuffer sb = new StringBuffer("https://");
      sb.append(req.getServerName());
      sb.append(uri);
      String query = req.getQueryString();
      if (query != null)
    	  sb.append('?').append(query);
      HttpServletResponse res = (HttpServletResponse) response;
      // res.addHeader("xxxx-security", "....");
      res.sendRedirect(sb.toString());
      return;
    }
    chain.doFilter(req, response);
  }

  private HttpServletRequest balanced(HttpServletRequest req) {
    String balancer = req.getRemoteAddr();
    if (balancer.startsWith(prefix)) //
      return new BalancedServletRequest(req);
    return req;
  }

  private boolean isException(String uri) {
    return uri.contains("rest") || uri.contains("xmlrpc") || uri.contains("crossdomain.xml")
    //   || "/".equals(uri)
    ;
  }

  @Override
  public void init(FilterConfig config) throws ServletException {
    prefix = config.getInitParameter("prefix");
    if (prefix == null) prefix = System.getProperty("DWO_ELB", "172.");
    LOG.config("init prefix = " + prefix);
    redirect = Boolean.valueOf(System.getProperty("DWO_REDIRECT", "true"));
    LOG.config("init redirect = " + redirect);
  }

}
