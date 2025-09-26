package nl.uu.fi.dwo.rest.dom.entities;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * The DWO profile information.
 * @author velth101
 *
 */
@XmlRootElement
public class DomDwoProfileFull extends DomDwoProfile implements Cloneable {
    private String dwoProfileText;
    private String dwoProfileDescription;
    private String base, language, title;
    
    public DomDwoProfileFull() {}
    
    public DomDwoProfileFull(DomDwoProfileFull p) {
        super(p);
    	dwoProfileText = p.dwoProfileText;
    	dwoProfileDescription = p.dwoProfileDescription;
    	base = p.base;
    	title = p.title;
    	language = p.language;
    }

    public DomDwoProfileFull duplicate() {
    	return new DomDwoProfileFull(this);
    }

	public String getDwoProfileText() {
		return dwoProfileText;
	}

	public void setDwoProfileText(String dwoProfileText) {
		this.dwoProfileText = dwoProfileText;
	}

	public String getDwoProfileDescription() {
		return dwoProfileDescription;
	}

	public void setDwoProfileDescription(String dwoProfileDescription) {
		this.dwoProfileDescription = dwoProfileDescription;
	}

	public String getBase() {
		return base;
	}

	public void setBase(String base) {
		this.base = base;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
    
}
