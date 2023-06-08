package fi.dwo.server.xss;

import java.io.IOException;
import java.security.Principal;
import java.util.logging.Logger;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import fi.dwo.server.rest.util.Origin;

public class XSSFilter implements Filter {

    @Override
    public void destroy() {
    }

    Logger logger = java.util.logging.Logger.getLogger(getClass().getName());

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
            FilterChain chain) throws IOException, ServletException {
        String method;
        //logger.fine("doFilter called");
        if (request instanceof HttpServletRequest) {
            method = ((HttpServletRequest) request).getMethod();
            if ("OPTIONS".equals(method) && response instanceof HttpServletResponse) {
                doOptions((HttpServletRequest) request, (HttpServletResponse) response);
                return;
            }
        }
        if (response instanceof HttpServletResponse) {
            HttpServletResponse res = (HttpServletResponse) response;
            HttpServletRequest req = (HttpServletRequest) request;
            String origin;
            origin = Origin.of(req);
            res.setHeader("Access-Control-Allow-Origin", origin);
            res.setHeader("Access-Control-Expose-Headers", "content-type");
            res.setHeader("Access-Control-Allow-Credentials", "true");
            // ensure there is never any caching for now
            boolean rest = req.getRequestURI().contains("/rest/");
            boolean nocache = req.getRequestURI().contains(".nocache.");
            boolean cache = req.getRequestURI().contains(".cache.");
            if(rest || nocache) {
            	res.setHeader("Cache-Control", "no-cache, no-store, must-revalidate"); // HTTP 1.1.
            	res.setHeader("Pragma", "no-cache"); // HTTP 1.0.
            	res.setDateHeader("Expires", 0L); // Proxies.
            }
            if (cache) {
            	res.setDateHeader("Expires", System.currentTimeMillis() + 1000*60*60 );
            }
            res.setCharacterEncoding("UTF-8");
        }

        chain.doFilter(request, response);

    }

    @Override
    public void init(FilterConfig config) throws ServletException {
    }

    private void doOptions(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Principal u = request.getUserPrincipal();
        String a = request.getAuthType();
        String up = request.getHeader("Authorization");
        logger.info("doOptions: " + u + " " + a + " " + up + ": " + request.getRequestURI());

        String origin;
//        origin = request.getHeader("Origin");
//        if (origin == null) {
//            origin = "*";
//        }
//        origin = System.getProperty("ALLOW_ORIGIN", "*");
        origin = Origin.of(request);
        response.setHeader("Access-Control-Allow-Origin", origin);
        response.setHeader("Access-Control-Allow-Methods", "GET, PUT, POST, OPTIONS");
        response.setHeader("Access-Control-Expose-Headers", "content-type");
        response.setHeader("Access-Control-Allow-Headers", "origin, content-type, authorization, x-http-method-override");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        // ensure there is never any caching for now
//        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate"); // HTTP 1.1.
//        response.setHeader("Pragma", "no-cache"); // HTTP 1.0.
//        response.setHeader("Expires", "0"); // Proxies.

//        response.setContentType("text/plain");
        response.getOutputStream().close();
    }

}
