package nl.uu.fi.dwo.mobile.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.Element;
import com.google.gwt.xml.client.NamedNodeMap;
import com.google.gwt.xml.client.Node;
import com.google.gwt.xml.client.NodeList;

/**
 * 
 * @author Evertson Croes
 * 
 */
public class StringCodeToHashMap
{
	private static Logger logger = Logger.getLogger("StringCodeToHashMap");
	
	
	private HashMap<String,Element> refs = new HashMap<String,Element>();
	
	public HashMap<String, Object> decodeStringToHashMap(Document dom)
	{
		refs.clear();
		HashMap<String, Object> result;
		Node main = dom.getElementsByTagName("object").item(0);
		result = convertNodeToHashMap(main);
		return result;
	}

	
	
	public HashMap<String, Object> convertNodeToHashMap(Node node)
	{
		HashMap<String, Object> result = new HashMap<String, Object>();
try { 		//Extracts all nodes that are elements and puts them in an array. 
		ArrayList<Node> children = getElementList(node.getChildNodes());
		for (int i = 0; i < children.size(); i++)
		{
			Node currentNode = children.get(i);
			if(currentNode == null)
			{
				logger.severe("node " + i + "/" + children.size() + " is null");
				continue;
			}
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
				Node keyNode = firstChild.getFirstChild();
				String keyName = keyNode != null ? keyNode.getNodeValue() : "";
				if (secondName.equalsIgnoreCase("string"))
				{
					if (secondChild.getFirstChild() != null)
						result.put(keyName, secondChild.getFirstChild().getNodeValue());

					else
						result.put(keyName, "");
				}

				//creates another hashmap and sets it as a value
				else if (secondName.equalsIgnoreCase("object"))
				{
					//waarom werkt dit niet:
					//if (secondChild.getAttributes().getNamedItem("class")!=null && (secondChild.getAttributes().getNamedItem("class").toString()).equals("java.awt.Color"))
					//	if (secondChild.getAttributes().getNamedItem("class")!=null && secondChild.toString().indexOf('\n')>-1 && "<object class=\"java.awt.Color\">".equals(secondChild.toString().substring(0,secondChild.toString().indexOf('\n')).trim()))
					Element secondElement = (Element) secondChild;
					String className = secondElement.getAttribute("class");
					if(className == null) {
						logger.severe(secondElement + " without class");
						//className = "java.lang.Object";
						String ref = secondElement.getAttribute("idref");
						logger.info("insert reference " + ref);
						secondChild = secondElement = refs.get(ref);
						className = secondElement.getAttribute("class");
					} else {
						String id = secondElement.getAttribute("id");
						if(id != null) refs.put(id, secondElement);
					}
					if ("java.awt.Color".equals(className))
					{
						ArrayList<Node> childs = getElementList(secondChild.getChildNodes());
						int red = Integer.parseInt(childs.get(0).getFirstChild().getNodeValue());
						int green = Integer.parseInt(childs.get(1).getFirstChild().getNodeValue());
						int blue = Integer.parseInt(childs.get(2).getFirstChild().getNodeValue());
						//CssColor color = CssColor.make(red, green, blue);
						//result.put(firstChild.getFirstChild().getNodeValue(), color);
						result.put(keyName + "_red", red);
						result.put(keyName + "_green", green);
						result.put(keyName + "_blue", blue);
					}
					//waarom werkt dit niet:
					//else if (secondChild.getAttributes().getNamedItem("class")!=null && (secondChild.getAttributes().getNamedItem("class").toString()).equals("java.awt.Font"))
					//	else if (secondChild.getAttributes().getNamedItem("class")!=null && secondChild.toString().indexOf('\n')>-1 && "<object class=\"java.awt.Font\">".equals(secondChild.toString().substring(0,secondChild.toString().indexOf('\n')).trim()))
					else if ("java.awt.Font".equals(className))
					{
						ArrayList<Node> childs = getElementList(secondChild.getChildNodes());
						String fontName = childs.get(0).getFirstChild().getNodeValue();
						int fontStyle = Integer.parseInt(childs.get(1).getFirstChild().getNodeValue());
						int fontSize = Integer.parseInt(childs.get(2).getFirstChild().getNodeValue());
						//Font font = new Font("SansSerif", fontStyle, fontSize);
						result.put(keyName + "_size", fontSize);
						result.put(keyName + "_style", fontStyle);
					}
					else if ("fi.beans.dwomaccess.ByteArray".equals(className))
					{
						ArrayList<Node> childs = getElementList(secondChild.getChildNodes());
						Node node2 = childs.get(0);
						if (!node2.hasChildNodes())
							result.put(keyName, "");
						else
						{
							final String nodeValue = node2.getFirstChild().getNodeValue();
							result.put(keyName, nodeValue);
						}
					}
					else if ("java.net.URI".equals(className))
					{
						ArrayList<Node> childs = getElementList(secondChild.getChildNodes());
						final String nodeValue = childs.get(0).getFirstChild().getNodeValue();
						result.put(keyName, new String(nodeValue)); // TODO een of ander marker...
					}
					else if ("java.util.Vector".equals(className))
					{
						ArrayList<Node> childs = getElementList(secondChild.getChildNodes());
						result.put(keyName, convertNodeToList(childs));
					}
					else
					{
						logger.log(Level.INFO,"className = " + String.valueOf(className));
						result.put(keyName, convertNodeToHashMap(secondChild)); // FIXME controle op java.util.Hashtable
					}
				}
				//sets an int as value
				else if (secondName.equalsIgnoreCase("int"))
				{
					if (secondChild.getFirstChild() != null)
						result.put(keyName, Integer.parseInt(secondChild.getFirstChild().getNodeValue()));

					else
						result.put(keyName, 0);
				}
				//sets an double as value
				else if (secondName.equalsIgnoreCase("double"))
				{
					if (secondChild.getFirstChild() != null)
						result.put(keyName, Double.parseDouble(secondChild.getFirstChild().getNodeValue()));

					else
						result.put(keyName, 0);
				}

				//sets a boolean as value
				else if (secondName.equalsIgnoreCase("boolean"))
				{
					if (secondChild.getFirstChild() != null)
					{
						if (secondChild.getFirstChild().getNodeValue().equalsIgnoreCase("true"))
							result.put(keyName, true);
						else
							result.put(keyName, false);
					}
					else
						result.put(keyName, true);

				}
				//sets an arraylist as value
				else if (secondName.equalsIgnoreCase("array"))
					result.put(keyName, convertNodeToArray(secondChild));
			}

		}
} catch( Exception e) {
		logger.log(Level.SEVERE, e.toString(), e);
}
		return result;
	}

	private Object convertNodeToList(ArrayList<Node> childs) {
		ArrayList<Object> result = new ArrayList<Object>();
		for (Iterator<Node> iterator = childs.iterator(); iterator.hasNext();) {
			Node object = iterator.next();
			object = object.getChildNodes().item(1); // skip whitespace.
			result.add(convertNodeToObject(object));
		}
		return result;
	}

	private Object convertNodeToObject(Node object) {
		String name = object.getNodeName();
		if("array".equals(name))
			return convertNodeToArray(object);
		return object;
	}

	public ArrayList<Node> getElementList(NodeList list)
	{
		ArrayList<Node> elements = new ArrayList<Node>();
		final int length = list.getLength();
		for (int i = 0; i < length; i++)
		{
			try
			{
				Node currentNode = list.item(i);
				if (currentNode == null)
				{
					logger.severe ("getElementList " + i + " is null");
				}
				else if (currentNode.getNodeName() == null)
				{
					logger.info("node " + i + " "  + currentNode + " null name");
				}
				else if (!currentNode.getNodeName().equalsIgnoreCase("#text"))
				{
					elements.add(currentNode);
				}
			}
			catch (RuntimeException re)
			{
				logger.log(Level.SEVERE, "getElementList", re);
			}
		}

		return elements;
	}

	public ArrayList<Object> convertNodeToArray(Node node)
	{
		ArrayList<Node> children = new ArrayList<Node>();
		children = getElementList(node.getChildNodes());
		NamedNodeMap attributes = node.getAttributes();
		int len = Integer.parseInt(attributes.getNamedItem("length").getNodeValue());
		Object defaultValue = null;
		String type = attributes.getNamedItem("class").getNodeValue();

		if ("int".equals(type))
			defaultValue = new Integer(0);
		if ("double".equals(type))
			defaultValue = new Double(0.0);
		if ("boolean".equals(type))
			defaultValue = Boolean.FALSE;
		if ("short".equals(type))
			defaultValue = Short.valueOf((short) 0);

		ArrayList<Object> result = new ArrayList<Object>(len);
		for (int i = 0; i < len; i++)
		{
			result.add(defaultValue);
		}
		for (int i = 0; i < children.size(); i++)
		{
			Node currentNode = children.get(i);
			int index = Integer.parseInt(currentNode.getAttributes().getNamedItem("index").getNodeValue());
			ArrayList<Node> elements = getElementList(currentNode.getChildNodes());
			Node child = elements.get(0);
			if (child.getNodeName().equalsIgnoreCase("object"))
			{
				result.set(index, convertNodeToHashMap(child));
			} else {
				Node firstChild = child.getFirstChild();
				if (child.getNodeName().equalsIgnoreCase("string"))
				{   if(firstChild != null)
						result.set(index, firstChild.getNodeValue());
					else
						result.set(index, "");
				}
				else if (child.getNodeName().equalsIgnoreCase("int"))
				{
					result.set(index, Integer.parseInt(firstChild.getNodeValue()));
				}
				else if (child.getNodeName().equalsIgnoreCase("double"))
				{
					result.set(index, Double.parseDouble(firstChild.getNodeValue()));
				}
				else if (child.getNodeName().equalsIgnoreCase("boolean"))
				{
					if (firstChild.getNodeValue().equalsIgnoreCase("true"))
					{
						result.set(index, true);
					}
					else
					{
						result.set(index, false);
					}
				}
				else if (child.getNodeName().equalsIgnoreCase("array"))
				{
					result.set(index, convertNodeToArray(child));
				} else if (child.getNodeName().equalsIgnoreCase("short")) 
				{
					result.set(index,  Short.parseShort(firstChild.getNodeValue()));
				}
			}

		}

		return result;
	}

}
