package nl.uu.fi.dwo.rest.security;

/**
 *
 * @author Gert van der Plas
 */
public class TOTPBuilder {
//    HMac sha1Hmac = new HMac(new SHA1Digest());
//		sha1Hmac.init(new KeyParameter(seed20));
//		HMac sha256Hmac = new HMac(new SHA256Digest());
//		sha256Hmac.init(new KeyParameter(seed32));
//		HMac sha512Hmac = new HMac(new SHA512Digest());
//		sha512Hmac.init(new KeyParameter(seed64));
//According to RFC 6238, the reference implementation is as follows:
//
//Generate a key, K, which is an arbitrary bytestring, and share it securely with the client.
//Agree upon an epoch, T0, and an interval, TI, which will be used to calculate the value of the counter C (defaults are the Unix epoch as T0 and 30 seconds as TI)
//Agree upon a cryptographic hash method (default is SHA-1)
//Agree upon a token length, N (default is 6)
 // See appendix A and B   https://tools.ietf.org/id/draft-mraihi-totp-timebased-06.html
}
