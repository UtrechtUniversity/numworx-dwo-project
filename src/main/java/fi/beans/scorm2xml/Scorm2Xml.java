package fi.beans.scorm2xml;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import fi.beans.scorm.ScormAdapter;

/**
 * Class die het cmi datamodel converteert naar een xml-file.
 * De standaard die wordt gevolgd is: 
 * <em>http://ltsc.ieee.org/xsd/1484_11_3 ieee_1484.11.3-2005.xsd</em>
 * @author Wim van Velthoven
 *
 */
public class Scorm2Xml extends ScormAdapter {

	public static final String NAMESPACE = "http://ltsc.ieee.org/xsd/1484_11_3";
	public static final String COCD = "cocd";
	public static final String EMPTY_DOC = "<?xml version='1.0'?><cocd xmlns='http://ltsc.ieee.org/xsd/1484_11_3'></cocd>";
	protected Document doc;
	protected Element root;
	protected DocumentBuilder builder;
	
	public Scorm2Xml() {
		super(true);
		createBuilder();
		createEmptyDocument();
	}

	public Scorm2Xml(Reader in) {
		super(true);
		createBuilder();
		createDocument(new InputSource(in));
	}

	public Scorm2Xml(InputStream in)
	{
		super(true);
		createBuilder();
		createDocument(new InputSource(in));
	}
	
	public Scorm2Xml(String in)
	{
		this(new StringReader(in));	
	}
	
	
	protected void createDocument(InputSource is) {
		try {
			doc = builder.parse(is);
			root = doc.getDocumentElement();
		} catch (Exception e) {
			createEmptyDocument();
		} 
	}
	
	protected void createEmptyDocument() {
		createDocument(new InputSource(new StringReader(EMPTY_DOC)));
	}

	protected void createBuilder() {
		DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
		documentBuilderFactory.setIgnoringElementContentWhitespace(true);
		documentBuilderFactory.setNamespaceAware(true);
		try {
			builder = documentBuilderFactory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			// should not happen
			e.printStackTrace();
		}
	}

	public void write(StreamResult out) throws IOException
	{
		 // Use a Transformer for output
		  TransformerFactory tFactory = TransformerFactory.newInstance();
		  Transformer transformer = null;
		try {
			transformer = tFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.ENCODING, "US-ASCII");
		} catch (TransformerConfigurationException e) {
			// should not happen
			e.printStackTrace();
		}
		DOMSource source = new DOMSource(doc);
		try {
			transformer.transform(source, out);
		} catch (TransformerException e) {
			throw (IOException)( new IOException(e.getMessage()).initCause(e) ) ;
		} 
	}

	public String toString()
	{
		StringWriter out = new StringWriter();
		try {
			write(new StreamResult(out));
		} catch (IOException e) {
			return e.toString();
		}
		return out.toString();
	}
	
	public void write(OutputStream out) throws IOException
	{
		write( new StreamResult(out));
	}

	public String GetValue(String key) {
		if(!key.startsWith("cmi."))
			return "";
		key = key.substring(4);
		
		String lastsubkey = COCD;
		Element r = root;
		int i = 0;
		while( (i = key.indexOf('.'))>=0)
		{
			String subkey = key.substring(0,i);

			Node node = getElementByTagNameNS(r, NAMESPACE, subkey);
			if ( node == null) 
			{
				String s  = singular(lastsubkey);
				node = getElementByTagNameNS(r, NAMESPACE, s);
				if( node != null )
				{
					int n = Integer.parseInt(subkey);
					r = (Element) r.getChildNodes().item(n);
					if(null == r)
						return "";
				} else
				{	if(key.endsWith("_count"))
							return "0";
					return "";
				}
			} else 
			{
				r = (Element) node;
			}			
			key = key.substring(i+1);
			lastsubkey = subkey;
		}
		if("_count".equals(key))
			return String.valueOf(r.getChildNodes().getLength());
		Node node = getElementByTagNameNS(r, NAMESPACE, key);
		if (  node == null) 
			return "";
		return node.getFirstChild().getNodeValue();
	}

	/**
	 * Legacy method. Use LMSGetValue.
	 * @param key
	 * @return result
	 * @deprecated use LMSGetValue
	 */
	public String getValue(String key)
	{
		return LMSGetValue(key);
	}

	private String singular(String key) {
		return key.substring(0, key.length()-1);
	}

	/**
	 * Legacy method. Use LMSSetValue
	 * @param key
	 * @param value
	 * @deprecated use LMSSetValue
	 */
	public void setValue(String key, String value) {
		LMSSetValue(key, value);
	}

	/**
	 * Set value uit het cmi data model.
	 * @param key cmi.xxx...
	 * @param value de waarde 
	 */
	public String SetValue(String key, String value) {
		if(!key.startsWith("cmi."))
			return "";
		key = key.substring(4);
		String lastsubkey = COCD;
		Element r = root;
		int i = 0;
		while( (i = key.indexOf('.'))>=0)
		{
			String subkey = key.substring(0,i);
			Node node = getElementByTagNameNS(r, NAMESPACE, subkey);
			if ( node == null ) 
			{
				try {
					int j = Integer.parseInt(subkey);
					subkey = singular(lastsubkey);
					node = r.getChildNodes().item(j);
					if(node != null)
					{
						r = (Element) node;
						key = key.substring(i+1);
						lastsubkey = null;
						continue;	
					}
				} catch(NumberFormatException e)
				{
				} 
				Element newchild = doc.createElementNS(NAMESPACE, subkey);
				r.appendChild(newchild);
				r = newchild;
			} else {
				r = (Element) node;
			}
			key = key.substring(i+1);
			lastsubkey = subkey;
		}
		Node node = getElementByTagNameNS(r, NAMESPACE, key);
		if ( node == null) 
		{
			node = doc.createElementNS(NAMESPACE, key);
			node.appendChild(doc.createTextNode(value));
			r.appendChild(node);
		}
		if("".equals(value))
		{	r.removeChild(node);
			while(r != root && r.getFirstChild() == null)
			{
				node = r;
				r = (Element) r.getParentNode();
				r.removeChild(node);
			}
		}
		else
			node.getFirstChild().setNodeValue(value);
		return "";
	}

	private Node getElementByTagNameNS(Element r, String namespace,
			String subkey) {
		int i = 0;
		Node result;
		final NodeList nl = r.getElementsByTagNameNS(namespace, subkey);
		do {
			result = nl.item(i++);
		} while(result != null && result.getParentNode() != r);
		return result;
	}

	private static final String[] EMPTY = new String[0]; 

	private TreeSet getSet(Node element)
	{
		TreeSet result = new TreeSet();
		Node node = element.getFirstChild();
		while(node != null)
		{
			String name = node.getLocalName();
			if(name != null)
				result.add(name);
			node = node.getNextSibling();
		}
		return result;
	}
	
	
	
	public String[] getChildren(String key)
	{
		Element r = root;
		if("cmi".equals(key))
		{
			Set set = getSet(r);
			String[] result = new String[set.size()];
			set.toArray(result);			
			return convert(result);
		}
		key = map(key);
		String lastsubkey = COCD;
		int i = 0;
		while( (i = key.indexOf('.'))>=0)
		{
			String subkey = key.substring(0,i);

			Node node = getElementByTagNameNS(r, NAMESPACE, subkey);
			if ( node == null) 
			{
				String s  = singular(lastsubkey);
				node = getElementByTagNameNS(r, NAMESPACE, s);
				if( node != null )
				{
					int n = Integer.parseInt(subkey);
					r = (Element) r.getChildNodes().item(n);
					if(null == r)
						return EMPTY;
				} else
				{	
					return EMPTY;
				}
			} else 
			{
				r = (Element) node;
			}			
			key = key.substring(i+1);
			lastsubkey = subkey;
		}
		Node node = getElementByTagNameNS(r, NAMESPACE, key);
		if ( node == null) 
		{
			String s  = singular(lastsubkey);
			node = getElementByTagNameNS(r, NAMESPACE, s);
			if( node != null )
			{
				int n = Integer.parseInt(key);
				r = (Element) r.getChildNodes().item(n);
				if(null == r)
					return EMPTY;
			} else
			{	
				return EMPTY;
			}
		}
		Set set = getSet(node);
		if(set.size() == 1)
		{
			String only = set.iterator().next().toString();
			if("objective".equals(only) || "interaction".equals(only))
			{
				int size = node.getChildNodes().getLength();
				String[] result = new String[size];
				for (int j = 0; j < size; j++) {
					result[j] = Integer.toString(j);
				}
				return result;
			}
		}
		String[] result = new String[set.size()];
		set.toArray(result);			
		return convert(result);
	}

	// from camelCase to camel_case
	private String[] convert(String[] result) {
		for (int i = 0; i < result.length; i++) {
			String string = result[i];
			for(int j = 0; j < string.length(); j++)
			{
				char ch = string.charAt(j);
				if(Character.isUpperCase(ch))
				{
					string = string.substring(0, j) + "_" + Character.toLowerCase(ch) + 
							 string.substring(j+1);
				}
			}
			result[i] = string;
		}
		return result;
	}
	
	public Properties toProperties() {
		Properties result = new Properties();
		toProperties(result, "cmi");
		return result;
	}

	private void toProperties(Properties result, String key) {
		String children[] = getChildren(key);
		if(children.length == 0)
		{
			String value = getValue(key);
			if(value.length()>0)
				result.put(key, value);
		} else {
			for (int i = 0; i < children.length; i++) {
				String subkey = children[i];
				toProperties(result, key + '.' + subkey);
			}
		}
	}
	
}
