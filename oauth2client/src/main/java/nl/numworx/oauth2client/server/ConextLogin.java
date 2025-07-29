package nl.numworx.oauth2client.server;

import javax.servlet.ServletConfig;

public class ConextLogin extends EntreeSLogin implements Login {

	
	private void surfconext_test() {
		client_id = "test.dwo.nl";
		client_secret = "prmr766D8QrgtQLut9Dx";
		ISSUER = "https://connect.test.surfconext.nl";
		AUTHORIZATION_URL = "https://connect.test.surfconext.nl/oidc/authorize";
		TOKEN_URL = 	"https://connect.test.surfconext.nl/oidc/token";
		KEYS_URL = "https://connect.test.surfconext.nl/oidc/certs";
		USERINFO = "https://connect.test.surfconext.nl/oidc/userinfo";

		String allow = System.getProperty("ALLOW_ORIGIN");
		if (allow != null) {
			allow = allow.split("\\s+")[0]; // spaces als separator
			this.redirect_url = allow + "/redirect/conext";
		}
	}
	public String toString() { return "conext"; }
	
	public ConextLogin(ServletConfig cfg) {
		super(cfg);
		surfconext_test();
	}

}
