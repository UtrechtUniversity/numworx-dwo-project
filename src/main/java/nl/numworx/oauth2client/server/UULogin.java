package nl.numworx.oauth2client.server;

import java.io.IOException;
import java.math.BigInteger;
import java.security.Key;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.util.Base64;
import java.util.Base64.Decoder;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.oltu.oauth2.client.OAuthClient;
import org.apache.oltu.oauth2.client.URLConnectionClient;
import org.apache.oltu.oauth2.client.request.OAuthClientRequest;
import org.apache.oltu.oauth2.client.request.OAuthClientRequest.AuthenticationRequestBuilder;
import org.apache.oltu.oauth2.client.response.OAuthJSONAccessTokenResponse;
import org.apache.oltu.oauth2.client.response.OAuthResourceResponse;
import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.apache.oltu.oauth2.common.message.types.GrantType;
import org.apache.oltu.oauth2.common.token.OAuthToken;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwsHeader;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SigningKeyResolver;

public class UULogin implements SigningKeyResolver, Login {

    public static final String ID_TOKEN= "id_token";
    public static final String PASSWORD = "urn:uu.nl:idp:contract:password";
    public static final String PASSWORD_MFA = "urn:uu.nl:idp:contract:rba:numworx";

	private String client_id = "87e22bd9-ac67-4457-b4c3-c039c42cf052";
	private String client_secret = "LPb6jeN6q7CYurh96iZZAVz3cqab7r_3hVxkqGzj0j5_1Bf-rhjsNYnIUrpqw5y8hu1ckesX5-fmtINofw6jrw";
	
	private String redirect_url = "https://numworx.acc.uu.nl/dwo/oauth2/redirect";
	
	final static String numworx_scope = "urn:uu.nl:idp:scope:oauth:numworx";
		
	private String ISSUER = "https://login.acc.uu.nl/nidp/oauth/nam";
	private String AUTHORIZATION_URL = "https://login.acc.uu.nl/nidp/oauth/nam/authz";
	private String TOKEN_URL = 	"https://login.acc.uu.nl/nidp/oauth/nam/token";
	private String KEYS_URL = "https://login.acc.uu.nl/nidp/oauth/nam/keys";

	private OAuthClient oAuthClient = new OAuthClient(new URLConnectionClient());

    public UULogin(ServletConfig servletConfig) {
    	this();
	}
    public UULogin() {
		if (!System.getProperty("UU_SECRET", "").isEmpty())
			productie();
    }

    /*  Client_ID: 084ed6f6-43bb-4ac8-bb41-92ad6bc14354
		Grants: authorization code
		Token Types: code, access token, id token
		Scope: urn:uu.nl:idp:scope:oauth:numworx
		 
		Endpoint type 		URL
		Authorization 		https://login.uu.nl/nidp/oauth/nam/authz
		Registration  		https://login.uu.nl/nidp/oauth/nam/clients
		Token	      		https://login.uu.nl/nidp/oauth/nam/token
		Token Introspect 	https://login.uu.nl/nidp/oauth/v1/nam/introspect
		UserInfo			https://login.uu.nl/nidp/oauth/nam/userinfo
		OpenID Metadata		https://login.uu.nl/nidp/oauth/nam/.well-known/openid-configuration
		Revocation			https://login.uu.nl/nidp/oauth/nam/revoke
		JSON Web Key Set	https://login.uu.nl/nidp/oauth/nam/keys
     */
    private void productie() {
		client_id = "084ed6f6-43bb-4ac8-bb41-92ad6bc14354";
		client_secret = System.getProperty("UU_SECRET", "");
		ISSUER = "https://login.uu.nl/nidp/oauth/nam";
		AUTHORIZATION_URL = "https://login.uu.nl/nidp/oauth/nam/authz";
		TOKEN_URL = "https://login.uu.nl/nidp/oauth/nam/token";
		KEYS_URL = "https://login.uu.nl/nidp/oauth/nam/keys";
		redirect_url = "https://numworx.uu.nl/dwo/oauth2/redirect";
	}
       
	String login(String state, String nonce, Boolean acr, String prompt) throws OAuthSystemException {
		    
		    AuthenticationRequestBuilder builder = OAuthClientRequest.authorizationLocation(AUTHORIZATION_URL);
		    if (state != null) builder.setState(state);
		    if (nonce != null) builder.setParameter("nonce", nonce);
		    if (acr != null) builder.setParameter("acr_values", acr?PASSWORD_MFA:PASSWORD);

		    if (prompt != null)
		    	builder.setParameter("prompt", prompt); // always login 

		    OAuthClientRequest request = builder
		        .setClientId(client_id)
		        .setRedirectURI(redirect_url)
		        .setResponseType("code")
		        .setScope("openid " + numworx_scope)
		       // state, nonce, claims
		        .buildQueryMessage();
		    return request.getLocationUri();    
		  }

	
	  UUClaims getToken(String code) throws OAuthSystemException, OAuthProblemException { // code fromn login response
		    OAuthClientRequest request = OAuthClientRequest
		        .tokenLocation(TOKEN_URL)
		        .setGrantType(GrantType.AUTHORIZATION_CODE)
		        .setClientId(client_id)
		        .setClientSecret(client_secret)
		        .setRedirectURI(redirect_url)
		        .setCode(code)
		        .buildBodyMessage();
		    long now = System.currentTimeMillis();
		    OAuthJSONAccessTokenResponse oAuthResponse = oAuthClient.accessToken(request);
		    UUClaims claims = idToken(oAuthResponse.getParam(ID_TOKEN));
		    claims.token = oAuthResponse.getOAuthToken();
		    claims.now = now;
		    return claims;
		  }

	static class ResourceRequest extends OAuthClientRequest {

	    ResourceRequest(String url) {
			super(url);
		}
	}
	  
	private Map<String, PublicKey> keys = new HashMap<String, PublicKey>();
	
	Map<String, PublicKey> getKeys() throws OAuthSystemException, OAuthProblemException, ParseException  {
		OAuthClientRequest request = new ResourceRequest(KEYS_URL);	
		OAuthResourceResponse response;
		response = oAuthClient.resource(request, "", OAuthResourceResponse.class);
		JSONParser parser = new JSONParser();
		Object parse = parser.parse(response.getBody());
		List<Object> list = (List) ((Map) parse).get("keys");
		Map<String, PublicKey> collect = (Map<String, PublicKey>) list.stream().collect(Collectors.toMap(this::getKey, this::getValue));
		keys.putAll(collect);
		return collect;
	}

	private String getKey(Object item) {
		return (String) ((Map)item).get("kid");
	}
	
	private PublicKey getValue(Object item) {
		Map map = (Map) item;
		String n = (String) map.get("n");
		String e = (String) map.get("e");
		Decoder urlDecoder = Base64.getUrlDecoder();
		byte[] nn = urlDecoder.decode(n);
		byte[] ee = urlDecoder.decode(e);
		try {
			KeyFactory keyFactory = KeyFactory.getInstance("RSA");
			BigInteger modulus = new BigInteger(+1, nn);
			BigInteger publicExponent = new BigInteger(+1, ee);
			KeySpec keySpec = new RSAPublicKeySpec(modulus, publicExponent);
			return keyFactory.generatePublic(keySpec);
			
		} catch (NoSuchAlgorithmException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (InvalidKeySpecException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return null;
	}
	
	class UUClaims {
		public long now;
		String sn, givenName, email, uid, insertion, affiliation, studentNumber;
		String nonce;
		Claims claims;
		OAuthToken token;

	}
	
	
	private String getString(Claims body, String key) {
		Object object = body.get(key);
		if (object == null || object instanceof String) return (String) object;
		if (object instanceof Collection) {
			Collection c = (Collection) object;
			if (c.isEmpty()) return null;
			object = c.toArray()[0]; // pick first
			if (object == null) return null;
			return object.toString();
		}
// conversion from other types to string
		return body.get(key, String.class);
	}
	
	UUClaims idToken(String idToken) {
		JwtParser parser = Jwts.parser().setSigningKeyResolver(this);

//		parser.setClock(new Clock() {
//
//			@Override
//			public Date now() {
//				return new Date(1621597188L);
//			} });

		parser.requireIssuer(ISSUER);
		parser.requireAudience(client_id);
		
		Jws<Claims> token = parser.parseClaimsJws(idToken);
		UUClaims u = new UUClaims();
		Claims body = u.claims = token.getBody();
		
		u.sn = getString(body,"sn");
		u.givenName = getString(body,"givenName");
		u.insertion = getString(body,"uuSurnamePrefix");
		u.email = getString(body,"mail");
		u.uid   = getString(body,"uuShortID");
		u.studentNumber = getString(body,"uuStudentNumber");
		u.affiliation = getString(body,"urn:mace:dir:attribute-def:eduPersonAffiliation");
		u.nonce = getString(body,"nonce");

		return u;
	}
	
	@Override
	public Key resolveSigningKey(JwsHeader header, Claims claims) {
		String kid = header.getKeyId();
		PublicKey publicKey = keys.get(kid);
		if (publicKey == null) {
			try {
				return getKeys().get(kid);
			} catch (OAuthSystemException | OAuthProblemException | ParseException e) {
				e.printStackTrace();
			}
		}
		return publicKey;
	}


	@Override
	public Key resolveSigningKey(JwsHeader header, String plaintext) {
		return resolveSigningKey(header, (Claims) null);
	}

	@Override
	public void login(HttpServletRequest req, HttpServletResponse resp, String state, String codeChallenge,
			Boolean asr) throws IOException, OAuthSystemException {
		String prompt = null;
		HttpSession session = req.getSession(false);
		if (session != null) {
			prompt = (String) session.getAttribute(OAUTH2_PROMPT);
		}
		resp.sendRedirect(login(state, codeChallenge, asr, prompt));
	}
}
