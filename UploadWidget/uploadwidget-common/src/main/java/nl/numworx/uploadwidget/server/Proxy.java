package nl.numworx.uploadwidget.server;

import java.io.Closeable;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.client.params.CookiePolicy;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.SystemDefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicHttpRequest;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;

@SuppressWarnings("serial")
public class Proxy extends HttpServlet {

	private HttpClient proxyClient;
	static Logger LOG = Logger.getLogger(Proxy.class.getName());
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		String url;
		String pathinfo = req.getPathInfo();
		if (pathinfo == null) {
			resp.sendError(HttpServletResponse.SC_NOT_FOUND);
			return;
		}
		pathinfo = pathinfo.split("/")[1];
		url = decode(pathinfo);
		
		log("doGet " + url);
		writeFromClient(resp, url, proxyClient);		
	}

	protected static void writeFromClient(HttpServletResponse resp, String url, HttpClient proxyClient) throws IOException, ClientProtocolException {
		HttpUriRequest request = new HttpGet(url);		
		HttpResponse response = proxyClient.execute(request);
		
		int code = response.getStatusLine().getStatusCode();
		if (code != 200) {
			LOG.info("Error doGet " + response.getStatusLine() );
			for(org.apache.http.Header h: response.getAllHeaders()) {
				LOG.info("response " + h.getName() + " : " + h.getValue());
			}
			resp.sendError(code);
			return; // jammer dan...
		}
		HttpEntity entity = response.getEntity();
		String mimetype = entity.getContentType().getValue();
		resp.setContentType(mimetype);
//		if (entity.getContentEncoding() != null) 
//			resp.setContentEncoding(entity.getContentEncoding().getValue()); // gzip en zo..
		long contentLength = entity.getContentLength();
		try {
			resp.setContentLengthLong(contentLength);
		} catch (NoSuchMethodError e) {
			resp.setContentLength((int) contentLength);
		}
		entity.writeTo(resp.getOutputStream());
	}

	private String decode(String pathinfo) {
		return new String(Base64.getUrlDecoder().decode(pathinfo), StandardCharsets.UTF_8);
	}

	@Override
	public void destroy() {
		if (proxyClient instanceof Closeable)
		try {
			((Closeable)proxyClient).close();
		} catch (IOException e) {
			log("destroy", e);
		}
	}

	@SuppressWarnings("deprecation")
	@Override
	public void init() throws ServletException {
	    proxyClient = createHttpClient();
	}

	@SuppressWarnings({"deprecation"})
	protected static SystemDefaultHttpClient createHttpClient() {
	    HttpParams hcParams = new BasicHttpParams();
	    hcParams.setParameter(ClientPNames.COOKIE_POLICY, CookiePolicy.IGNORE_COOKIES);
	    hcParams.setBooleanParameter(ClientPNames.HANDLE_REDIRECTS, true);	    	    
		return new SystemDefaultHttpClient(hcParams);
	}

	public static String encode(String url, String base) {
		String proxy = Base64.getUrlEncoder().withoutPadding().encodeToString(url.getBytes(StandardCharsets.UTF_8));
		String suffix = ""; // from url between last / and ?
		int q = url.indexOf('?');
		if (q == -1) q = url.length();
		int s = url.lastIndexOf('/', q);
		suffix = url.substring(s+1 , q);
		return base + "/proxy/" + proxy + "/" + suffix;
	}

	@SuppressWarnings("deprecation")
	public static void write(String url, HttpServletResponse resp) throws IOException {
		try (SystemDefaultHttpClient client = createHttpClient()) {
			try {
				writeFromClient(resp, url, client);
			} catch (IOException e) {
				LOG.log(Level.SEVERE, "cannot write " + url, e);
				throw e;
			}	
		}
	}


}
