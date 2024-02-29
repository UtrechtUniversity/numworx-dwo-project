package nl.uu.fi.dwo.rest.entities;

import javax.xml.bind.annotation.XmlRootElement;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolDataFull;

@XmlRootElement
public class RestSchoolDataFull extends RestContext {

	public RestSchoolDataFull() {
	}

	public DomSchoolDataFull getData() {
		return data;
	}

	public void setData(DomSchoolDataFull restData) {
		this.data = restData;
	}

	private DomSchoolDataFull data;
}
