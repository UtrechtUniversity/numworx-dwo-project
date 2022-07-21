package nl.numworx.oauth2client.server;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface Login {
	String CHALLENGE = "dwoSAMLchallenge";
	String schoolid = System.getProperty("ENV_ORGID", "385");

	void login(HttpServletRequest req, HttpServletResponse resp, String state, String codeChallenge, Boolean asr) throws Exception;
	
}
