package nl.uu.fi.dwo.rest.dom.mfa;

import java.util.List;

public class MFA {
	public static final String MFA_RIGHT = "M";

	public String qr, secret, issuer;
	public List<String> recovery;
}
