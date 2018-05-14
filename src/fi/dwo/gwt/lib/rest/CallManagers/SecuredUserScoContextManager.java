package fi.dwo.gwt.lib.rest.CallManagers;

import com.google.gwt.core.client.GWT;

import fi.dwo.gwt.lib.rest.client.RestCallers.ScoContextRestCaller;
import fi.dwo.gwt.lib.rest.client.RestCallers.SecuredStudentExamScoContextRestCaller;
import fi.dwo.gwt.lib.rest.client.RestCallers.SecuredUserScoContext2RestCaller;

public class SecuredUserScoContextManager extends PublicScoContextManager  {

	public SecuredUserScoContextManager() {
		super(GWT.<ScoContextRestCaller>create(SecuredUserScoContext2RestCaller.class));
	}
	
	public SecuredUserScoContextManager(boolean secure) {
		super( secure 
				? GWT.<ScoContextRestCaller>create(SecuredStudentExamScoContextRestCaller.class)
				: GWT.<ScoContextRestCaller>create(SecuredUserScoContext2RestCaller.class)
				);
	}
	
}
