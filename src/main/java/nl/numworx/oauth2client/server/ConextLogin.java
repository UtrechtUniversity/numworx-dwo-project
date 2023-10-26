package nl.numworx.oauth2client.server;

import javax.servlet.ServletConfig;

public class ConextLogin extends EntreeSLogin implements Login {

	public ConextLogin(ServletConfig cfg) {
		super(cfg);
	}

}
