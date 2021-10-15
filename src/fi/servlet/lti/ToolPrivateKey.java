package fi.servlet.lti;

import sun.security.util.DerInputStream;
import sun.security.util.DerValue;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.spec.RSAPrivateCrtKeySpec;
import java.util.Base64;

@SuppressWarnings("restriction")
public class ToolPrivateKey {
  public static final ToolPrivateKey INSTANCE = new ToolPrivateKey();
  private final static String PEM_RSA_PRIVATE_START = "-----BEGIN RSA PRIVATE KEY-----";
  private final static String PEM_RSA_PRIVATE_END = "-----END RSA PRIVATE KEY-----";

  public static void main(String[] args) throws Exception {
    Object o = INSTANCE.getPrivateKey();
    System.out.println(o.getClass());
    System.out.println(o);
  }

  String getKid() {
    return "58f36e10-c1c1-4df0-af8b-85c857d1634f";
  }
  // see https://stackoverflow.com/questions/7216969/getting-rsa-private-key-from-pem-base64-encoded-private-key-file
  Key getPrivateKey() throws IOException, GeneralSecurityException {
    InputStream in = getClass().getResourceAsStream("resources/private.key");
    byte[] all = new byte[in.available()];
    in.read(all);
    in.close();
    String privateKeyPem = new String(all);
    if (privateKeyPem.indexOf(PEM_RSA_PRIVATE_START) != -1) {  // PKCS#1 format

      privateKeyPem = privateKeyPem.replace(PEM_RSA_PRIVATE_START, "").replace(PEM_RSA_PRIVATE_END, "");
      privateKeyPem = privateKeyPem.replaceAll("\\s", "");

      DerInputStream derReader = new DerInputStream(Base64.getDecoder().decode(privateKeyPem));

      DerValue[] seq = derReader.getSequence(0);

      if (seq.length < 9) {
          throw new GeneralSecurityException("Could not parse a PKCS1 private key.");
      }

      // skip version seq[0];
      BigInteger modulus = seq[1].getBigInteger();
      BigInteger publicExp = seq[2].getBigInteger();
      BigInteger privateExp = seq[3].getBigInteger();
      BigInteger prime1 = seq[4].getBigInteger();
      BigInteger prime2 = seq[5].getBigInteger();
      BigInteger exp1 = seq[6].getBigInteger();
      BigInteger exp2 = seq[7].getBigInteger();
      BigInteger crtCoef = seq[8].getBigInteger();

      RSAPrivateCrtKeySpec keySpec = new RSAPrivateCrtKeySpec(modulus, publicExp, privateExp, prime1, prime2, exp1, exp2, crtCoef);

      KeyFactory factory = KeyFactory.getInstance("RSA");

      return factory.generatePrivate(keySpec);
    }
    return null;
    
  }
}
