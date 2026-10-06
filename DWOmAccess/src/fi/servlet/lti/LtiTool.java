package fi.servlet.lti;

import java.util.logging.Level;
import java.util.logging.Logger;

import edu.uoc.elc.lti.tool.Registration;
import edu.uoc.elc.lti.tool.Tool;
import edu.uoc.elc.lti.tool.ToolBuilders;
import edu.uoc.lti.claims.ClaimAccessor;
import edu.uoc.lti.claims.ClaimsEnum;
import edu.uoc.lti.deeplink.content.Presentation;
import edu.uoc.lti.oidc.OIDCLaunchSession;
import nl.uu.fi.dwo.lms.jclient.lib.rest.cache.PublicProfileCache;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;

public class LtiTool extends Tool {
	private Presentation presentation;
	private ClaimAccessor claimAccessor;
	private OIDCLaunchSession launchSession;
	private Logger LOG = Logger.getLogger(getClass().getName());

	public LtiTool(Registration registration, ClaimAccessor claimAccessor, OIDCLaunchSession oidcLaunchSession,
			ToolBuilders toolBuilders) {
		super(registration, claimAccessor, oidcLaunchSession, toolBuilders);
		this.claimAccessor = claimAccessor;
		this.launchSession = oidcLaunchSession;
	}

	@Override
	public boolean validate(String token, String state) {
		
		LOG.warning("deployment id = " + launchSession.getDeploymentId() );

		launchSession.setDeploymentId("1"); // ons kent ons!
		
		
		boolean validate = super.validate(token, state);
		presentation = claimAccessor.get(ClaimsEnum.PRESENTATION, Presentation.class);
		return validate;
	}

	/**
	 * @return the presentation
	 */
	public Presentation getPresentation() {
		return presentation;
	}

	@Override
	public String getLocale() {
		String locale = super.getLocale();
		if (locale == null && presentation != null) 
			return presentation.getLocale();
		return locale;
	}

// from registration: default profile and default school (as BRIN)
	public String profile() {
		String profile = getCustomParameter("profile"); 
		if (profile == null) 
			profile = "bv"; // TODO: default from registration
		try {
			profile = PublicProfileCache.get(profile).asLong().toString();
		} catch (Exception e) {
			LOG.log(Level.SEVERE, "no profile " + profile, e);
			profile = null; 
		}
		return profile;
	}
	
	public String BRIN() {
// platform.guid als schoolidentifier
		if (getPlatform() != null) 
			return getPlatform().getGuid();
		return "20AI00";
	}

	public String className() {
// context.label als klasnaam
		if (getContext() != null) 
			return getContext().getLabel();
		return null;
	}
}
