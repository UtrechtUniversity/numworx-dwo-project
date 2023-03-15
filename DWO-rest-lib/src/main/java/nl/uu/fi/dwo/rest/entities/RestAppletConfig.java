package nl.uu.fi.dwo.rest.entities;

import javax.xml.bind.annotation.XmlRootElement;

import nl.uu.fi.dwo.rest.dom.entities.DomAppletConfig;
import nl.uu.fi.dwo.rest.dom.entities.DomContext;

@XmlRootElement
public class RestAppletConfig {
	private DomContext restContext;
	private DomAppletConfig domAppletConfig;
	   
   public RestAppletConfig() {
   }
   
   public DomContext getRestContext() {
		return restContext;
   }
   public void setRestContext(DomContext restContext) {
	   this.restContext = restContext;
   }
   public DomAppletConfig getDomAppletConfig() {
	   return domAppletConfig;
   }
   public void setDomAppletConfig(DomAppletConfig domAppletConfig) {
	   this.domAppletConfig = domAppletConfig;
   }
	   
}
