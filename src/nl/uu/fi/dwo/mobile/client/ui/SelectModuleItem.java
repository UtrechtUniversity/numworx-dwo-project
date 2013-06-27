package nl.uu.fi.dwo.mobile.client.ui;

import java.util.List;
import java.util.Map;

import nl.uu.fi.dwo.mobile.DWOplayer;

import com.google.gwt.core.client.GWT;
import com.google.gwt.xml.client.Node;

/**
 * Item in selection list
 * 
 * @author Danny Hendrix
 * 
 */
public class SelectModuleItem
{
	
	public static final String PREFIX = DWOplayer.PREFIX;
	
	public enum Type
	{
		SCO, MODULE, FOLDER, ROOT
	}

	public static final SelectModuleItem ROOT = new SelectModuleItem(null, SelectModuleItem.Type.ROOT);
	static {
		ROOT.setName("Standaard DWO Modules");
		ROOT.setDescription("<html><body><b>DWO-modules</b><br>In de DWO is veel oefenmateriaal beschikbaar.  Naast dit oefenmateriaal zijn er ook diverse volledige lessen en lessenseries beschikbaar, die kunnen worden gebruikt als aanvulling op of zelfs vervanging van het reguliere boek. In de etalage kunt u een indruk krijgen van de mogelijkheden die de DWO biedt voor de wiskundeles.</body></html>");
	}
	
	
	private String name;
	private String file;
	private String description;
	private int id;

	private Type type = Type.ROOT;
	private List<SelectModuleItem> children;
	private SelectModuleItem parent;

	public SelectModuleItem(int id, String name, String file)
	{
		this.id = id;
		this.name = name;
		this.file = file;
		this.type = Type.SCO;
	}

	public SelectModuleItem(Map<String,Object> map, Type type)
	{
		switch(type) {
		case MODULE:
			if(Boolean.TRUE.equals(map.get("withChildren")))
				this.type = Type.FOLDER;
			else
				this.type = Type.MODULE;
			this.name = map.get("name").toString();
			this.id   = ((Integer) map.get("courseID")).intValue();
			this.description = (String) map.get("description");
			break;
		case SCO:
			this.type = type;
			this.name = map.get("sconame").toString();
			this.description = (String) map.get("description");
			this.id = ((Integer) map.get("scoID")).intValue();
			this.file = PREFIX + this.id;
		break;
// more to follow....			
			
			
		}
	}
	
	
	
	public SelectModuleItem(int id, Node node)
	{
		this.id = id;
		for (int i = 0; i < node.getChildNodes().getLength(); i++)
		{

			Node curr = node.getChildNodes().item(i);
			if (curr.getNodeName().equalsIgnoreCase("name"))
				this.name = curr.getChildNodes().toString();
			if (curr.getNodeName().equalsIgnoreCase("file"))
				this.file = curr.getChildNodes().toString();

		}
		GWT.log(this.name + " " + this.file);
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getFile()
	{
		return file;
	}

	public void setFile(String file)
	{
		this.file = file;
	}

	public int getID()
	{
		return this.id;
	}

	public void setID(int id)
	{
		this.id = id;
	}

	public Type getType()
	{
		return type;
	}

	public void setType(Type type)
	{
		this.type = type;
	}

	public List<SelectModuleItem> getChildren()
	{
		return children;
	}

	public void setChildren(List<SelectModuleItem> children)
	{
		this.children = children;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public SelectModuleItem getParent() {
		return parent;
	}

	public void setParent(SelectModuleItem parent) {
		this.parent = parent;
	}
}
