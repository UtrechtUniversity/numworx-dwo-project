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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Base64.Decoder;
import java.util.stream.Collectors;

import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwsHeader;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SigningKeyResolver;
import io.jsonwebtoken.impl.DefaultClaims;

public class EntreeSLogin implements SigningKeyResolver, Login {


    private static final String ID_TOKEN= "id_token";
//    private static final String PASSWORD = "urn:uu.nl:idp:contract:password";
//    private static final String PASSWORD_MFA = "urn:uu.nl:idp:contract:password:multifactor";

	String client_id = "entree-s.dwo.nl";
	String client_secret = "GnNH7WqZkoy3NNAWjqzXIjrQWIFh2mTAoHkK";
	
	String redirect_url = "https://entree-s.dwo.nl/redirect";
	
		
	String ISSUER = "https://oidcng.entree-s.kennisnet.nl";
	String AUTHORIZATION_URL = "https://oidcng.entree-s.kennisnet.nl/oidc/authorize";
	String TOKEN_URL = 	"https://oidcng.entree-s.kennisnet.nl/oidc/token";
	String KEYS_URL = "https://oidcng.entree-s.kennisnet.nl/oidc/certs";
	String USERINFO = "https://oidcng.entree-s.kennisnet.nl/oidc/userinfo";

	private void productie() {
	// https://oidcng.entree.kennisnet.nl/.well-known/openid-configuration
		
		client_id = "entree.dwo.nl";
		client_secret = System.getProperty("ENTREE_SECRET");
		ISSUER = "https://oidcng.entree.kennisnet.nl";
		AUTHORIZATION_URL = "https://oidcng.entree.kennisnet.nl/oidc/authorize";
		TOKEN_URL = 	"https://oidcng.entree.kennisnet.nl/oidc/token";
		KEYS_URL = "https://oidcng.entree.kennisnet.nl/oidc/certs";
		USERINFO = "https://oidcng.entree.kennisnet.nl/oidc/userinfo";
	}
	
	
	
	private OAuthClient oAuthClient = new OAuthClient(new URLConnectionClient());


    Long expiresIn, now;
	private OAuthToken token;
	String numworx_scope = "profile";
	
	public String toString() { return "entree"; }
 	
	public EntreeSLogin(ServletConfig cfg) {
		String allow = System.getProperty("ALLOW_ORIGIN");
		if (allow != null) {
			allow = allow.split("\\s+")[0]; // spaces als separator
			this.redirect_url = allow + "/redirect";
		}
		if (!System.getProperty("ENTREE_SECRET", "").isEmpty())
			productie();
	}

	public String login() throws OAuthSystemException {
    	return login(null, null, null);
    }
    
	public String login(String state, String nonce, Boolean acr) throws OAuthSystemException {
		    
		    AuthenticationRequestBuilder builder = OAuthClientRequest.authorizationLocation(AUTHORIZATION_URL);
		    if (state != null) builder.setState(state);
		    if (nonce != null) builder.setParameter("nonce", nonce);
		    //if (acr != null) builder.setParameter("acr_values", acr?PASSWORD_MFA:PASSWORD);
		    //builder.setParameter("prompt", "login"); // always login 
		    OAuthClientRequest request = builder
		        .setClientId(client_id)
		        .setRedirectURI(redirect_url)
		        .setResponseType("code")
		        .setScope(("openid " + numworx_scope).trim() )
		       // state, nonce, claims
		        .buildQueryMessage();
		    return request.getLocationUri();    
		  }

	
	  Claims getToken(String code) throws OAuthSystemException, OAuthProblemException { // code fromn login response
		    OAuthClientRequest request = OAuthClientRequest
		        .tokenLocation(TOKEN_URL)
		        .setGrantType(GrantType.AUTHORIZATION_CODE)
		        .setClientId(client_id)
		        .setClientSecret(client_secret)
		        .setRedirectURI(redirect_url)
		        .setCode(code)
		        .buildBodyMessage();
		    now = System.currentTimeMillis();
		    OAuthJSONAccessTokenResponse oAuthResponse = oAuthClient.accessToken(request);
		    Claims claims = idToken(oAuthResponse.getParam(ID_TOKEN));
		    token = oAuthResponse.getOAuthToken();
		    return claims;
		  }

	static class ResourceRequest extends OAuthClientRequest {

	    ResourceRequest(String url) {
			super(url);
		}
	}
	  
	
	Map<String, PublicKey> keys = new HashMap<String, PublicKey>();
	
	
	public Map<String, PublicKey> getKeys() throws OAuthSystemException, OAuthProblemException, ParseException  {
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
	
	String sn, givenName, email, uid, insertion, affiliation, studentNumber;
	String nonce;
	
	
	Claims idToken(String idToken) {
		JwtParser parser = Jwts.parser().setSigningKeyResolver(this);
		parser.setAllowedClockSkewSeconds(10);

//		parser.setClock(new Clock() {
//
//			@Override
//			public Date now() {
//				return new Date(1621597188L);
//			} });

		parser.requireIssuer(ISSUER);
		parser.requireAudience(client_id);
		
		Jws<Claims> token = parser.parseClaimsJws(idToken);	
		Claims body = token.getBody();
		
		uid   = body.getSubject();
		affiliation = body.get("eduPersonAffiliation", String.class);
		nonce = body.get("nonce", String.class);

		return body;
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

	public Claims userInfo() throws OAuthSystemException, OAuthProblemException, ParseException {
		OAuthClientRequest request = new ResourceRequest(USERINFO);	
		request.addHeader("Authorization", token.getTokenType() + " " + token.getAccessToken());
		OAuthResourceResponse response;
		response = oAuthClient.resource(request, "", OAuthResourceResponse.class);
		JSONParser parser = new JSONParser();
		Object parse = parser.parse(response.getBody());
	    Claims body = new DefaultClaims((Map<String, Object>) parse);
		sn = body.get("sn", String.class);
		if (sn == null) sn = body.get("family_name", String.class);
		givenName = body.get("givenName", String.class);
		if (givenName == null) givenName = body.get("given_name", String.class);
		
		insertion = body.get("nlEduPersonTussenvoegsels", String.class);
		email = body.get("mail", String.class);
		uid   = body.get("uid", String.class);
		if (uid == null) {
			JSONArray uids = body.get("uids", JSONArray.class);
			if (uids != null && uids.size() > 0) {
				uid = (String) uids.get(0);
			}
		}
		if (uid == null) uid = body.getSubject();
		affiliation = body.get("eduPersonAffiliation", String.class);
		if (affiliation == null) {
			List list = body.get("eduperson_affiliation", List.class);
			if (list != null) affiliation = list.toString(); // OID.
		}
	    return body;
	}
	
	
	

	@Override
	public Key resolveSigningKey(JwsHeader header, String plaintext) {
		return resolveSigningKey(header, (Claims) null);
	}

	@Override
	public void login(HttpServletRequest req, HttpServletResponse resp, String state, String codeChallenge,
			Boolean asr) throws IOException, OAuthSystemException {
		resp.sendRedirect(login(state, codeChallenge, asr));
	}

}
