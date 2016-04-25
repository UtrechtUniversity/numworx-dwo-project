package fi.dwo.dwojapplet.form;

import fi.dwo.commons.exceptions.CourseException;
import fi.dwo.commons.exceptions.DwoXmlRpcException;
import fi.dwo.commons.exceptions.PersistenceException;
import fi.dwo.dwojapplet.domain.Sco;
import fi.dwo.dwojapplet.persistence.PersistenceFacade;
import fi.dwo.dwojapplet.persistence.StoreCreator;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.xmlrpc.applet.XmlRpcException;
import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Save en restore van courses en single sco's.
 *
 * @author Wim
 * @version $Rev$
 */
class ManifestFile {

    private static final String SCO_ITEMS = "sco";
    private static final String LOM = "http://www.imsglobal.org/xsd/imsmd_rootv1p2p4";
    private static final String DWO = "http://www.freudenthal.nl/dwo";
    private static final String ADLCP = "http://www.adlnet.org/xsd/adlcp_rootv1p2";
    private static final String IMSCP = "http://www.imsglobal.org/xsd/imscp_rootv1p1p2";

    private static String APPLET_ID = "appletID";
    private static String TITLE = "title";
    private static String COURSE_TITLE = "name";
    private static String SCO_TITLE = "sconame";
    private static String DESCRIPTION = "description";
    private static String LAUNCHDATA = "launchdata";
    private static String SEQUENCE_NR = "sequencenr";

//    private DbAccessIF dbAccess;
    private Document document;

    public ManifestFile() {
    }

    Hashtable configMap = new Hashtable();

    public void createIMSManifest(int course, int scoid, OutputStream out) throws ParserConfigurationException, TransformerException, SQLException, IOException, XmlRpcException, PersistenceException {
        Hashtable record = PersistenceFacade.instance().getRecord("tblCourse", "courseID", course);
        Hashtable restriction;
        restriction = new Hashtable();
        restriction.put("courseID", record.get("courseID"));
        if (scoid != -1) {
            // insert scoid in restriction.
        }
        Vector scos = PersistenceFacade.instance().getScos(restriction, SEQUENCE_NR);

        DocumentBuilderFactory factory;
        factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);

        DocumentBuilder builder = factory.newDocumentBuilder();
        document = builder.newDocument();
        Element toplevel = document.createElementNS(IMSCP, "manifest");
        document.appendChild(toplevel);
// Metadata		
        Element metadata = document.createElementNS(IMSCP, "metadata");
        Element schema = document.createElementNS(IMSCP, "schema");
        schema.appendChild(document.createTextNode("ADL SCORM"));
        metadata.appendChild(schema);
        Element schemaVersion = document.createElementNS(IMSCP, "schemaversion");
        schemaVersion.appendChild(document.createTextNode("1.2"));
        metadata.appendChild(schemaVersion);
        toplevel.appendChild(metadata);
// Organizations
        appendNL(toplevel);
        Element organizations = document.createElementNS(IMSCP, "organizations");
        Attr deforg = document.createAttribute("default");
        deforg.setValue("ORG1");
        organizations.setAttributeNode(deforg);
        Element organization = document.createElementNS(IMSCP, "organization");
        organization.setAttribute("identifier", deforg.getValue());
        organization.setAttribute("structure", "hierarchical");
        appendNL(organization);
// Course:
        Element title = document.createElementNS(IMSCP, TITLE);
        title.appendChild(document.createTextNode((String) record.get(COURSE_TITLE)));
        organization.appendChild(title);
        appendNL(organization);
        // onderdeel van metadata....
        Element description = document.createElementNS(LOM, "imsmd:description");
        description.appendChild(document.createTextNode((String) record.get(DESCRIPTION)));
        organization.appendChild(description);
        appendNL(organization);
// items
        Iterator iter;
        iter = scos.iterator();
        while (iter.hasNext()) {
            Hashtable sco = (Hashtable) iter.next();
            Element item = document.createElementNS(IMSCP, "item");
            appendNL(item);
// sco.title
            title = document.createElementNS(IMSCP, TITLE);
            title.appendChild(document.createTextNode(sco.get("sconame").toString()));
            item.appendChild(title);
            appendNL(item);
// sco.description
            description = document.createElementNS(LOM, "imsmd:description");
            description.appendChild(document.createTextNode(sco.get(DESCRIPTION).toString()));
            item.appendChild(description);
            appendNL(item);
// sco.appletID
            Attr appletID = document.createAttributeNS(DWO, "dwo:appletID");
            appletID.setValue(sco.get("appletID").toString());
            item.setAttributeNodeNS(appletID);
// sco.launchdata
            Element launchdata = document.createElementNS(ADLCP, "adlcp:datafromlms");
            launchdata.appendChild(document.createTextNode(sco.get(LAUNCHDATA).toString()));
            item.appendChild(launchdata);
            appendNL(item);

            organization.appendChild(item);
            appendNL(organization);
        }
        organizations.appendChild(organization);
        toplevel.appendChild(organizations);

        DOMSource source = new DOMSource(document);
        TransformerFactory transformFactory;
        transformFactory = TransformerFactory.newInstance();
        Transformer transform = transformFactory.newTransformer();
        StreamResult result = new StreamResult(out);
        transform.transform(source, result);
    }

    /**
     * @param toplevel
     * @throws DOMException
     */
    private void appendNL(Element toplevel) throws DOMException {
        toplevel.appendChild(document.createTextNode("\n"));
    }

    static final String FILENAME = "imsmanifest.xml";

    Hashtable inputIMSManifest(InputStream input) throws ParserConfigurationException, SAXException, IOException {
        Hashtable result = new Hashtable();
        Vector items = new Vector();
        result.put(SCO_ITEMS, items);
        DocumentBuilderFactory factory;
        factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        DocumentBuilder parser = factory.newDocumentBuilder();
        document = parser.parse(input);
        NodeList list = document.getElementsByTagNameNS(IMSCP, "organization");
        Element organization = (Element) list.item(0);
        list = organization.getChildNodes();
        int len = list.getLength();
        int itemNr = 1;
        for (int i = 0; i < len; i++) {
            Node node = list.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) node;
                Node firstChild = element.getFirstChild();
                if (element.getLocalName().equals(TITLE)) {
                    result.put(COURSE_TITLE, getNodeValue(firstChild));
                } else if (element.getLocalName().equals(DESCRIPTION)) {
                    result.put(DESCRIPTION, getNodeValue(firstChild));
                } else if (element.getLocalName().equals("item")) {
                    Hashtable r = insertItem(element, itemNr);
                    items.addElement(r);
                    itemNr++;
                }
            }
        }
        return result;
    }

    /**
     * @param firstChild
     * @return nodeValue of ""
     * @throws DOMException
     */
    private static String getNodeValue(Node firstChild) throws DOMException {
        if (firstChild == null) {
            return "";
        }
        return firstChild.getNodeValue();
    }



    
// nieuw
    private Hashtable insertItem(Element element, int itemNr) {
		Hashtable result = new Hashtable();
		NodeList list = element.getChildNodes();
		Attr appletID = element.getAttributeNodeNS(DWO, APPLET_ID);
		result.put(APPLET_ID, new Integer(appletID.getValue()));
		result.put(SEQUENCE_NR, new Integer(itemNr));
		int len = list.getLength();
		for(int i = 0; i < len; i++)
		{
			Node node = list.item(i);
			if( node.getNodeType()== Node.ELEMENT_NODE) {
				element = (Element) node;
				Node firstChild = element.getFirstChild();
				if(element.getLocalName().equals(TITLE))
				{
					result.put(SCO_TITLE, getNodeValue(firstChild));
				} else
				if(element.getLocalName().equals(DESCRIPTION))
				{
						result.put(DESCRIPTION, getNodeValue(firstChild));
				} else
				if(element.getLocalName().equals("datafromlms"))
				{
						result.put(LAUNCHDATA, getNodeValue(firstChild));
				} 
			}
		}
		return result;
	}
	
	int addCourse(Hashtable course, int dwoProfile, int schoolID, int parent) throws DwoXmlRpcException, SQLException, IOException, XmlRpcException, PersistenceException, CourseException
	{
		String name;
		String description;
		name = (String) course.get(COURSE_TITLE);
		description = (String) course.get(DESCRIPTION);
		int courseID = PersistenceFacade.instance().addCourse(schoolID, notnull(name), notnull(description), dwoProfile, parent, false);
		appendCourse(courseID, 0, course);
		return courseID;
	}
	
	void appendCourse(int courseID, int offset, Hashtable course) throws DwoXmlRpcException, IOException, XmlRpcException, SQLException, PersistenceException
	{
		Vector items = (Vector) course.get(SCO_ITEMS);
		Iterator i = items.iterator();
		while (i.hasNext()) {
			Hashtable sco = (Hashtable) i.next();
			String name = (String) sco.get(SCO_TITLE);
			int appletID = ((Number)sco.get(APPLET_ID)).intValue();
			int sequencenr = ((Number)sco.get(SEQUENCE_NR)).intValue();
			String description = (String) sco.get(DESCRIPTION);
			String launchdata = (String) sco.get(LAUNCHDATA);
			int sconr = PersistenceFacade.instance().addSco(courseID, notnull(name), notnull(description), appletID, notnull(launchdata), sequencenr+offset);
	
			Sco newsco = (Sco) PersistenceFacade.instance().get(sconr, Sco.class);
        	if(newsco.hasFeature(Sco.JSON_OUT))
        	{	byte[] launchdataBytes = newsco.getLaunchdataBytes();
        		System.out.println("JSON launchdata " + newsco.getID() + " " + launchdataBytes.length + " bytes");
				StoreCreator.instance().changeSco(newsco.getID(), newsco.getScoName(), newsco.getDescription(), 
				        true, launchdataBytes, newsco.getShowScore());
        	}
	
		
		
		}
	}
	
	private static String notnull(String s)
	{
		if(s==null)
			return "";
		return s;
	}
	

}
