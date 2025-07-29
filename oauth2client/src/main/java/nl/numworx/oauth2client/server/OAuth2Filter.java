package nl.numworx.oauth2client.server;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Objects;
import java.util.Random;
import java.util.logging.Logger;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import nl.uu.fi.dwo.lms.jclient.lib.rest.managers.OAuthManager;
import nl.uu.fi.dwo.lms.jclient.lib.rest.transport.StoredRestManager;

public class OAuth2Filter implements Filter {
	
	final static private Logger LOG = Logger.getLogger(OAuth2Filter.class.getName());

	private final class BearerWrapper extends HttpServletRequestWrapper {
		private final String bearer;

		private BearerWrapper(HttpServletRequest request, String bearer) {
			super(request);
			this.bearer = bearer;
		}

		@Override
		public String getAuthType() {
			return "Bearer";
		}

		@Override
		public String getRemoteUser() {
			return bearer;
		}
	}

	public static final String PREFIX = "nl.numworx.oauth2client.server.Oauth2Filter.";
	public static final String PREFIX_TOKEN = PREFIX + "token.";
	
	private String authz = "/dwo/oauth2/mfalogin";
	private String client_id = null;
	private StoredRestManager instance = StoredRestManager.getInstance().duplicate();
	private OAuthManager manager;
	private boolean always;
	
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		always = "always".equals(filterConfig.getInitParameter("fresh"));
		
		String param = filterConfig.getInitParameter("authz");
		if (param != null) authz = param;
		param = filterConfig.getInitParameter("client_id");
		if (param != null) client_id = param;
		param = filterConfig.getServletContext().getInitParameter("dbrest.url");
		try {
			instance.getAuthenticator().setServerUrlPath(new URL(param));
		} catch (MalformedURLException e) {
		}
		manager = new OAuthManager(instance);
		
	}
	
	public static void newsession(HttpSession session) {
		Enumeration<String> keys = session.getAttributeNames();
		ArrayList<String> todelete = new ArrayList<>();
		while (keys.hasMoreElements()) {
			String key = keys.nextElement();
			if (key.startsWith(PREFIX_TOKEN)) todelete.add(key);
		}
		todelete.forEach(session::removeAttribute);
	}
	
	@SuppressWarnings("deprecation")
	private void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
		HttpSession storage = request.getSession();
		LOG.severe("do Filter session=" + storage.getId() + ",keys "  + Arrays.toString(storage.getValueNames()));
		String code = request.getParameter("code");
		String redirect = request.getRequestURL().toString();
		if (code != null) {
			String state = request.getParameter("state");
			Object orgstate = storage.getAttribute(PREFIX + "state");
			Object verifier = storage.getAttribute(PREFIX + "verifier");
			if (Objects.equals(orgstate, state)) {
				String retry = manager.authorization_token(code, client_id, Objects.toString(verifier, null), redirect);
			if(retry != null) {
				String bearer = instance.getBasicAuthString().substring(7);
				LOG.severe("chain with new bearer " + bearer);
				storage.removeAttribute(PREFIX + "state");
				storage.setAttribute(PREFIX_TOKEN + "bearer", bearer);
				storage.setAttribute(PREFIX_TOKEN + "retry", retry);
				HttpServletRequestWrapper wrap = new BearerWrapper(request, bearer);
				chain.doFilter(wrap, response);
				return;
			}}
		}
		Object bearer = storage.getAttribute(PREFIX_TOKEN + "bearer");
		if (always && "GET".equals(request.getMethod()))
			bearer = null;
		Object login = storage.getAttribute(Login.OAUTH2_PROMPT);
		LOG.severe("bearer=" + bearer + ",prompt="+login);
		if (bearer != null && login == null) {
			request = new BearerWrapper(request, bearer.toString());
			LOG.info("chain with old bearer");
			chain.doFilter(request, response); // als response.status = need authetication, remove bearer
			return;
		} else if (login != null) {
			LOG.info("remove bearer");
			storage.removeAttribute(PREFIX_TOKEN + "bearer");
		}
		LOG.info("redirect to " + authz);
		String state = randomString(64);
		storage.setAttribute(PREFIX + "state", state);
		StringBuilder sb = new StringBuilder(authz)
		.append("?response_type=code")
		.append("&redirect_uri=").append(redirect)
		.append("&state=").append(state)
		.append("&client_id=").append(client_id);
		
		response.sendRedirect(sb.toString());
	}
	
	private String randomString(int length) {
		Random random = new Random();
		char[] possible = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789".toCharArray();
		char[] result = new char[length];
		for(int i = 0; i < length; i++) {
			result[i] = possible[random.nextInt(possible.length)];
		}
		return new String(result);
	}


	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		if (client_id != null)
			doFilter( (HttpServletRequest) request, (HttpServletResponse) response, chain);		
		else
			chain.doFilter(request, response);
	}

	@Override
	public void destroy() {
	}

}
