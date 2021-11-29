package nl.numworx.uploadwidgetgwt.server;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;

import gwtupload.server.UploadAction;
import gwtupload.server.exceptions.UploadActionException;
import nl.numworx.uploadwidget.shared.AtomEntry;
import nl.numworx.uploadwidgetgwt.shared.Constants;
import nl.uu.fi.dwo.lms.jclient.lib.rest.transport.RestAuthenticator;
import nl.uu.fi.dwo.lms.jclient.lib.rest.transport.StoredRestManager;

@SuppressWarnings("serial")
public class Upload extends UploadAction {
	
	Store store;
	StoredRestManager rest = StoredRestManager.getInstance();

	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		store = Store.instance();
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		String authorization = request.getHeader(Constants.AUTHORIZATION);
		if (authorization != null)
			request.getSession().setAttribute(Constants.AUTHORIZATION, authorization);
		super.doGet(request, response);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		// TODO Auto-generated method stub
		super.doPost(request, response);
	}

	@Override
	public String executeAction(HttpServletRequest request, List<FileItem> sessionFiles) throws UploadActionException {
		StoredRestManager rest = StoredRestManager.getInstance();
		String learnerId = null;
		String registration = null;
		String uuid = null;
		String authorization = (String) request.getSession().getAttribute(Constants.AUTHORIZATION);
		rest.setBearerAuthString(authorization);
		for(FileItem item: sessionFiles) {
			if (item.isFormField()) {
				String key = item.getFieldName();
				if ("learnerId-0".equals(key)) {
					learnerId = item.getString();
				} else if ("registration-0".equals(key)) {
					registration = item.getString();
				} else if ("uuid-0".equals(key)) {
					uuid = item.getString();
				}
				continue;
			}
			AtomEntry entry = new AtomEntry();
			entry.title = item.getName();
			entry.type  = item.getContentType();
			entry.length = item.getSize();
			StringBuffer requestURL = request.getRequestURL();
			requestURL.setLength(requestURL.lastIndexOf("/")+1);
			String guid = UUID.randomUUID().toString();
			entry.id = guid;
			requestURL.append("download/")
				.append(uuid).append("/")
				.append(registration).append("/")
				.append(guid).append("/")
				.append(entry.title);
			entry.url = requestURL.toString();
			Map<String, String> map = Collections.singletonMap("learnerId", learnerId);
			store.addEntry(entry, map);
			item.delete();
		}
		return super.executeAction(request, sessionFiles);
	}

}
