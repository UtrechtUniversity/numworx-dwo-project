package fi.dwo.rest.entities;

import javax.xml.bind.annotation.XmlRootElement;

import fi.dwo.rest.dom.entities.DomContext;
import fi.dwo.rest.dom.entities.DomDwoProfile;

@XmlRootElement
public class RestDwoProfile {
	private DomContext restContext;
	private DomDwoProfile domDwoProfile;
	   
   public RestDwoProfile() {
   }
   
   public DomContext getRestContext() {
		return restContext;
   }
   public void setRestContext(DomContext restContext) {
	   this.restContext = restContext;
   }
   public DomDwoProfile getDomDwoProfile() {
	   return domDwoProfile;
   }
   public void setDomDwoProfile(DomDwoProfile domDwoProfile) {
	   this.domDwoProfile = domDwoProfile;
   }
	   
}
