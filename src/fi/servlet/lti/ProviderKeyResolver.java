package fi.servlet.lti;

import java.math.BigInteger;
import java.security.Key;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Base64.Decoder;
import java.util.stream.Collectors;

import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwsHeader;
import io.jsonwebtoken.SigningKeyResolver;

public class ProviderKeyResolver implements SigningKeyResolver {

	/*
	 * {"keys":[{
	 * 		"kty":"RSA",
	 * 		"alg":"RS256",
	 * 		"use":"sig",
	 * 		"e":"AQAB",
	 * 		"n":"8osiSa75nmqmakwNNocLA2N2huWM9At_tjSZOFX1r4-PDclSzxhMw-ZcgHH-E_05Ec6Vcfd75i8Z-Bxu4ctbYk2FNIvRMN5UgWqxZ5Pf70n8UFxjGqdwhUA7_n5KOFoUd9F6wLKa6Oh3OzE6v9-O3y6qL40XhZxNrJjCqxSEkLkOK3xJ0J2npuZ59kipDEDZkRTWz3al09wQ0nvAgCc96DGH-jCgy0msA0OZQ9SmDE9CCMbDT86ogLugPFCvo5g5zqBBX9Ak3czsuLS6Ni9Wco8ZSxoaCIsPXK0RJpt6Jvbjclqb4imsobifxy5LsAV0l_weNWmU2DpzJsLgeK6VVw",
	 * 		"kid":"fcec4f14-28a5-4697-87c3-e9ac361dada5"
	 *  }]}
	 */
	Map<String, PublicKey> keys = new HashMap<String, PublicKey>();
	public Map<String, PublicKey> getKeys(String iss) throws ParseException  {

		
		String response ="{\"keys\":[{\"kty\":\"RSA\",\"alg\":\"RS256\",\"use\":\"sig\",\"e\":\"AQAB\",\"n\":\"8osiSa75nmqmakwNNocLA2N2huWM9At_tjSZOFX1r4-PDclSzxhMw-ZcgHH-E_05Ec6Vcfd75i8Z-Bxu4ctbYk2FNIvRMN5UgWqxZ5Pf70n8UFxjGqdwhUA7_n5KOFoUd9F6wLKa6Oh3OzE6v9-O3y6qL40XhZxNrJjCqxSEkLkOK3xJ0J2npuZ59kipDEDZkRTWz3al09wQ0nvAgCc96DGH-jCgy0msA0OZQ9SmDE9CCMbDT86ogLugPFCvo5g5zqBBX9Ak3czsuLS6Ni9Wco8ZSxoaCIsPXK0RJpt6Jvbjclqb4imsobifxy5LsAV0l_weNWmU2DpzJsLgeK6VVw\",\"kid\":\"fcec4f14-28a5-4697-87c3-e9ac361dada5\"}]}";
		JSONParser parser = new JSONParser();
		Object parse = parser.parse(response);
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

	
	public Key resolveSigningKey(JwsHeader header, Claims claims) {
		String kid = header.getKeyId();
		String iss = claims.getIssuer();
		PublicKey publicKey = keys.get(kid);
		if (publicKey == null) {
			try {
				return getKeys(iss).get(kid);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		return publicKey;
	}

	public Key resolveSigningKey(JwsHeader header, String plaintext) {
		return resolveSigningKey(header, (Claims) null);
	}

}
