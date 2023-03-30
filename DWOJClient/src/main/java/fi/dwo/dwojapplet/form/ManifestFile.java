package fi.dwo.dwojapplet.form;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.Arrays;
import java.util.Base64;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.osgi.util.promise.Promise;
import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import fi.dwo.commons.exceptions.CourseException;
import fi.dwo.commons.exceptions.PersistenceException;
import fi.dwo.commons.persistence.MySQLPersistenceId;
import fi.dwo.commons.persistence.entities.PersistentApplet;
import fi.dwo.commons.persistence.entities.PersistentCourse;
import fi.dwo.commons.persistence.entities.PersistentDwoProfile;
import fi.dwo.commons.persistence.entities.PersistentScoContext;
import fi.dwo.dwojapplet.domain.Course;
import fi.dwo.dwojapplet.domain.Sco;
import fi.dwo.dwojapplet.gui.GuiCreator;
import fi.dwo.dwojapplet.persistence.PersistenceFacade;
import hplb.misc.ByteArray;
import nl.uu.fi.dwo.lms.jclient.lib.rest.managers.AbstractScoContextManager;
import nl.uu.fi.dwo.lms.jclient.lib.rest.managers.PublicScoContextManager;
import nl.uu.fi.dwo.rest.dom.entities.DomCourseFull;
import nl.uu.fi.dwo.rest.dom.entities.DomDwoProfile;
import nl.uu.fi.dwo.rest.dom.entities.DomScoContext;
import nl.uu.fi.dwo.rest.dom.entities.DomScoContextFull;
import nl.uu.fi.dwo.rest.dom.entities.DomScoData;
import nl.uu.fi.dwo.rest.dom.entities.util.ScoType;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
import nl.uu.fi.dwo.rest.persistence.PersistenceId;

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
    private static String THUMBNAIL = "thumbnail";

//    private DbAccessIF dbAccess;
    private Document document;

    public ManifestFile() {
    }

    Hashtable configMap = new Hashtable();

//    public void createIMSManifest(int course, int scoid, OutputStream out) throws ParserConfigurationException, TransformerException, SQLException, IOException, XmlRpcException, PersistenceException {
//        Course record = PersistenceFacade.instance().get( course, Course.class);
//        createIMSManifest(record, out);
//    }
    public void createIMSManifest(Course record, OutputStream out) throws ParserConfigurationException, TransformerException {
        
        
        //        Hashtable restriction;
//        restriction = new Hashtable();
//        restriction.put("courseID", record.getID());
//        if (scoid != -1) {
//            // insert scoid in restriction.
//        }
        record.loadScos();
        List<Sco> scos = Arrays.asList(record.getScoList());

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
        title.appendChild(document.createTextNode((String) record.getName()));
        organization.appendChild(title);
        appendNL(organization);
        // onderdeel van metadata....
        Element description = document.createElementNS(LOM, "imsmd:description");
        description.appendChild(document.createTextNode((String) record.getDescription()));
        organization.appendChild(description);
        appendNL(organization);
// Metadata of course
        metadata = document.createElementNS(IMSCP, "metadata");
        schema = document.createElementNS(IMSCP, "schema");
        schema.appendChild(document.createTextNode("DWO " + THUMBNAIL));
        schemaVersion = document.createElementNS(IMSCP, "schemaversion");
        schemaVersion.appendChild(document.createTextNode("1.0"));
        metadata.appendChild(schema);
        appendNL(metadata);
        metadata.appendChild(schemaVersion);
        appendNL(metadata);
        Element thumbnail = document.createElementNS(DWO, THUMBNAIL);
        metadata.appendChild(thumbnail);
        appendNL(metadata);

        byte[] data = record.getImageData();
        if (data == null && record.getImageUrl() != null) {
          try {
            URL u = new URL(record.getImageUrl());
            data = ByteArray.getContent(u);           
          } catch(Exception e) {            
          }
        }
        if (data != null) {
        String base64 = Base64.getEncoder().encodeToString(data);
        thumbnail.appendChild(document.createTextNode(base64));
        }
        organization.appendChild(metadata);
        appendNL(organization);
// items
        Iterator<Sco> iter;
        iter = scos.iterator();
        while (iter.hasNext()) {
            Sco sco = iter.next();
            Element item = document.createElementNS(IMSCP, "item");
            appendNL(item);
// sco.title
            title = document.createElementNS(IMSCP, TITLE);
            title.appendChild(document.createTextNode(sco.getScoName()));
            item.appendChild(title);
            appendNL(item);
// sco.description
            description = document.createElementNS(LOM, "imsmd:description");
            description.appendChild(document.createTextNode(sco.getDescription()));
            item.appendChild(description);
            appendNL(item);
// sco.appletID
            Attr appletID = document.createAttributeNS(DWO, "dwo:appletID");
            appletID.setValue(String.valueOf(sco.getAppletID()));
            item.setAttributeNodeNS(appletID);
// sco.launchdata
            Element launchdata = document.createElementNS(ADLCP, "adlcp:datafromlms");
            launchdata.appendChild(document.createTextNode(sco.getLaunchdataString()));
            item.appendChild(launchdata);
            appendNL(item);
// Metadata of sco
            metadata = document.createElementNS(IMSCP, "metadata");
            schema = document.createElementNS(IMSCP, "schema");
            schema.appendChild(document.createTextNode("DWO " + THUMBNAIL));
            schemaVersion = document.createElementNS(IMSCP, "schemaversion");
            schemaVersion.appendChild(document.createTextNode("1.0"));
            metadata.appendChild(schema);
            appendNL(metadata);
            metadata.appendChild(schemaVersion);
            appendNL(metadata);
            thumbnail = document.createElementNS(DWO, THUMBNAIL);
            metadata.appendChild(thumbnail);
            appendNL(metadata);
            data = sco.getImageData();
            if (data == null) {
              PersistenceId id = PersistentScoContext.buildPersistenceId(Long.valueOf(sco.getScoID()));
              DomScoContext domScoId = new DomScoContext();
              domScoId.setId(id);
              Promise<DomScoContext> p = PublicScoContextManager.getAsync(domScoId, fi.dwo.dwojapplet.domain.DWO.getDwoProfile(), null);
              Promise<byte[]> map = p.map(s -> {        
                try {
                  return ByteArray.getContent(new URL(s.getImage()));
                } catch (Exception e) {
                  return null;
                }
              });
              try {
                data = map.getValue();
                sco.setImageData(data);
              } catch (Exception e) {
              }
            }
            if (data != null) {
              String base64 = Base64.getEncoder().encodeToString(data);
              thumbnail.appendChild(document.createTextNode(base64));
            }
            item.appendChild(metadata);
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

    @SuppressWarnings("rawtypes")
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
                } else if (element.getLocalName().equals("metadata")) {
                  Node thumbnail = element.getElementsByTagNameNS(DWO, THUMBNAIL).item(0);
                  byte[] data = Base64.getDecoder().decode(thumbnail.getTextContent());
                  if (data.length > 0 ) 
                    result.put(THUMBNAIL, data);
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
				else if (element.getLocalName().equals("metadata")) {
                  Node thumbnail = element.getElementsByTagNameNS(DWO, THUMBNAIL).item(0);
                  byte[] data = Base64.getDecoder().decode(thumbnail.getTextContent());
                  if (data.length > 0 ) 
                    result.put(THUMBNAIL, data);
                }
			}
		}
		return result;
	}
	
	int addCourse(Hashtable course, int dwoProfile, int schoolID, int parent, int n) throws PersistenceException, CourseException, Dwo2Exception
	{
		String name;
		String description;
		name = (String) course.get(COURSE_TITLE);
		description = (String) course.get(DESCRIPTION);
		byte[] thumbnail = (byte[]) course.get(THUMBNAIL);
		int courseID = addCourseRest(schoolID, notnull(name), notnull(description), dwoProfile, parent, false, n, thumbnail);
		DomDwoProfile p = new DomDwoProfile();
		p.setId(PersistentDwoProfile.buildPersistenceId((long)dwoProfile));
		appendCourse(courseID, 0, course,p);
		return courseID;
	}

	int addCourseRest(long schoolID, String name, String description,long dwoProfile, long parent, boolean isMap, long offset, byte[] image) throws Dwo2Exception {
    	PersistentCourse pc = new PersistentCourse();
// if Course extends persistentCourse
    		pc.setName(name);
    		pc.setWithChildren(Boolean.valueOf(isMap));
    		pc.setDescription(description);    		
    		pc.setDwoProfileID(dwoProfile);
    		pc.setImageData(image);
// defaults:
    		
// special cases...
    		pc.setParentID(parent); // NPE?
    		pc.setSchoolID(schoolID);
    		pc.setSequencenr(offset);
    		
    		DomCourseFull edit = pc.buildDomCourseFull();
		edit = GuiCreator.instance().getCourseManager().add(edit);
// legacy
		int pid = MySQLPersistenceId.getNativeId(edit).intValue();
		return pid;

	}
	
	
	
	
	
	void appendCourse(int courseID, int offset, Hashtable course, DomDwoProfile profile) throws PersistenceException, Dwo2Exception
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
			byte[] thumbnail = (byte[]) sco.get(THUMBNAIL);
			//GuiCreator.instance().getDWO().addScoWithExceptions(course, appletConfig, name, description, showScore, imageData)
			
			
			Sco addsco = new Sco();
    			DomScoContextFull scoContext = new DomScoContextFull();
    			DomScoData scoData = new DomScoData();
    			scoContext.setImageData(thumbnail);
    			scoContext.setScoName(notnull(name));
    			scoContext.setDescription(notnull(description));
    			scoContext.setShowScore(Boolean.TRUE);
    			scoContext.setAppletId(PersistentApplet.buildPersistenceId((long)appletID));
    			scoContext.setCourseId(PersistentCourse.buildPersistenceId((long)courseID));
    			scoContext.setSequencenr((long)sequencenr+offset);
    			scoContext.setUrnId(null);
// scodata
    			addsco.setAppletID(appletID);
    			addsco.setLaunchdataString(launchdata);
    			addsco.getApplet();
    			Map<?, ?> m = addsco.getLaunchdata();
    			Object mode = m.get("mode");
    			int value = mode == null ? 0 : Integer.parseInt(mode.toString());
    			scoContext.setScoType(ScoType.values()[value]);
    			scoData.setLaunchdata(launchdata);
    			if (addsco.hasFeature(Sco.JSON_OUT))
    				scoData.setLaunchdatabytes(addsco.getLaunchdataBytes());
// FIXME breaks CDI     			
    	        AbstractScoContextManager manager = GuiCreator.instance().getScoContextManager();
    	        scoContext = manager.add(scoContext, scoData, profile);
    	            
// legacy?
    			//Sco newsco =  PersistenceFacade.instance().toSco(scoContext); 

   
			
			
			
			
//			int sconr = PersistenceFacade.instance().addSco(courseID, notnull(name), notnull(description), appletID, notnull(launchdata), sequencenr+offset);
//	
//			Sco newsco = (Sco) PersistenceFacade.instance().get(sconr, Sco.class);
//        	if(newsco.hasFeature(Sco.JSON_OUT))
//        	{	byte[] launchdataBytes = newsco.getLaunchdataBytes();
//        		System.out.println("JSON launchdata " + newsco.getID() + " " + launchdataBytes.length + " bytes");
//				StoreCreator.instance().changeSco(newsco.getID(), newsco.getScoName(), newsco.getDescription(), 
//				        true, launchdataBytes, newsco.getShowScore());
//        	}
	
		
		
		}
	}
	
	private static String notnull(String s)
	{
		if(s==null)
			return "";
		return s;
	}
	

}
