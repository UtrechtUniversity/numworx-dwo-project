package fi.dwo.server.db;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@SuppressWarnings("serial")
public class SEBHosting extends HttpServlet {

	
	String replacement = "https://app.dwo.nl";
	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String path = req.getPathTranslated();
		if (path == null) {
			path = getServletContext().getRealPath(req.getServletPath());
		}
		File f = new File(path).getParentFile();
		f = new File(f, "leerling.seb");
		log("reading " + f + " for " + path);
		Reader in;
		try {
			in = new InputStreamReader(new FileInputStream(f), StandardCharsets.UTF_8);
		} catch (FileNotFoundException e) {
			resp.sendError(HttpServletResponse.SC_NOT_FOUND);
			return;
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
			resp.getWriter().write(sb.toString().replace("https://app.dwo.nl", replacement));
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
	}

}
