package fi.dwo.server.db;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import fi.dwo.commons.persistence.entities.PersistentClassCourse;
import fi.dwo.server.PersistentDataManagers.core.ClassCourseManager;

@SuppressWarnings("serial")
public class SEBHosting extends HttpServlet {

	
	private static final String HTTPS_APP_DWO_NL = "https://app.dwo.nl";
	String replacement = HTTPS_APP_DWO_NL;
	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String code = req.getParameter("a"); // deze komt door
		if (code != null) {
			log("oauth code in seb " + code);
			code = "&amp;a=" + URLEncoder.encode(code); // in XML embedded
		} else code = "";
		
		String path = req.getPathTranslated();
		if (path == null) {
			path = getServletContext().getRealPath(req.getServletPath());
		}
		File file = new File(path);
		File f = file.getParentFile();
		f = new File(f, "leerling.seb");
		log("reading " + f + " for " + path);
		Reader in;
		try {
			in = new InputStreamReader(new FileInputStream(f), StandardCharsets.UTF_8);
		} catch (FileNotFoundException e) {
			resp.sendError(HttpServletResponse.SC_NOT_FOUND);
			return;
		}
		Long classcourse = null;
		String base = file.getName();
		if (base.endsWith(".seb")) {
			try { 
				classcourse = Long.valueOf(base.substring(0, base.length()-4));
				PersistentClassCourse cc = ClassCourseManager.findEntity(classcourse);
				if (cc == null) 
					classcourse = null;
			} catch(Exception e) {}
		}
		BufferedReader reader = new BufferedReader(in);
		try {
			char[] buffer = new char[10240];
			StringBuilder sb = new StringBuilder();
			int len;
			while ( (len = reader.read(buffer)) > 0) {
				sb.append(buffer, 0, len);
			}
			resp.setContentType("application/seb");
			resp.setCharacterEncoding("UTF-8");
			String content = sb.toString().replace(HTTPS_APP_DWO_NL, replacement);
			if (classcourse != null)
				content = content.replace("toets.jsp", "toets.jsp?id="+classcourse + code);
			resp.getWriter().write(content);
		} finally {
			reader.close();
		}
	}

	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServlet#getLastModified(javax.servlet.http.HttpServletRequest)
	 */
	@Override
	protected long getLastModified(HttpServletRequest req) {
		String path = req.getPathTranslated();
		if(path == null) return super.getLastModified(req);
		File file = new File(path);
		return file.lastModified();
	}

	/* (non-Javadoc)
	 * @see javax.servlet.GenericServlet#init()
	 */
	@Override
	public void init() throws ServletException {
		replacement = System.getProperty("ALLOW_ORIGIN", replacement);
		replacement = replacement.split("\\s+")[0];
		if ("*".equals(replacement)) replacement = HTTPS_APP_DWO_NL;
	}

}
