package nl.numworx.oauth2client.server;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.oltu.oauth2.common.exception.OAuthSystemException;

import fi.dwo.commons.persistence.Dwo2ExceptionJavaTranslator;
import nl.uu.fi.dwo.rest.util.Dwo2ExceptionTranslator;

public class LoginServlet extends HttpServlet {

	private Login config;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String responseType = req.getParameter("response_type");
		String redirectUri  = req.getParameter("redirect_uri");
		String codeChallenge = req.getParameter("code_challenge");
		String state = req.getParameter("state");
		String client_id = req.getParameter("client_id");
		String path = req.getServletPath();
		
		
		if ("code".equals(responseType) && client_id != null && !client_id.isEmpty()) {
						
			Boolean asr = null;
			if (path.endsWith("mfalogin"))
				asr = Boolean.TRUE;
			try {
				state = redirectUri + ";" + state;
				config.login(req, resp, state, codeChallenge, asr);
				return;
			} catch (Exception e) {
				log("doGet", e);
			}
		}
		
		resp.sendError(HttpServletResponse.SC_FORBIDDEN);	
	}

	static {
        Dwo2ExceptionTranslator.setTranslator(new Dwo2ExceptionJavaTranslator());
	}

	@Override
	public void init() throws ServletException {
		String type = getInitParameter("config");
		if (type != null)
		try {
			Class<Login> clz = (Class<Login>) Class.forName(type);
			Constructor<Login> cl = clz.getConstructor(ServletConfig.class);
			config = cl.newInstance(getServletConfig());
			return;
		} catch (Exception e) {
			throw new ServletException(e);
		} 
//		config = new UULogin(getServletConfig());
//		config = new JavaSamlLogin(getServletConfig());
		config = new DwoLogin(getServletConfig());
	}

}
