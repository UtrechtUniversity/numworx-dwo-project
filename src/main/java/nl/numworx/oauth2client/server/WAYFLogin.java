package nl.numworx.oauth2client.server;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class WAYFLogin implements Login {
	
	Login NULL = new Login() {

		@Override
		public void login(HttpServletRequest req, HttpServletResponse resp, String state, String codeChallenge,
				Boolean asr) throws Exception {
			resp.sendError(HttpServletResponse.SC_NOT_FOUND);
		}
		
	};

	Map<String, Login> loginMap = new HashMap<>();
	
	public WAYFLogin(ServletConfig servletConfig) {
		Login entree = new EntreeSLogin(servletConfig);
		loginMap.put("entree", entree);
// default is entree
		loginMap.put("", entree);
		loginMap.put(null, entree);
// conext voor HO
		Login conext = new ConextLogin(servletConfig);
		loginMap.put("conext", conext);
// solis voor UU
		Login solis = new UULogin(servletConfig);
		loginMap.put("solis", solis);
// oops recursie
		Login dwo = new DwoLogin(servletConfig);
		loginMap.put("dwo", dwo);
		
	}

	@Override
	public void login(HttpServletRequest req, HttpServletResponse resp, String state, String codeChallenge, Boolean asr)
			throws Exception {
		String idphint = req.getParameter("idphint"); // semi official
		if (idphint == null) 
			idphint = Objects.toString(req.getParameter("with"), ""); // local, not null		

		loginMap.getOrDefault(idphint, NULL).login(req, resp, state, codeChallenge, asr);
	}

}
