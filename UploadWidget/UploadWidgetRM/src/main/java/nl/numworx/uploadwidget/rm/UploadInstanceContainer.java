package nl.numworx.uploadwidget.rm;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.cbook.cbookif.CBookContext;
import org.cbook.cbookif.rm.Resource;
import org.cbook.cbookif.rm.ResourceContainer;
import org.cbook.cbookif.rm.ResourceException;

public class UploadInstanceContainer implements ResourceContainer {
	private static final Logger LOG = Logger.getLogger(UploadInstanceContainer.class.getName());
	private String oauth_token;
	private URL serverUrlPath;

	private Long put(String path, InputStream in, String type) throws IOException {
	      URL url = new URL(getServerUrlPath(), path); // TODO make login
	      HttpURLConnection conn = (HttpURLConnection) url.openConnection();
	      OutputStream outStream = null;
	      conn.setRequestMethod("PUT");
	      conn.setRequestProperty("Content-Type", type);
	      conn.setRequestProperty("Authorization", getBasicAuthString());
	      conn.setDoOutput(true);
	      conn.setUseCaches(false);
	      outStream = conn.getOutputStream();

	      byte[] buffer = new byte[10240]; // 10kb
	      int s;
	      long size = 0;
	      while ( (s = in.read(buffer))>0) {
	    	  size += s;
	    	  outStream.write(buffer, 0, s);
	      }
	      
	      outStream.close();
	      int responseCode = conn.getResponseCode();
	      LOG.info("responsecode " + responseCode + ", size " + size);
	      return size;
	}
	
	private String getBasicAuthString() {
		return oauth_token;
	}

	private URL getServerUrlPath() {
		return serverUrlPath;
	}

	public UploadInstanceContainer(CBookContext context) {
		oauth_token = (String) context.getProperty("oauth_token");
		serverUrlPath = (URL) context.getProperty("serverUrlPath");
		String pfx = "";
		try {
			pfx = "dav/upload/instance/" + context.getProperty("UUID") + "/";
			serverUrlPath = new URL(serverUrlPath, pfx);
		} catch (MalformedURLException e) {
			LOG.log(Level.SEVERE, "invalid prefix " + pfx, e);
			serverUrlPath = null;
		}
	}

	@Override
	public Long getContentLength() {
		return null;
	}

	@Override
	public String getMimeType() {
		return null;
	}

	@Override
	public String getName() {
		return ".";
	}

	@Override
	public ResourceContainer getParent() {
		return null;
	}

	@Override
	public InputStream getStream() throws ResourceException {
		return null;
	}

	@Override
	public URL getURL() throws ResourceException {
		return null;
	}

	@Override
	public boolean isContainer() {
		return true;
	}

	@Override
	public void remove() throws ResourceException {

	}

	@Override
	public void setName(String arg0) throws ResourceException {
	}

	@Override
	public Resource create(String name, URL url) throws ResourceException {
		try {
			URLConnection uc = url.openConnection();
			InputStream in = uc.getInputStream();
			String mimetype = uc.getContentType();
			if (mimetype == null) mimetype = "application/octet-stream";
			return create(name, in, mimetype);
		} catch (ResourceException e) {
			throw e;
		} catch (IOException e) {
			throw new ResourceException("create " + url, e);
		}
	}

	@Override
	public Resource create(String arg0, Resource arg1) throws ResourceException {
		return null;
	}
	
	@Override
	public Resource create(String name, InputStream in, String type) throws ResourceException {
		Long length = null;
		try {
			length = put(name, in, type);
			in.close();
		} catch (Exception e) {
			throw new ResourceException("create " + name, e);
		}
		return new UploadResource(this, name, type, length);
	}

	@Override
	public ResourceContainer createContainer(String arg0) throws ResourceException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Resource[] list() throws ResourceException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Resource open(String arg0) throws ResourceException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ResourceContainer openContainer(String arg0) throws ResourceException {
		// TODO Auto-generated method stub
		return null;
	}

}
