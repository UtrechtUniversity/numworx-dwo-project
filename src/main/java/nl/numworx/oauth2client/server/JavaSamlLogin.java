package nl.numworx.oauth2client.server;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.onelogin.saml2.Auth;
import com.onelogin.saml2.exception.Error;
import com.onelogin.saml2.exception.SettingsException;

public class JavaSamlLogin implements Login {

	@Override
	public void login(HttpServletRequest request, HttpServletResponse response, String state, String codeChallenge, Boolean asr) throws SettingsException, Error, IOException {
		Auth auth = new Auth(request, response);
		String relayState = state;
		auth.login(relayState);
	}

}
