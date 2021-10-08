package fi.servlet.lti;

import java.net.URLEncoder;

import javax.servlet.http.HttpServletRequest;

/** DTO voor LTI 1.3 providers
 * 
 *      "http://localhost:9001": {
        "client_id": "d42df408-70f5-4b60-8274-6c98d3b9468d",
        "auth_login_url": "http://localhost:9001/platform/login.php",
        "auth_token_url": "http://localhost/platform/token.php",
        "key_set_url": "http://localhost/platform/jwks.php",
        "private_key_file": "/private.key",
        "kid": "58f36e10-c1c1-4df0-af8b-85c857d1634f",
        "deployment": [
            "8c49a5fa-f955-405e-865f-3d7e959e809f"
        ]
    }
 */
public class ProviderInfo {
	public String
		client_id = "d42df408-70f5-4b60-8274-6c98d3b9468d",
        auth_login_url = "http://localhost:9001/platform/login.php",
        auth_token_url = "http://localhost/platform/token.php",
        key_set_url = "http://localhost/platform/jwks.php",
        kid = "58f36e10-c1c1-4df0-af8b-85c857d1634f",
    
		deployment[] = new String[] {
                       "8c49a5fa-f955-405e-865f-3d7e959e809f"
		};	
	public byte[] private_key; // not yet.
	
	public static ProviderInfo get(String iss) {
		if (iss == null || iss.isEmpty()) return null; // not found.
		return new ProviderInfo();
	}
	
	public static ProviderInfo get(HttpServletRequest request) {
// require login_hint and iss.
		String login_hint = request.getParameter("login_hint");
		if (login_hint == null|| login_hint.isEmpty()) return null;
		return get(request.getParameter("iss"));
	}

	/** create redirect url 
	 *      'scope'         => 'openid', // OIDC Scope.
            'response_type' => 'id_token', // OIDC response is always an id token.
            'response_mode' => 'form_post', // OIDC response is always a form post.
            'prompt'        => 'none', // Don't prompt user on redirect.
            'client_id'     => $registration->get_client_id(), // Registered client id.
            'redirect_uri'  => $launch_url, // URL to return to after login.
            'state'         => $state, // State to identify browser session.
            'nonce'         => $nonce, // Prevent replay attacks.
            'login_hint'    => $request['login_hint'] // Login hint to identify platform session.
      optional
            $auth_params['lti_message_hint'] = $request['lti_message_hint'];

	 * @param launch_url
	 * @param request
	 * @return
	 */
	public String redirect_url(String launch_url, HttpServletRequest request) {
		String state = "state101";
		String nonce = "nonce101";
		launch_url = URLEncoder.encode(launch_url);
		String login_hint = request.getParameter("login_hint");
		login_hint = URLEncoder.encode(login_hint);
		String lti_message_hint = request.getParameter("lti_message_hint");
		if (lti_message_hint != null) 
			lti_message_hint = "&lti_message_hint=" + URLEncoder.encode(lti_message_hint);
		
		return auth_login_url + "?scope=openid&response_type=id_token&response_mode=form_post&prompt=none" +
						"&client_id=" + client_id +
						"&redirect_uri=" + launch_url +
						"&state=" + state +
						"&nonce=" + nonce +
						"&login_hint=" + login_hint +
						lti_message_hint;
	}
	
}
