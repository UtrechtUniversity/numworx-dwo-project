package nl.numworx.uploadwidget;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Map;

import javax.inject.Inject;
import javax.swing.Box;
import javax.swing.DefaultListModel;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.cbook.cbookif.AssessmentMode;
import org.cbook.cbookif.CBookEvent;
import org.cbook.cbookif.CBookEventHandler;
import org.cbook.cbookif.CBookEventListener;
import org.cbook.cbookif.CBookWidgetInstanceIF;
import org.cbook.cbookif.SuccessStatus;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import fi.beans.numworxlf.JButton;
import fi.beans.numworxlf.JFileChooser;
import fi.beans.numworxlf.JScrollPane;
import nl.numworx.uploadwidget.shared.AtomEntry;

public class Upload extends JPanel implements CBookWidgetInstanceIF, CBookEventListener, ActionListener {

	
	String server = "http://localhost:8888/feed.xml";
	
	JList<AtomEntry> items;
	DefaultListModel<AtomEntry> model;
	JButton up, down, delete;
	
	@Inject Upload() {
		super(new BorderLayout());
		model = new DefaultListModel<>();
		items = new JList<>(model);
		add(new JScrollPane(items), BorderLayout.CENTER);
		Box hb = Box.createHorizontalBox();
		up = new JButton("Upload"); hb.add(up); hb.add(Box.createHorizontalGlue());
		down = new JButton("Download"); hb.add(down);hb.add(Box.createHorizontalGlue());
		delete = new JButton("Delete"); hb.add(delete);
		
		up.addActionListener(this);
		down.addActionListener(this);
		delete.addActionListener(this);
		
		add(hb, BorderLayout.SOUTH);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	CBookEventHandler handler = new CBookEventHandler(this);
	
	@Override
	public void addCBookEventListener(CBookEventListener listener, String command) {
		handler.addCBookEventListener(listener, command);
	}

	@Override
	public JComponent asComponent() {
		return this;
	}

	@Override
	public CBookEventListener asEventListener() {
		return this;
	}

	@Override
	public void destroy() {

	}

	@Override
	public int getScore() {
		return 0;
	}

	@Override
	public Hashtable<String, ?> getState() {
		Hashtable<String, ?> state = new Hashtable<>();
		return state;
	}

	@Override
	public SuccessStatus getSuccessStatus() {
		return SuccessStatus.UNKNOWN;
	}

	@Override
	public void init() {
	}

	@Override
	public void removeCBookEventListener(CBookEventListener listener, String command) {
		handler.removeCBookEventListener(listener, command);
	}

	@Override
	public void reset() {
	}

	@Override
	public void setAssessmentMode(AssessmentMode mode) {
	}

	@Override
	public void setLaunchData(Map<String, ?> launchData, Map<String, Number> randomVars) {
	}

	@Override
	public void setState(Map<String, ?> state) {
	}

	
	public static void clean(Node node)
	{
	  NodeList childNodes = node.getChildNodes();

	  for (int n = childNodes.getLength() - 1; n >= 0; n--)
	  {
	     Node child = childNodes.item(n);
	     short nodeType = child.getNodeType();

	     if (nodeType == Node.ELEMENT_NODE)
	        clean(child);
	     else if (nodeType == Node.TEXT_NODE)
	     {
	        String trimmedNodeVal = child.getNodeValue().trim();
	        if (trimmedNodeVal.length() == 0)
	           node.removeChild(child);
	        else
	           child.setNodeValue(trimmedNodeVal);
	     }
	     else if (nodeType == Node.COMMENT_NODE)
	        node.removeChild(child);
	  }
	}

	
	@Override
	public void start() {
		try {
			model.clear();
			InputSource input = new InputSource(server);
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setCoalescing(true);
			factory.setIgnoringElementContentWhitespace(true);
			factory.setNamespaceAware(true);
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document document = builder.parse(input);
			clean(document);
			NodeList nodes = document.getElementsByTagNameNS( "http://www.w3.org/2005/Atom", "entry");
			for(int i = 0; i < nodes.getLength(); i++) {
				AtomEntry entry = new AtomEntry();
				Node item = nodes.item(i);
				Node node = item.getFirstChild();
				entry.title = node.getTextContent();
				node = node.getNextSibling();
				NamedNodeMap attributes = node.getAttributes();
				entry.url = attributes.getNamedItem("href").getNodeValue();
				entry.type = attributes.getNamedItem("type").getNodeValue();
				entry.length = Long.valueOf(attributes.getNamedItem("length").getNodeValue());
				model.addElement(entry);
			}
			
		} catch(IOException | ParserConfigurationException | SAXException io) {
			
		}
		
		
	}

	@Override
	public void stop() {
	}

	@Override
	public void acceptCBookEvent(CBookEvent ev) {
	}

	JFileChooser chooser = new JFileChooser();
	
	@Override
	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();
		if (source == up) {
			chooser.setMultiSelectionEnabled(true);
			int result = chooser.showOpenDialog(this);
			if (result == JFileChooser.APPROVE_OPTION) {
				File[] list = chooser.getSelectedFiles();
				for (File f : list) {
					AtomEntry entry = new AtomEntry();
					entry.title = f.getName();
					entry.url = f.toURI().toASCIIString();
					entry.length = f.length();
					entry.type = "application/octet-stream";
					model.addElement(entry);
				}
			}
			return;
		}
		if (source == down) {
			chooser.setMultiSelectionEnabled(false);
			int result = chooser.showSaveDialog(this);
			if (result == JFileChooser.APPROVE_OPTION) {
				File f = chooser.getSelectedFile();
				AtomEntry atom = items.getSelectedValue();
				System.err.println("read from " + atom.url);
				System.err.println(" write to " + f);
			}
			return;
		}
		if (source == delete) {
			int[] selected = items.getSelectedIndices();
			for (int i = selected.length-1; i >= 0; i--) {
				model.remove(selected[i]);
			}
		}
		
	}

}
