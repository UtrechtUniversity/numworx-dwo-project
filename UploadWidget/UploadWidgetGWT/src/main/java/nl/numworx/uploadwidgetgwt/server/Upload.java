package nl.numworx.uploadwidgetgwt.server;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;

import fi.dwo.commons.persistence.Dwo2ExceptionJavaTranslator;
import gwtupload.server.UploadAction;
import gwtupload.server.exceptions.UploadActionException;
import nl.numworx.uploadwidget.server.Store;
import nl.numworx.uploadwidget.shared.AtomEntry;
import nl.numworx.uploadwidgetgwt.shared.Constants;
import nl.uu.fi.dwo.lms.jclient.lib.rest.transport.RestAuthenticator;
import nl.uu.fi.dwo.lms.jclient.lib.rest.transport.StoredRestManager;
import nl.uu.fi.dwo.rest.dom.entities.DomSchool;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolRoleAndClassV2;
import nl.uu.fi.dwo.rest.util.Dwo2ExceptionTranslator;

@SuppressWarnings("serial")
public class Upload extends UploadAction {

	static {
        Dwo2ExceptionTranslator.setTranslator(new Dwo2ExceptionJavaTranslator());
	}
	
	
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
		Optional<DomSchoolRoleAndClassV2> actor = Optional.empty();
		rest.setBearerAuthString(authorization);
		for(FileItem item: sessionFiles) {
			if (item.isFormField()) {
				String key = item.getFieldName();
				if ("learnerId-0".equals(key)) {
					learnerId = item.getString();
					actor = JavaUpload.getActor(authorization, learnerId);
				} else if ("registration-0".equals(key)) {
					registration = item.getString();
				} else if ("uuid-0".equals(key)) {
					uuid = item.getString().replace('-', '/');
				}
				continue;
			}
			DomSchool school = actor.get().getSchool();
			AtomEntry entry = new AtomEntry();
			entry.title = item.getName();
			entry.type  = item.getContentType();
			entry.length = item.getSize();
			StringBuffer requestURL = new StringBuffer();		
			requestURL.append(school.getId()).append("/")
				.append(uuid).append("/")
				.append(registration).append("/")
				.append(entry.title);
			entry.url = requestURL.toString();
			Map<String, String> map = Collections.singletonMap("learnerid", learnerId);
			store.addEntry(entry, map, item);
			item.delete();
		}
		return super.executeAction(request, sessionFiles);
	}

}
