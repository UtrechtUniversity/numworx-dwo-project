package fi.dwo.gwt.lib.rest.CallManagers;

import com.google.gwt.core.client.GWT;

import fi.dwo.gwt.lib.rest.client.RestCallers.ScoContextRestCaller;
import fi.dwo.gwt.lib.rest.client.RestCallers.SecuredStudentExamScoContextRestCaller;
import fi.dwo.gwt.lib.rest.client.RestCallers.SecuredUserScoContextRestCaller;

public class SecuredUserScoContextManager extends PublicScoContextManager  {

	public SecuredUserScoContextManager() {
		super(GWT.<ScoContextRestCaller>create(SecuredUserScoContextRestCaller.class));
	}
	
	public SecuredUserScoContextManager(boolean secure) {
		super( secure 
				? GWT.<ScoContextRestCaller>create(SecuredStudentExamScoContextRestCaller.class)
				: GWT.<ScoContextRestCaller>create(SecuredUserScoContextRestCaller.class)
				);
	}
	
}
