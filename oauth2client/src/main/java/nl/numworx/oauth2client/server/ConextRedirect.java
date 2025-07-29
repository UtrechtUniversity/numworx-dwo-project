package nl.numworx.oauth2client.server;

public class ConextRedirect extends EntreeSRedirect {

	public ConextRedirect() {
	}

	@Override
	protected EntreeSLogin createLogin() {
		return new ConextLogin(getServletConfig());
	}

}
