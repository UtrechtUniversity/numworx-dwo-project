package nl.numworx.schoolyear.jclient.dto;

public class ElementId {
	public ElementId() {}

	public ElementId(String uuid) {
		this.element_id = uuid;
	}

	public String element_id; // UUID
}
