package fi.servlet.lti;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.RSAPrivateCrtKeySpec;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Base64.Encoder;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import edu.uoc.elc.lti.tool.ToolDefinition;
import edu.uoc.lti.jwt.AlgorithmFactory;
import sun.security.util.DerInputStream;
import sun.security.util.DerValue;

@SuppressWarnings("serial")
public class JwksServlet extends HttpServlet {

	
	static class Key {
		public String kty = "RSA", alg = "RS256", use= "sig";
		public String e, n;
		public String kid;
	}
	
	static class Keys {
		public List<Key> keys = new ArrayList<>();
	}

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {
    // just fake!
    // need real public key
//      InputStream in = getClass().getResourceAsStream("resources/jwks.json");
//      byte[] buffer = new byte[1024];
//      int len = in.read(buffer);
      resp.setContentType("application/json");
      resp.setCharacterEncoding("UTF-8");
     // resp.getOutputStream().write(buffer, 0, len);
      extractJwks(resp);
  }

  @SuppressWarnings("restriction")
  private void extractJwks(HttpServletResponse resp) throws IOException {
	  
	  ProviderInfo info = ProviderInfo.get("http://localhost:9001"); // the one and only....

      String privateKeyPem = info.privateKey;
      DerInputStream derReader = new DerInputStream(Base64.getDecoder().decode(privateKeyPem));
      DerValue[] seq = derReader.getSequence(0);

      if (seq.length < 9) {
          throw new IOException("Could not parse a PKCS1 private key.");
      }

      // skip version seq[0];
      BigInteger modulus = seq[1].getBigInteger();
      BigInteger publicExp = seq[2].getBigInteger();

      
      String kid = info.getKid();
	  ObjectMapper mapper = new ObjectMapper();
	  Key key = new Key();
	  key.kid = kid;
	  Encoder encoder = Base64.getUrlEncoder();
	  key.n = encoder.encodeToString(modulus.toByteArray());
	  key.e = encoder.encodeToString(publicExp.toByteArray());
	  Keys keys = new Keys();
	  keys.keys.add(key);
	  ObjectWriter writer = mapper.writerFor(Keys.class);
	  writer.writeValue(System.out, keys);
	  writer.writeValue(resp.getOutputStream(), keys);
	  
  }
  
  
}
