package fi.servlet.dwomaccess;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
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

import fi.dwo.commons.persistence.entities.PersistentSchool;
import fi.dwo.server.rest.jaxrsfilters.DwoUserPrincipal;
import fi.dwo.server.rest.util.SchoolyearUtilManager;
import nl.numworx.schoolyear.jclient.SchoolyearClient;
import nl.numworx.schoolyear.jclient.dto.SignatureDTO;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;

public class SubnetFilter implements Filter {

  private static Logger LOG = Logger.getLogger(SubnetFilter.class.getName());
  
  String IPRANGE = System.getProperty("ENV_IPRANGE", "");
  boolean needSEB = !Boolean.getBoolean("ENV_NOSEB");
  private String subPathFilter = ".*";
  private Pattern pattern;

  @Override
  public void init(FilterConfig filterConfig) throws ServletException {
    IPRANGE = System.getProperty("ENV_IPRANGE", "");
    needSEB = !Boolean.getBoolean("ENV_NOSEB");
    String subPathFilter = filterConfig.getInitParameter("subPathFilter");
    if (subPathFilter != null) {
      this.subPathFilter = subPathFilter;
    }
    pattern = Pattern.compile(this.subPathFilter);
    
    LOG.info("IPRANGE = " + IPRANGE);
    LOG.info("needSEB = " + needSEB);
    LOG.info("pattern = " + subPathFilter);
    
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
    	LOG.warning("host not in range " + host);
        forbidden(response);
        return;
      }
      if (needSEB) {
        HttpServletRequest req = (HttpServletRequest) request;
 // schoolyear:
        String signature = req.getHeader("x-sy-signature");
        if (signature != null) {
        	LOG.info("check " + signature + " for " + req.getUserPrincipal().getName());
        	DwoUserPrincipal u = (DwoUserPrincipal) req.getUserPrincipal();
        	if (u != null) {
        		PersistentSchool school = u.getSg().getSchool();       		
        		try {
					SchoolyearClient client = SchoolyearUtilManager.build(school);
					SignatureDTO dto = new SignatureDTO();
					dto.x_sy_signature = signature;
					if (!client.validateSignature(dto)) {
						LOG.warning("school year validation failed");
						forbidden(response);
						return;
					}
				} catch (Dwo2Exception e) {
					LOG.log(Level.WARNING, "DWO exception ", e);
					forbidden(response);
					return;
				} catch (IOException e) {
					LOG.log(Level.WARNING, "IO exception ", e);
					
				}
        	}
        } else {
        String requestHash = req.getHeader("X-SafeExamBrowser-RequestHash"); 
        if (requestHash == null || requestHash.isEmpty()) {
        	LOG.warning("requestHash missing");
        	forbidden(response);
        	return;
        }
        // TODO Calculate hashes, see DWOServer
      }
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
