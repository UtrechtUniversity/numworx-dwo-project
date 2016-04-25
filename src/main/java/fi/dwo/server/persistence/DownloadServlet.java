package fi.dwo.server.persistence;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.logging.Logger;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class DownloadServlet extends HttpServlet {

    private static final Logger LOG = Logger.getLogger(DownloadServlet.class.getName());

    private static final String VERSION_ID = "version-id";
    private static final String PACK200_GZIP_ENCODING = "pack200-gzip";
    // HTTP Compression RFC 2616 : Standard headers
    private static final String ACCEPT_ENCODING = "Accept-Encoding";
    // HTTP Compression RFC 2616 : Standard headers
    public static final String CONTENT_ENCODING = "content-encoding";

    String pfx = "";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doHeadGet(req, resp, true);
    }

    private void doHeadGet(HttpServletRequest req, HttpServletResponse resp, boolean b) throws IOException {
        ServletContext context = getServletContext();
        String pathInfo = req.getPathInfo();
        if (pathInfo == null) {
            pathInfo = "";
        }
        String path = req.getServletPath() + pathInfo; // full path in war
        String mime = context.getMimeType(path);
        if (mime == null) {
            mime = "application/octet-stream";
        }
        String fileName = context.getRealPath(path);
        URL url = context.getResource(path); // debugging
        log(path + " as file " + fileName);
        log(path + " as url " + url); // prefer url in osgi context

        if (url == null) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        // file strategie
        if ("file".equals(url.getProtocol())) {
            fileName = url.getPath();
        }
        resp.setContentType(mime);

        File file = new File(fileName);
        long mod = file.lastModified();
        resp.setDateHeader("Last-Modified", mod);
        // Enumeration x = req.getHeaderNames(); while (x.hasMoreElements()) {
        // Object object = x.nextElement();
        // log(object + ":" + req.getHeader(object.toString()));
        // }
        String encoding = req.getHeader(ACCEPT_ENCODING);
        if (encoding != null && encoding.contains(PACK200_GZIP_ENCODING)) {
            URL u = context.getResource(path + ".pack.gz");
            if (u != null) {
                resp.setHeader(CONTENT_ENCODING, PACK200_GZIP_ENCODING);
                url = u;
                log(path + " as packed url " + url); // prefer url in osgi
                // context does not work!
            }
        }
        int size;
        URLConnection uc = url.openConnection();
        uc.setDoInput(b);
        size = uc.getContentLength();
        if (size > 0) {
            resp.setContentLength(size); // ?? 2Gb limit
        }
        if (b) {
            copy(uc.getInputStream(), resp.getOutputStream());
        }
    }

    private void copy(InputStream in, ServletOutputStream out) throws IOException {
        byte[] buf = new byte[128 * 1024];
        int len;
        do {
            len = in.read(buf);
            if (len > 0) {
                out.write(buf, 0, len);
            }
        } while (len > 0);
        in.close();
        out.flush();
    }

    @Override
    protected void doHead(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doHeadGet(req, resp, false);
    }

    @Override
    public void init() throws ServletException {
    }

    @Override
    protected long getLastModified(HttpServletRequest req) {
        String path = req.getPathInfo();
        if (path == null) {
            path = "";
        }
        path = req.getServletPath() + path;
        path = getServletContext().getRealPath(path);
        if (path == null) {
            return super.getLastModified(req);
        } else {
            File file = new File(path);
            if (file.canRead()) {
                return file.lastModified();
            } else {
                return super.getLastModified(req);
            }
        }
    }

}
