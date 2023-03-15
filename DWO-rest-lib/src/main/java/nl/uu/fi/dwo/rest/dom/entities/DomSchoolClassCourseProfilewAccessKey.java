package nl.uu.fi.dwo.rest.dom.entities;

import javax.xml.bind.annotation.XmlRootElement;


/**
 *
 * @author Gert van der Plas
 */
@XmlRootElement
public class DomSchoolClassCourseProfilewAccessKey extends DomSchoolClassCourseAndProfile{
    private String accessKey;

    public DomSchoolClassCourseProfilewAccessKey() {
    }

	/**
	 * @return the accessKey
	 */
	public String getAccessKey() {
		return accessKey;
	}

	/**
	 * @param accessKey the accessKey to set
	 */
	public void setAccessKey(String accessKey) {
		this.accessKey = accessKey;
	}


}
