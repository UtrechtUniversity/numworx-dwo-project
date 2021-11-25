package nl.numworx.uploadwidgetgwt.server;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;

import gwtupload.server.UploadAction;
import gwtupload.server.exceptions.UploadActionException;
import nl.numworx.uploadwidget.shared.AtomEntry;

@SuppressWarnings("serial")
public class Upload extends UploadAction {
	
	Store store;

	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		store = Store.instance();
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		// TODO Auto-generated method stub
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
		for(FileItem item: sessionFiles) {
			AtomEntry entry = new AtomEntry();
			entry.title = item.getName();
			entry.type  = item.getContentType();
			entry.length = item.getSize();
			StringBuffer requestURL = request.getRequestURL();
			requestURL.setLength(requestURL.lastIndexOf("/")+1);
			String uuid = UUID.randomUUID().toString();
			// entry.id = uuid;
			requestURL.append("download/")
				.append(uuid).append("/")
				.append(entry.title);
			entry.url = requestURL.toString();
			store.addEntry(entry);
			item.delete();
		}
		return super.executeAction(request, sessionFiles);
	}

}
