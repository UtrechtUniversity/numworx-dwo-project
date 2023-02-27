package nl.uu.fi.dwo.rest.entities;

import javax.xml.bind.annotation.XmlRootElement;

import nl.uu.fi.dwo.rest.dom.entities.DomContext;
import nl.uu.fi.dwo.rest.dom.entities.DomDwoProfileFull;

@XmlRootElement
public class RestDwoProfileFull {
	private DomContext restContext;
	private DomDwoProfileFull domDwoProfile;
	   
   public RestDwoProfileFull() {
   }
   
   public DomContext getRestContext() {
		return restContext;
   }
   public void setRestContext(DomContext restContext) {
	   this.restContext = restContext;
   }
   public DomDwoProfileFull getDomDwoProfile() {
	   return domDwoProfile;
   }
   public void setDomDwoProfile(DomDwoProfileFull domDwoProfile) {
	   this.domDwoProfile = domDwoProfile;
   }
	   
}
