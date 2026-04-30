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
		if (!System.getProperty("CONEXT_SECRET", "").isEmpty())
			productie();
		}
	public String toString() { return "conext"; }

	private void productie() {
	// https://connect.surfconext.nl/.well-known/openid-configuration
		
		client_id = "app.dwo.nl";
		client_secret = System.getProperty("CONEXT_SECRET");
		ISSUER = "https://connect.surfconext.nl";
		AUTHORIZATION_URL = "https://connect.surfconext.nl/oidc/authorize";
		TOKEN_URL = 	"https://connect.surfconext.nl/oidc/token";
		KEYS_URL = "https://connect.surfconext.nl/oidc/certs";
		USERINFO = "https://connect.surfconext.nl/oidc/userinfo";
	}
	
	public ConextLogin(ServletConfig cfg) {
		super(cfg);
		surfconext_test();
	}

}
