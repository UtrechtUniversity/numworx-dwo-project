package nl.uu.fi.dwo.mobile.utils;

import java.util.ArrayList;
import java.util.HashMap;
//import java.awt.Font;

import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.Node;
import com.google.gwt.xml.client.Element;
import com.google.gwt.xml.client.NodeList;
import com.google.gwt.canvas.dom.client.CssColor;

/**
 * 
 * @author Evertson Croes
 * 
 */
public class StringCodeToHashMap
{
	public HashMap<String, Object> decodeStringToHashMap(Document dom)
	{
		HashMap<String, Object> result = new HashMap<String, Object>();
		Node main = dom.getElementsByTagName("object").item(0);
		result = convertNodeToHashMap(main);
		return result;
	}

	public HashMap<String, Object> convertNodeToHashMap(Node node)
	{
		HashMap<String, Object> result = new HashMap<String, Object>();
		//Extracts all nodes that are elements and puts them in an array. 
		ArrayList<Node> children = getElementList(node.getChildNodes());
		for (int i = 0; i < children.size(); i++)
		{
			Node currentNode = children.get(i);
			String nodeName = currentNode.getNodeName();
			if (nodeName.equalsIgnoreCase("void"))
			{
				ArrayList<Node> voidChildren = getElementList(currentNode.getChildNodes());
				//get key
				Node firstChild = voidChildren.get(0);
				//get value
				Node secondChild = voidChildren.get(1);
				String secondName = secondChild.getNodeName();
				//sets a string as value
				if (secondName.equalsIgnoreCase("string"))
				{
					if (secondChild.getFirstChild() != null)
						result.put(firstChild.getFirstChild().getNodeValue(), secondChild.getFirstChild().getNodeValue());

					else
						result.put(firstChild.getFirstChild().getNodeValue(), "");
				}
				
				//creates another hashmap and sets it as a value
				else if (secondName.equalsIgnoreCase("object"))
				{	
					//waarom werkt dit niet:
					//if (secondChild.getAttributes().getNamedItem("class")!=null && (secondChild.getAttributes().getNamedItem("class").toString()).equals("java.awt.Color"))
					//	if (secondChild.getAttributes().getNamedItem("class")!=null && secondChild.toString().indexOf('\n')>-1 && "<object class=\"java.awt.Color\">".equals(secondChild.toString().substring(0,secondChild.toString().indexOf('\n')).trim()))
					if("java.awt.Color".equals(((Element)secondChild).getAttribute("class")))
					{	ArrayList<Node> childs = getElementList(secondChild.getChildNodes());
						int red = Integer.parseInt(childs.get(0).getFirstChild().getNodeValue());
						int green = Integer.parseInt(childs.get(1).getFirstChild().getNodeValue());
						int blue = Integer.parseInt(childs.get(2).getFirstChild().getNodeValue());
						//CssColor color = CssColor.make(red, green, blue);
						//result.put(firstChild.getFirstChild().getNodeValue(), color);
						result.put(firstChild.getFirstChild().getNodeValue()+"_red", red);
						result.put(firstChild.getFirstChild().getNodeValue()+"_green", green);
						result.put(firstChild.getFirstChild().getNodeValue()+"_blue", blue);
					}
					//waarom werkt dit niet:
					//else if (secondChild.getAttributes().getNamedItem("class")!=null && (secondChild.getAttributes().getNamedItem("class").toString()).equals("java.awt.Font"))
					//	else if (secondChild.getAttributes().getNamedItem("class")!=null && secondChild.toString().indexOf('\n')>-1 && "<object class=\"java.awt.Font\">".equals(secondChild.toString().substring(0,secondChild.toString().indexOf('\n')).trim()))
					else if("java.awt.Font".equals(((Element)secondChild).getAttribute("class")))
					{	ArrayList<Node> childs = getElementList(secondChild.getChildNodes());
						String fontName = childs.get(0).getFirstChild().getNodeValue();
						int fontStyle = Integer.parseInt(childs.get(1).getFirstChild().getNodeValue());
						int fontSize = Integer.parseInt(childs.get(2).getFirstChild().getNodeValue());
						//Font font = new Font("SansSerif", fontStyle, fontSize);
						result.put(firstChild.getFirstChild().getNodeValue()+"_size", fontSize);
						result.put(firstChild.getFirstChild().getNodeValue()+"_style", fontStyle);
					}
					else result.put(firstChild.getFirstChild().getNodeValue(), convertNodeToHashMap(secondChild));
				}
				//sets an int as value
				else if (secondName.equalsIgnoreCase("int"))
				{
					if (secondChild.getFirstChild() != null)
						result.put(firstChild.getFirstChild().getNodeValue(), Integer.parseInt(secondChild.getFirstChild().getNodeValue()));

					else
						result.put(firstChild.getFirstChild().getNodeValue(), 0);
				}
				//sets an double as value
				else if (secondName.equalsIgnoreCase("double"))
				{
					if (secondChild.getFirstChild() != null)
						result.put(firstChild.getFirstChild().getNodeValue(), Double.parseDouble(secondChild.getFirstChild().getNodeValue()));

					else
						result.put(firstChild.getFirstChild().getNodeValue(), 0);
				}
				
				//sets a boolean as value
				else if (secondName.equalsIgnoreCase("boolean"))
				{
					if (secondChild.getFirstChild() != null)
					{
						if (secondChild.getFirstChild().getNodeValue().equalsIgnoreCase("true"))
							result.put(firstChild.getFirstChild().getNodeValue(), true);
						else
							result.put(firstChild.getFirstChild().getNodeValue(), false);
					}
					else
						result.put(firstChild.getFirstChild().getNodeValue(), true);

				}
				//sets an arraylist as value
				else if (secondName.equalsIgnoreCase("array"))
					result.put(firstChild.getFirstChild().getNodeValue(), convertNodeToArray(secondChild));
			}
			
		}
		return result;
	}

	public ArrayList<Node> getElementList(NodeList list)
	{
		ArrayList<Node> elements = new ArrayList<Node>();
		for (int i = 0; i < list.getLength(); i++)
		{
			Node currentNode = list.item(i);
			if (!currentNode.getNodeName().equalsIgnoreCase("#text"))
			{
				elements.add(currentNode);
			}
		}

		return elements;
	}

	public ArrayList<Object> convertNodeToArray(Node node)
	{
		ArrayList<Object> result = new ArrayList<Object>();
		ArrayList<Node> children = new ArrayList<Node>();
		children = getElementList(node.getChildNodes());

		for (int i = 0; i < children.size(); i++)
		{
			Node currentNode = children.get(i);
			ArrayList<Node> elements = getElementList(currentNode.getChildNodes());
			Node child = elements.get(0);
			if (child.getNodeName().equalsIgnoreCase("object"))
			{
				result.add(convertNodeToHashMap(child));
			}
			
			else if (child.getNodeName().equalsIgnoreCase("string"))
			{
				result.add(child.getFirstChild().getNodeValue());
			}
			else if (child.getNodeName().equalsIgnoreCase("int"))
			{
				result.add(Integer.parseInt(child.getFirstChild().getNodeValue()));
			}
			else if (child.getNodeName().equalsIgnoreCase("double"))
			{
				result.add(Double.parseDouble(child.getFirstChild().getNodeValue()));
			}
			else if (child.getNodeName().equalsIgnoreCase("boolean"))
			{
				if (child.getFirstChild().getNodeValue().equalsIgnoreCase("true"))
				{
					result.add(true);
				}
				else
				{
					result.add(false);
				}
			}
			else if (child.getNodeName().equalsIgnoreCase("array"))
			{
				result.add(convertNodeToArray(child));
			}

		}

		return result;
	}

}
