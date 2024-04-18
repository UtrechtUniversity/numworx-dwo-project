package nl.numworx.schoolyear.jclient.dto;

import java.util.List;
import java.util.Map;

public class Content {
	public Map<String, Element> elements;
	public List<ElementId> entry_points;
	public List<ElementId> exit_points;
	public List bookmarks;
	public List sets;
}
