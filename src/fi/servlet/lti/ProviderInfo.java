package fi.servlet.lti;

import java.net.URISyntaxException;
import java.net.URLEncoder;

import javax.servlet.http.HttpServletRequest;

import edu.uoc.elc.lti.tool.Tool;
import edu.uoc.elc.lti.tool.ToolBuilders;
import edu.uoc.elc.lti.tool.ToolDefinition;
import edu.uoc.elc.lti.tool.oidc.InMemoryOIDCLaunchSession;
import edu.uoc.elc.lti.tool.oidc.LoginRequest;
import edu.uoc.elc.lti.tool.oidc.LoginRequest.LoginRequestBuilder;
import edu.uoc.lti.accesstoken.JSONAccessTokenRequestBuilderImpl;
import edu.uoc.lti.claims.ClaimAccessor;
import edu.uoc.lti.jwt.claims.JWSClaimAccessor;
import edu.uoc.lti.jwt.client.JWSClientCredentialsTokenBuilder;
import edu.uoc.lti.jwt.deeplink.JWSTokenBuilder;
import edu.uoc.lti.oidc.OIDCLaunchSession;

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
    public Tool tool; // = new Tool(toolDefinition, claimAccessor, oidcLaunchSession, toolBuilders)
  
	public String
		client_id = "d42df408-70f5-4b60-8274-6c98d3b9468d",
        auth_login_url = "http://localhost:9001/platform/login.php",
        auth_token_url = "http://localhost:9001/platform/token.php",
        key_set_url = "http://localhost:9001/platform/jwks.php",
        kid = "58f36e10-c1c1-4df0-af8b-85c857d1634f",   
		deployment = "8c49a5fa-f955-405e-865f-3d7e959e809f";	

    String publicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA0MkrXiaPUxRzGOwrmSQKlDXUFn9veJlUybecFN07QIlqU758DxsSAvv8ZGPnzQVBKy9ykoXoaxecpKEIe/kK5qPbAVvnK6lGFbUl1QkK/NnHwf2zDy4S1f/OLh0oyKcI7izkUUl4lLzim5jsNChxpY00xqi5lh8Sk2qRppbbUR8rojTnl64mZq3P6Rl3GlXKj4GpRCFTdWb4Gyrx6KU6IZ2rufnGSSfRK4jnuASvTBW4PBbipxXN3mjPukx0tsWIYHh3hhv0DZUnOPBShPf0aTeT4c8+rjZ7EhDZJJr/OlLW9d+wonFKIz+fCdjzBxdGUEdoMsU7pW5xsmp8obAHUQIDAQAB";
    String privateKey = "MIIEpQIBAAKCAQEA0MkrXiaPUxRzGOwrmSQKlDXUFn9veJlUybecFN07QIlqU758DxsSAvv8ZGPnzQVBKy9ykoXoaxecpKEIe/kK5qPbAVvnK6lGFbUl1QkK/NnHwf2zDy4S1f/OLh0oyKcI7izkUUl4lLzim5jsNChxpY00xqi5lh8Sk2qRppbbUR8rojTnl64mZq3P6Rl3GlXKj4GpRCFTdWb4Gyrx6KU6IZ2rufnGSSfRK4jnuASvTBW4PBbipxXN3mjPukx0tsWIYHh3hhv0DZUnOPBShPf0aTeT4c8+rjZ7EhDZJJr/OlLW9d+wonFKIz+fCdjzBxdGUEdoMsU7pW5xsmp8obAHUQIDAQABAoIBAQC9MX4t++0mkMJXlDNRu1omwbxlgqcFdpRhkhNKyMqXia4jItqSaaphr+wfIHT90MQkGQPOiK9609OrTw08IgnhxBuB2MDbTLHom9UjfeVKCSK9xGKM3+hLqVkxalT5tnseMOnYSyaMSbli3Ck2fmu1ZAat+ljqE1Am64v+lHc6wsq4tUXvZ6/dIthvcnbuPP0RwdZH05GWqiI8sUz0W2zi7rqFJadaEZbxb/WFhO51MbyrZh34/MpxfqJEIkFnrzt+FgJ4F7mbQrv+XXo1mQ2I0MCknzWspYLwCsVyGV9jSuK+zmD9R/JGByf2rCeO3BAlNBnnE/Fu103DkZIFD5vBAoGBAPtEruX93ggJfK/dY/Bq3WRC7S5dGnRQZ7Z5lErK2ZX448HOhwdOH5e9FXPH5X+QpYkDFMe49BD6eDNCPrdF+0ttMrQfV2HtKiTbRae7rYrsRBkY+MKENixz4ENVNQdueyv0CvBe7Ba7bXHdrPdiSUwEBmkn9wG+btDy+ItHYX65AoGBANS3r63tVIraNT5mhfBHChmmy35A2YaJc2IJGWTOZjNb+CHu/99DwiHWvYhWp4RZ0BKK/7GkBetDhVg21sscL2981oTOIiul8wc5P252QJvjsyumuB5+NcdmzYF7PbvotuKI4o8hu7dHYY4Qp/MGz2eQhYGBSB9GqbRMJShtjkFZAoGBAIaxI7xAIRRX2ZIAcIFBF9qWEcRnvjWZoG7tr3OEV60QFS8gAbwFweO6RVSiVEDUjhfrIemKGLM9QM/hc/MUvYeKSsLJhjMFSjElpaorbfTpf/ugKkFDVDLyDsapV1rbe4VtNavyhkYNRLbkKMMX2ci446Lc/Ijfx1GU3Wzz36xpAoGAQ4mutcJMvWlazl0u2YM0qcBTi9p7NkQd5lqNPXxq5pOkzOFdTD3vPV84/jjFJzh83+ZSGMzDNFdT1xZSTFq+lN9GHRR1tPYTm4+JnEDfcp9xG8LrYoMgABeb2CiRCUByEKr1hAxp1V9MkhanvHnFEFTKjrvFcmi1KRGkGpnuOMECgYEA88kCnSMb1yHfexJQZ+WUgb8m+WeyOgW2a2DzU1yXLFoCEZlbNQYFFWbDeTHfmaur3rox0ZvcoDv1ohXCsULZz9uu72cgRaObgGsjFAo9J0btEJT7s1ljUr55NwLsaPUkWzTIce2BnIE388y74i9DcPRrFkbOlxXPzvP0E1r6SK4=";
	
    ToolDefinition toolDefinition = ToolDefinition.builder()
	      .accessTokenUrl(auth_token_url)
	      .oidcAuthUrl(auth_login_url)
	      .clientId(client_id)
	      .deploymentId(deployment)
	      .keySetUrl(key_set_url)
	      .platform("http://localhost:9001")
	      .name("DWOmAccess")
	      .publicKey(publicKey)
	      .privateKey(privateKey)
	      .build();
	ToolBuilders toolBuilders = new ToolBuilders(
	    new JWSClientCredentialsTokenBuilder(toolDefinition.getPublicKey(), toolDefinition.getPrivateKey()), 
	    new JSONAccessTokenRequestBuilderImpl(),
	    new JWSTokenBuilder(toolDefinition.getPublicKey(), toolDefinition.getPrivateKey()));


    ClaimAccessor claimAccessor = new JWSClaimAccessor(toolDefinition.getKeySetUrl());
	
	private ProviderInfo() {
	  OIDCLaunchSession oidcLaunchSession = new InMemoryOIDCLaunchSession();
      tool = new Tool(toolDefinition, claimAccessor, oidcLaunchSession, toolBuilders);
	}
	
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
	    LoginRequestBuilder builder = LoginRequest.builder();
	    builder.target_link_uri(launch_url);
		String login_hint = request.getParameter("login_hint");
		login_hint = URLEncoder.encode(login_hint);
        builder.login_hint(login_hint);
		String lti_message_hint = request.getParameter("lti_message_hint");
		if (lti_message_hint != null) 
			lti_message_hint = URLEncoder.encode(lti_message_hint);
        builder.lti_message_hint(lti_message_hint);
		try {
      return tool.getOidcAuthUrl(builder.build());
    } catch (URISyntaxException e) {
        throw new RuntimeException(e);
    }
//		return auth_login_url + "?scope=openid&response_type=id_token&response_mode=form_post&prompt=none" +
//						"&client_id=" + client_id +
//						"&redirect_uri=" + launch_url +
//						"&state=" + state +
//						"&nonce=" + nonce +
//						"&login_hint=" + login_hint +
//						lti_message_hint;
	}
	
}
