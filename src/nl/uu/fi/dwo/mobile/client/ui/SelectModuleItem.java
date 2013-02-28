package nl.uu.fi.dwo.mobile.client.ui;

import java.util.List;

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
	public enum Type
	{
		SCO, MODULE, FOLDER, ROOT
	}

	private String name;
	private String file;
	private int id;

	private Type type = Type.ROOT;
	private List<SelectModuleItem> children;

	public SelectModuleItem(int id, String name, String file)
	{
		this.id = id;
		this.name = name;
		this.file = file;
		this.type = Type.SCO;
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
}
