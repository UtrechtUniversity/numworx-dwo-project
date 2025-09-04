package nl.numworx.oauth2client.server;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.onelogin.saml2.Auth;
import com.onelogin.saml2.exception.Error;
import com.onelogin.saml2.exception.SettingsException;

public class JavaSamlLogin implements Login {

	private static final List<String> EMPTY = Collections.singletonList("");
	private String studentNumber;
	private String uid;
	private String givenName;
	private String insertion;
	private String sn;
	private String email;
	private String affiliation;
	private String nonce;
	private String state;

	public JavaSamlLogin(ServletConfig servletConfig) {
	}

	@Override
	public void login(HttpServletRequest request, HttpServletResponse response, String state, String codeChallenge, Boolean asr) throws SettingsException, Error, IOException {
		Auth auth = new Auth(request, response);
		String relayState = codeChallenge + ";" + state;
		auth.login(relayState);
	}

	public boolean authenticate(HttpServletRequest req, HttpServletResponse resp) throws Exception {
		Auth auth = new Auth(req, resp);
		auth.processResponse();
		List<String> errors = auth.getErrors();
		if (auth.isAuthenticated() && errors.isEmpty()) {
			Map<String, List<String>> attributes = auth.getAttributes();
			String nameId = auth.getNameId();
			String nameIdFormat = auth.getNameIdFormat();
			String sessionIndex = auth.getSessionIndex();
			String nameidNameQualifier = auth.getNameIdNameQualifier();
			String nameidSPNameQualifier = auth.getNameIdSPNameQualifier();
			HttpSession session = req.getSession();
			session.setAttribute("attributes", attributes);
			session.setAttribute("nameId", nameId);
			session.setAttribute("nameIdFormat", nameIdFormat);
			session.setAttribute("sessionIndex", sessionIndex);
			session.setAttribute("nameidNameQualifier", nameidNameQualifier);
			session.setAttribute("nameidSPNameQualifier", nameidSPNameQualifier);
			state = req.getParameter("RelayState");
			int index = state.indexOf(';');
			String codeChallenge = state.substring(0,index);
			state = state.substring(index+1);
			uid = attributes.getOrDefault("uid", EMPTY).get(0);
			studentNumber = "";
			givenName = attributes.getOrDefault("gn", EMPTY).get(0);
			insertion = "";
			sn = attributes.getOrDefault("sn", EMPTY).get(0);
			email = attributes.getOrDefault("email", EMPTY).get(0);
			affiliation = attributes.getOrDefault("eduPersonAffiliation", EMPTY).get(0);
			nonce = codeChallenge;
			return true;
		}
		return false;
	}


	public String getStudentNumber() {
		return studentNumber;
	}

	public void setStudentNumber(String studentNumber) {
		this.studentNumber = studentNumber;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getGivenName() {
		return givenName;
	}

	public void setGivenName(String givenName) {
		this.givenName = givenName;
	}

	public String getInsertion() {
		return insertion;
	}

	public void setInsertion(String insertion) {
		this.insertion = insertion;
	}

	public String getSn() {
		return sn;
	}

	public void setSn(String sn) {
		this.sn = sn;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getAffiliation() {
		return affiliation;
	}

	public void setAffiliation(String affiliation) {
		this.affiliation = affiliation;
	}

	public String getNonce() {
		return nonce;
	}

	public void setNonce(String nonce) {
		this.nonce = nonce;
	}

	public String getState() {
		return state;
	}
}