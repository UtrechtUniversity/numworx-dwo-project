package nl.numworx.edexml;

import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import nl.uu.fi.dwo.rest.dom.entities.*;

public class EdeXmlBuilder {

	private Document document;

	public EdeXmlBuilder() {
	}

	
	public void setSource(InputSource input) throws ParserConfigurationException, SAXException, IOException {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		
		DocumentBuilder db = dbf.newDocumentBuilder();
		
		setDocument(db.parse(input));
	}


	public Document getDocument() {
		return document;
	}

	public void setDocument(Document document) {
		this.document = document;
	}
	
	public Map<String,DomSchoolClassFull> parseGroepen() {
		Map<String,DomSchoolClassFull> result = new TreeMap<String,DomSchoolClassFull>();
		NodeList groepen = document.getDocumentElement().getChildNodes();
		int size = groepen.getLength();
		for(int i = 0; i < size; i++) {
			Node item = groepen.item(i);
			if(item instanceof Element && item.getNodeName().equals("groepen")) {
				Map<String, DomSchoolClassFull> r = parseGroepen((Element)item);
				result.putAll(r);
			}
		}
		return result;
		
	}

	private Map<String, DomSchoolClassFull> parseGroepen(Element groepen) {
		Map<String,DomSchoolClassFull> result = new TreeMap<String,DomSchoolClassFull>();
		NodeList nodes = groepen.getChildNodes();
		int size = nodes.getLength();
		for(int i = 0; i < size; i++ ) {
			Node item = nodes.item(i);
			if(item.getNodeType() == Node.ELEMENT_NODE) {
				String nodeName = item.getNodeName();
				if ("groep".equals(nodeName)||"samengestelde_groep".equals(nodeName))
				{
					String key = item.getAttributes().getNamedItem("key").getNodeValue();
					Element e = (Element) item;
					String naam = e.getElementsByTagName("naam").item(0).getTextContent();
					DomSchoolClassFull klas = new DomSchoolClassFull();
					klas.setSchoolClassName(naam);
					result.put(key, klas);
				}
				
			}
		}		
		return result;
	}


	public Map<String, DomUserFull> parseLeerlingen() {
		Map<String,DomUserFull> result = new TreeMap<String,DomUserFull>();
		NodeList groepen = document.getDocumentElement().getChildNodes();
		int size = groepen.getLength();
		for(int i = 0; i < size; i++) {
			Node item = groepen.item(i);
			if(item instanceof Element && item.getNodeName().equals("leerlingen")) {
				Map<String, DomUserFull> r = parseLeerlingen((Element)item);
				result.putAll(r);
			}
		}
		return result;
	}


	private Map<String, DomUserFull> parseLeerlingen(Element leerlingen) {
		Map<String,DomUserFull> result = new TreeMap<String,DomUserFull>();
		return parseUser(leerlingen, result, "leerling");
	}


	private Map<String, DomUserFull> parseUser(Element leerlingen,
			Map<String, DomUserFull> result, String type) {
		NodeList nodes = leerlingen.getChildNodes();
		int size = nodes.getLength();
		for(int i = 0; i < size; i++ ) {
			Node item = nodes.item(i);
			if(item.getNodeType() == Node.ELEMENT_NODE) {
				String nodeName = item.getNodeName();
				if (type.equals(nodeName))
				{
					String key = item.getAttributes().getNamedItem("key").getNodeValue();
					Element e = (Element) item;
					DomUserFull user = parseUser(e);
					result.put(key, user);
				}
			}
		}		
		return result;
	}


	private DomUserFull parseUser(Element item) {
		DomUserFull u = new DomUserFull();
		String roepnaam = getElement(item, "roepnaam");
		String achternaam = getElement(item, "achternaam");
		String tussenvoegsel = getElement(item, "tussenvoegsel");
		String emailadres = getElement(item, "emailadres");
		String gebruikersnaam = getElement(item, "gebruikersnaam");
		u.setInsertion(tussenvoegsel);
		u.setFamilyName(achternaam);
		u.setGivenName(roepnaam);
		u.setEmail(emailadres);
		u.setUserName(gebruikersnaam);
		return u;
	}


	private String getElement(Element item, String element) {
		NodeList tag = item.getElementsByTagName(element);
		if(tag.getLength() == 0) return "";
		return tag.item(0).getTextContent();
	}


	public Map<String, DomUserFull> parseLeerkrachten() {
		Map<String,DomUserFull> result = new TreeMap<String,DomUserFull>();
		NodeList groepen = document.getDocumentElement().getChildNodes();
		int size = groepen.getLength();
		for(int i = 0; i < size; i++) {
			Node item = groepen.item(i);
			if(item instanceof Element && item.getNodeName().equals("leerkrachten")) {
				Map<String, DomUserFull> r = parseLeerkrachten((Element)item);
				result.putAll(r);
			}
		}
		return result;
	}


	private Map<String, DomUserFull> parseLeerkrachten(Element item) {
		Map<String,DomUserFull> result = new TreeMap<String,DomUserFull>();
		return parseUser(item, result, "leerkracht");
	}
	
	
	
	
}
