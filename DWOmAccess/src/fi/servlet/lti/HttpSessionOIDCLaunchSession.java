package fi.servlet.lti;

import edu.uoc.lti.oidc.OIDCLaunchSession;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Arrays;
import java.util.List;

/**
 * @author xaracil@uoc.edu
 */
public class HttpSessionOIDCLaunchSession implements OIDCLaunchSession {
	private final static String STATE_SESSION_ATTRIBUTE_NAME = "currentLti1.3State";
	private final static String NONCE_SESSION_ATTRIBUTE_NAME = "currentLti1.3Nonce";
	private final static String TARGETLINK_URI_SESSION_ATTRIBUTE_NAME = "currentLti1.3TargetLinkUri";
	private final static String ISSUER = "currentLti1.3Issuer";
	
	private final HttpServletRequest request;

	public HttpSessionOIDCLaunchSession(HttpServletRequest request) {
		this.request = request;		
	}

	public final static List<String> KEYS = Arrays.asList(STATE_SESSION_ATTRIBUTE_NAME, NONCE_SESSION_ATTRIBUTE_NAME, TARGETLINK_URI_SESSION_ATTRIBUTE_NAME);

	public void clear() {
		final HttpSession session = this.request.getSession(false);
		if (session != null) {
			setState(null);
			setTargetLinkUri(null);
			setNonce(null);
			setIssuer(null);
		}
	}

	@Override
	public void setState(String s) {
		setAttribute(STATE_SESSION_ATTRIBUTE_NAME, s);
	}

	@Override
	public void setNonce(String s) {
		setAttribute(NONCE_SESSION_ATTRIBUTE_NAME, s);
	}

	@Override
	public void setTargetLinkUri(String s) {
		setAttribute(TARGETLINK_URI_SESSION_ATTRIBUTE_NAME, s);
	}

	private void setAttribute(String name, String value) {
		if (value == null) {
			request.getSession().removeAttribute(name);
		} else {
			request.getSession().setAttribute(name, value);
		}
	}

	@Override
	public String getState() {
		return getAttribute(STATE_SESSION_ATTRIBUTE_NAME);
	}

	@Override
	public String getNonce() {
		return getAttribute(NONCE_SESSION_ATTRIBUTE_NAME);
	}

	@Override
	public String getTargetLinkUri() {
		return getAttribute(TARGETLINK_URI_SESSION_ATTRIBUTE_NAME);
	}

	private String getAttribute(String name) {
		Object state = request.getSession().getAttribute(name);
		return state != null ? state.toString() : null;
	}
	
	public String getIssuer() {
		return getAttribute(ISSUER);
	}
	
	public void setIssuer(String iss) {
		setAttribute(ISSUER, iss);
	}

	private String clientId, deploymentId;
	@Override
	public String getClientId() {
		// TODO Auto-generated method stub
		return clientId;
	}

	@Override
	public String getDeploymentId() {
		// TODO Auto-generated method stub
		return deploymentId;
	}

	@Override
	public void setClientId(String arg0) {
		// TODO Auto-generated method stub
		clientId = arg0;
	}

	@Override
	public void setDeploymentId(String arg0) {
		// TODO Auto-generated method stub
		deploymentId = arg0;
	}
}
