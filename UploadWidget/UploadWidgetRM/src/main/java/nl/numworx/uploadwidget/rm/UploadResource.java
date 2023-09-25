package nl.numworx.uploadwidget.rm;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import org.cbook.cbookif.rm.Resource;
import org.cbook.cbookif.rm.ResourceContainer;
import org.cbook.cbookif.rm.ResourceException;

public class UploadResource implements Resource {

	private UploadInstanceContainer parent;
	private String name;
	private String type;
	private Long length;

	public UploadResource(UploadInstanceContainer parent, String name, String type, Long length) {
		this.parent = parent;
		this.name = name;
		this.type = type;
		this.length = length;
	}

	@Override
	public Long getContentLength() {
		return length;
	}

	@Override
	public String getMimeType() {
		return type;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public ResourceContainer getParent() {
		return parent;
	}

	@Override
	public InputStream getStream() throws ResourceException {
		return null;
	}

	@Override
	public URL getURL() throws ResourceException {
		try {
			 // met spaces en andere encoding
			return parent.getServerUrlPath().toURI().resolve(new URI(null, null, name, null)).toURL();
		} catch (MalformedURLException | URISyntaxException e) {
			throw new ResourceException(e);
		} // TODO make login

	}

	@Override
	public boolean isContainer() {
		return false;
	}

	@Override
	public void remove() throws ResourceException {
	}

	@Override
	public void setName(String arg0) throws ResourceException {
		this.name = arg0;
	}

}
