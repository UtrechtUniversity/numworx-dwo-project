package fi.servlet.lti;

import edu.uoc.elc.lti.tool.Registration;
import edu.uoc.elc.lti.tool.Tool;
import edu.uoc.elc.lti.tool.ToolBuilders;
import edu.uoc.lti.claims.ClaimAccessor;
import edu.uoc.lti.claims.ClaimsEnum;
import edu.uoc.lti.deeplink.content.Presentation;
import edu.uoc.lti.oidc.OIDCLaunchSession;

public class LtiTool extends Tool {
	private Presentation presentation;
	private ClaimAccessor claimAccessor;

	public LtiTool(Registration registration, ClaimAccessor claimAccessor, OIDCLaunchSession oidcLaunchSession,
			ToolBuilders toolBuilders) {
		super(registration, claimAccessor, oidcLaunchSession, toolBuilders);
		this.claimAccessor = claimAccessor;
	}

	@Override
	public boolean validate(String token, String state) {
		// TODO Auto-generated method stub
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

}
