package nl.uu.fi.dwo.mobile.client.ui;

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
	private String name;
	private String file;
	private int id;

	public SelectModuleItem(int id, String name, String file)
	{
		this.id = id;
		this.name = name;
		this.file = file;
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
}
