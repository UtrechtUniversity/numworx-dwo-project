// Source file:
// C:\\parameters\\fi\\dwo\\client\\gui\\ParameterManagementPanel.java

package fi.dwo.client.gui;

import java.applet.Applet;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.FlowLayout;
import java.awt.FontMetrics;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.List;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.security.SecureRandom;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.ListSelectionEvent;

import fi.beans.appletutil.AppletUtil;
import fi.beans.base64code.StringCodeObject;
import fi.beans.licman.LicMan;
import fi.beans.licman.LicenseException;
import fi.beans.scorm.Parameter;
import fi.beans.scorm.ScormAppletIF;
import fi.beans.scorm.ScormEditComponentIF;
import fi.dwo.client.domain.DWO;
import fi.dwo.client.domain.Sco;
import fi.dwo.client.domain.DwoHelper;
import fi.dwo.client.domain.User;
import fi.dwo.client.persistence.MapperCreator;
//import fi.dwo.client.system.Collections;
import fi.dwo.client.system.TextMapper;
import fi.dwo.parameters.domain.ConvertorCreator;
import fi.dwo.parameters.domain.ConvertorIF;
import fi.dwo.parameters.gui.MainParameterComponent;
import fi.dwo.parameters.gui.ParameterComponent;

/**
 * This class is a panel for editing the parameters of a SCO.
 * If the SCO has an edit-mode, a dialog with the editmode is showed.<br> 
 * The editmode is showed in a dialog, because of the possible size of the editmode.<br>
 * If the SCO has not an edit-mode, a MainParameterComponent is showed with the parameters of the sco.<br>
 * @author M.J.B. Kupers
 *
 */
public class ParameterManagementPanel extends JPanel implements CenterSubPanel, ActionListener, WindowListener {
    private static final boolean POPUP = false; // FIXME in productie true

	private CenterPanel center;

    private ScormEditComponentIF editComponent;
    private Parameter[] parameters;
    private ParameterComponent parameterComponent;

    private boolean editMode;
    
    private Sco sco;
    
    private JButton previewButton;
    private JButton saveButton;
    private JButton resetButton;
    private JButton closeButton;
    private JButton importScormButton;
    private JButton exportScormButton;
    private JButton exportAppletButton;
    
    private JLabel noParamLabel;
    
    private JScrollPane scrollPanel;
    
    private JDialog editModeDialog;
    private ScoDialog scoDialog;
    
    private FileDialog openDial;
    private FileDialog saveDial;
    private String scormTitel;

    /**
     * Creates a new ParameterManagementPanel
     * If the sco has an edit-mode, a dialog with the editmode is showed.
     * If the sco has not an edit-mode, a MainParameterComponent is showed with the parameters of the sco.<br>
     * @param sco The SCO wherefrom the parameters must be changed.
     */
    public ParameterManagementPanel(Sco sco) {
        super(new BorderLayout());
        
// nodig, maar nog niet gezet.
        setCenterPanel(GuiCreator.instance().getMainPanel().getCenter());
        center.end(); // idempotent!
        GuiCreator.instance().setWait();

        this.sco = sco;
        this.setBackground(GuiConstants.MAIN_BACKGROUND);
        JPanel buttonPanel;
		JPanel mainPanel;  // import van awt componenten
        buttonPanel = new JPanel(new FlowLayout()/*,BorderedPanel.SOUTH*/);
        buttonPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.black));
        buttonPanel.setBackground(GuiConstants.MAIN_BACKGROUND);
        this.add(buttonPanel, BorderLayout.NORTH);
        
        mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(GuiConstants.MAIN_BACKGROUND);
        this.add(mainPanel, BorderLayout.CENTER);
        
		ScormAppletIF applet = null;
        Applet editableApplet = sco.getApplet();
        if(editableApplet instanceof ScormAppletIF)
        {
        	applet = (ScormAppletIF) editableApplet;
            editMode = applet.hasEditMode();
        } else 
        	editMode = false;
        
        if(editMode) {
            Hashtable launchData = sco.getLaunchdata();
            launchData.put("language", TextMapper.getLanguage());
        	editComponent = applet.getEditComponent(launchData);
            this.setSize(800, 620);
            this.setPreferredSize(getSize());
            this.setMinimumSize(getSize());
            
            String title = TextMapper.getText(TextMapper.GUIPA_DLG_TTL);
            String[] tmp = {sco.getScoName()};
            title = MessageFormat.format(title, tmp);
            
            editModeDialog = new JDialog(DwoHelper.getFrameForComponent(DwoHelper.getApplet()), title, false);
           
            editModeDialog.setSize(800, 620);
            
            Dimension parentSize = Toolkit.getDefaultToolkit().getScreenSize();
            Dimension size = editModeDialog.getSize();
            int x = (parentSize.width - size.width) / 2;
            int y = (parentSize.height - size.height) / 2;
            
            if(x < 0) {
                x = 0;
            }
            if(y < 0) {
                y = 0;
            }

            editModeDialog.setLocation(x, y);
            editModeDialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
            editModeDialog.addWindowListener(this);
            
            // keuze, embedded of popup
            	if(!POPUP)
            	{	
            		editModeDialog = null;
            	} else {
            		editModeDialog.setContentPane(this);
            	}
        	} else {
        		if(applet != null)
        			parameters = applet.getEditableParameters();
        		else 
        			parameters = null;
        		if(parameters == null)parameters = new Parameter[0];
        		this.setSize(627, 485);
        		this.setPreferredSize(getPreferredSize());
        		editModeDialog = null;
        }
        
        
        FontMetrics fm;

        previewButton = new JButton(TextMapper.getText(TextMapper.GUIPA_BTN_PREVIEW));
        previewButton.addActionListener(this);
        buttonPanel.add(previewButton);
        
        if(sco.getAppletID()==12) previewButton.setEnabled(false);// geen preview mogelijk bij popupurlapplet
        
        saveButton = new JButton(TextMapper.getText(TextMapper.GUIPA_BTN_SAVE));
        saveButton.addActionListener(this);
        buttonPanel.add(saveButton);

        resetButton = new JButton(TextMapper.getText(TextMapper.GUIPA_BTN_RESET));
        resetButton.addActionListener(this);
        buttonPanel.add(resetButton);

        closeButton = new JButton(TextMapper.getText(TextMapper.GUIPA_BTN_CANCEL));
        closeButton.addActionListener(this);
        buttonPanel.add(closeButton);
// school 190 264 385 heeft scorm export recht     
        if(DwoHelper.isApplication())
        {	if( DwoHelper.isScormExportLoggedIn() ) 
        	{   importScormButton = new JButton("Import Scorm");
		        importScormButton.addActionListener(this);
		        buttonPanel.add(importScormButton);
		        
		        exportScormButton = new JButton("Export Scorm");
		        exportScormButton.addActionListener(this);
		        buttonPanel.add(exportScormButton);
        	}
/*
 * Let op alleen 13,20,27,46 en 51 voor recht 'a' of ADMIN
 *  (sco.getCourse().getDwoProfile()==13 
   || sco.getCourse().getDwoProfile()==20 
   || sco.getCourse().getDwoProfile()==27 
   || sco.getCourse().getDwoProfile()==46 
   || sco.getCourse().getDwoProfile()==51)
*/
	        if ( DwoHelper.isAppletExportLoggedIn() ) 
	    	{   exportAppletButton = new JButton("Export Applet");
		        exportAppletButton.addActionListener(this);
		        buttonPanel.add(exportAppletButton);
	    	}
	        
            
 // Fix, new FileDialog(Dialog,...) is 1.5          
	        final Frame topFrame = DwoHelper.getFrameForComponent(editModeDialog==null?(Component)DwoHelper.getApplet():editModeDialog);
	        openDial = new FileDialog(topFrame, "openen", FileDialog.LOAD);
			openDial.setDirectory(System.getProperty("user.dir","."));
			
            saveDial = new FileDialog(topFrame, "opslaan", FileDialog.SAVE);
			saveDial.setDirectory(System.getProperty("user.dir","."));
			saveDial.setName("*.htm");
        }
        
                
        scrollPanel = new JScrollPane();
        scrollPanel.setBorder(null);
        scrollPanel.setBackground(GuiConstants.MAIN_BACKGROUND);
        scrollPanel.getViewport().setBackground(GuiConstants.MAIN_BACKGROUND);
        mainPanel.add(scrollPanel);
        
           
        if(!editMode) {
            Hashtable tmp = sco.getLaunchdata();
            ConvertorIF convertor = ConvertorCreator.createConverter(ConvertorCreator.CONV_LAUNCHDATA);
            tmp = (Hashtable) convertor.convertHashtable(tmp, parameters);
            parameterComponent = new MainParameterComponent(parameters, tmp);
            JPanel hulp = new JPanel(new FlowLayout(FlowLayout.LEADING), false); hulp.add(parameterComponent);
            hulp.setOpaque(false);
            scrollPanel.setViewportView(hulp);//parameterComponent);
        } else {
            scrollPanel.setSize(getSize());
            scrollPanel.setViewportView(editComponent.getComponent());
            scrollPanel.validate();
            System.out.println(editComponent.getComponent());
            
        }
        
        noParamLabel = new JLabel(TextMapper.getText(TextMapper.GUIPA_NO_PARAMS));
        noParamLabel.setFont(GuiConstants.SCO_TEXT);
        fm = noParamLabel.getFontMetrics(noParamLabel.getFont());
        noParamLabel.setSize(fm.stringWidth(noParamLabel.getText()) + 10, fm.getHeight());
        noParamLabel.setLocation((this.getSize().width/2) - (noParamLabel.getSize().width/2), 100);
        noParamLabel.setHorizontalAlignment(JLabel.CENTER);
        if(parameters != null && parameters.length == 0)scrollPanel.setViewportView(noParamLabel);
        doLayout();
        GuiCreator.instance().setReady();
        
        if(editMode) {
        	if(POPUP)
        		editModeDialog.setVisible(true);
        	else
        		if(CenterPanel.isIconizer())
        			center.loadCenter(this);
        		else
        			center.loadTotal(this);
        } else {
            center.loadCenter(this);            
        }
    }

    boolean done;
    public void end() {
    	if(!done)
    	{	done = true;
    		saveSco();
    	}	
    }

    /**
     * Returns a Panel that can function as a header panel.
     * 
     * @return A panel that can function as a header panel.
     * @see fi.dwo.client.gui.CenterSubPanel#getHeaderPanel()
     */
    public Component getHeaderPanel() {
    	return new HeaderPanel(TextMapper.getText(TextMapper.GUIPA_SCO_EDIT));
    }

    /**
     * Sets the centerpanel to communicate with.
     * 
     * @param centerPanel The centerPanel to communicate with.
     */
    public void setCenterPanel(CenterPanel centerPanel) {
        center = centerPanel;
    }

    /**
     * @return java.awt.Component
     */
    public JComponent getComponent() {
        return this;
    }

    /**
     * Invoked when an action occurs.
     * 
     * @param e The ActionEvent.
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == closeButton) {
        	int x = saveSco();
        	if(x == JOptionPane.CLOSED_OPTION || x == JOptionPane.CANCEL_OPTION)
        		return;
        	done = true;
        	if(editMode)
        		editComponent.end();
        	
            if(editMode&&POPUP) {
                editModeDialog.setVisible(false);
            } else {
                center.loadCenter(GuiCreator.instance().getScoManagementPanel(sco.getCourse()));
            }
        } else if(e.getSource() == resetButton) {
            if(editMode) {
                editComponent.reset();
            } else {
                parameterComponent.reset();
            }
        } else if(e.getSource() == previewButton) {
            Hashtable tmp, old;
            if(editMode) {
                tmp = editComponent.getLaunchData();
            } else {
                tmp = (Hashtable) sco.getLaunchdata();
                ConvertorIF convertor = ConvertorCreator.createConverter(ConvertorCreator.CONV_LAUNCHDATA);
                tmp = (Hashtable) convertor.convertHashtable(tmp, parameters);
                
                parameterComponent.addParameters(tmp);
                tmp = (Hashtable) convertor.createHashtable(tmp, parameters);
            }
            old = sco.getLaunchdata();
            sco.setLaunchdata(tmp);
            ScoPanel sp = GuiCreator.instance().previewSco(sco);
            // vreembde fout: preview gaat de eerste keer niet goed, tweede keer wel
            // Ik maak dus nogmaal een scoPanel aan.
            sco.setLaunchdata(tmp);
            sp = GuiCreator.instance().previewSco(sco);
            
            //ScoDialog.showScoPreview(this, sp);
            scoDialog = new ScoDialog(this, sp.getSco().getScoName(), "", false, sp);
            scoDialog.show();
            //
            sco.setLaunchdata(old);
            
            
        } else if (e.getSource() == saveButton) {
        	int x = saveSco();
        	done = false;
//        	if(editMode&&POPUP) {
//                editModeDialog.setVisible(false);
//            }
           
        } else if(e.getSource() == exportScormButton) {
// even uit in productie, aan bij testen
        	//save();
        	save2004();
        } else if(e.getSource() == importScormButton) {
	    	open();
        } else if(e.getSource() == exportAppletButton) {
        	saveApplet();
        }
    }
    	
    private int saveSco() {
    	String message;
        Hashtable tmp;
        if(editMode) {
            tmp = editComponent.getLaunchData();
        } else {
            tmp = (Hashtable) sco.getLaunchdata();
            ConvertorIF convertor = ConvertorCreator.createConverter(ConvertorCreator.CONV_LAUNCHDATA);
            tmp = (Hashtable) convertor.convertHashtable(tmp, parameters);
            parameterComponent.addParameters(tmp);
            tmp = (Hashtable) convertor.createHashtable(tmp, parameters);
        }
        Hashtable old = sco.getLaunchdata();
    	old.remove("language");old.remove("bgcolor");
    	tmp.remove("language");tmp.remove("bgcolor");
    	
    	
    	
        message = TextMapper.getText(TextMapper.GUIPA_MSG_PARAM_SAVE);
        int result;
// Deze tekst is m.i. niet helemaal lekker geformuleerd. Wim
        if (
        		//!(compareMap(tmp, old)) &&
        		(result = JOptionPane.showConfirmDialog(this, message, TextMapper.getText(TextMapper.GUIPA_MSG_TTL_PARAM_SAVE), JOptionPane.YES_NO_OPTION)) == JOptionPane.YES_OPTION) {
        	final GuiCreator instance = GuiCreator.instance();
			instance.setWait();setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        	sco.setLaunchdata(tmp);
            instance.updateSco(sco);
            MapperCreator.instance(Applet.class).removeObject(sco.getAppletID());
            instance.setReady();setCursor(Cursor.getDefaultCursor());
            return JOptionPane.YES_OPTION;
        }
        return result; // NO, CANCEL, CLOSED
    }

    // map equals map, 
    // serialized object not not equal others.
    
	private boolean compareMap(Map tmp, Map old) {
		boolean equals = old.equals(tmp); // dit zou genoeg moeten zijn!
		if(equals) return true;
		equals = old.size() == tmp.size();
		if(!equals) return false;

		Iterator iter = old.keySet().iterator();
    	while (iter.hasNext()) {
			Object object = (Object) iter.next();
			Object v1 = old.get(object);
			Object v2 = tmp.get(object);
			if(v1.equals(v2))
			{
				// all's well
			} else {
				if(v1 instanceof String && v2 instanceof String)
				{
// base64 differ, objects may be not!
					Object vv1 = StringCodeObject.decodeStringToObject(v1.toString());
					Object vv2 = StringCodeObject.decodeStringToObject(v2.toString());				
					if(vv1 instanceof Map && vv2 instanceof Map)
					{
						equals = compareMap((Map)vv1, (Map)vv2);
					} else 
					{
						equals = vv1 != null && vv1.equals(vv2);
					}
					if(!equals) 
						return false;
				} else
				if(v2 != null && v1.getClass().isArray() && v2.getClass().isArray())
				{
					Class c1 = v1.getClass();
					Class c2 = v2.getClass();
// int[], etc. ????? via Class.getComponentClass() TODO if (! v1.getClass().getComponentClass().isPrimitive() ) ...
					if( !c1.getComponentType().isPrimitive() && !c2.getComponentType().isPrimitive())
					{
						Object[] vva1 = (Object[]) v1; Object[] vva2 = (Object[]) v2;					
						equals = Arrays.equals(vva1, vva2);
						if(equals || vva1.length != vva2.length)
							return equals;
						// unequal, why
						for (int i = 0; i < vva2.length; i++) {
							Object o1 = vva1[i]; Object o2 = vva2[i];
							if( o1 instanceof Map && o2 instanceof Map ) 
							{	if( ! compareMap((Map)o1, (Map)o2))
									return false;
							} else 
								if( ! (o1 != null) && ! o1.equals(o2))
									return false;
								if( o1 == null && o2 != null)
									return false;
						}
						return true;
					} else {
// er zijn 7 types: byte, char, short, int, long, float, double
						if(v1 instanceof byte[] && v2 instanceof byte[] )
						{
							return Arrays.equals( (byte[])v1, (byte[])v2);
						}
						if(v1 instanceof int[] && v2 instanceof int[] )
						{
							return Arrays.equals( (byte[])v1, (byte[])v2);
						}
						// to difficult...
						return false;
					}
				} else
					return false;
			}
		}
    	return true;
	}
    
	public void open()
	{	String directory,naam;
		openDial.show();
		directory = openDial.getDirectory();
		naam = openDial.getFile();
		if(naam!=null)
		{	readZip(directory+naam);
		}
	}
	
	public void save()
	{	String directory,naam;
		saveDial.show();
		directory = saveDial.getDirectory();
		naam = saveDial.getFile();
		if(naam!=null)
		{	if(naam.indexOf(".")>-1)naam = naam.substring(0,naam.indexOf("."));
			scormTitel = naam;
			createZip(directory+naam);
			//schrijfTestFile(directory+""+sco.getScoName()+".htm");
		}
	}
	
	ScormChooser scormChooser;
	public void save2004()
	{
		if(scormChooser == null)
		{
			scormChooser = new ScormChooser();
			//scormChooser.scorm2004.setEnabled(false);
		}
		int result = scormChooser.showSaveDialog(this);
		if(result == JFileChooser.APPROVE_OPTION)
		{
			File file = scormChooser.getSelectedFile();
			boolean is2004 = scormChooser.isScorm2004();
			String naam = file.getName();
			if(naam.lastIndexOf(".")>-1)naam = naam.substring(0,naam.indexOf("."));
			scormTitel = naam;
			if(is2004)
				createScorm2004(file);
			else
				createZip(file.getAbsoluteFile().getParentFile().getAbsolutePath() + "/" + naam);
		}		
	}
	
	class ScormParameters {

		private Random rn = new Random();

	    private int rand(int lo, int hi) {
	        int n = hi - lo + 1;
	        int i = rn.nextInt() % n;
	        if (i < 0)
	            i = -i;
	        return lo + i;
	    }

	    private String randomstring(int lo, int hi) {
	        int n = rand(lo, hi);
	        char b[] = new char[n];
	        for (int i = 0; i < n; i++)
	            b[i] = (char) rand('a', 'z');
	        return new String(b);
	    }

	    private String randomstring() {
	        return randomstring(10, 20);
	    }

		
// SCO parameters
		static final int SCO_TITLE = 0;
		static final int SCO_CLASS = 1;
		static final int SCO_JAR   = 2;
		static final int SCO_LAUNCH_DATA = 3;		
		static final int SCO_ID = 4;
		static final int SCO_DESCRIPTION = 5;
// USER parameters
		static final int USER_FIRSTNAME = 6;
		static final int USER_LASTNAME = 7;
		static final int USER_EMAIL = 8;
// VERSION
		static final int VERSION = 9;
		static final int UUID = 10;
		static final int LANG = 11;
		static final int BGCOLOR = 12;
		private static final int PLENGTH = 13;
		private Object[] parameters = new Object[PLENGTH];
		
		public void setSco(Sco sco)
		{
			parameters[SCO_TITLE] = sco.getScoName();
			parameters[SCO_DESCRIPTION] = sco.getDescription();
			parameters[SCO_CLASS] = sco.getAppletData().getClassName();
			parameters[SCO_JAR] = sco.getAppletData().getJarName();
			parameters[SCO_ID] = String.valueOf(sco.getID());
			Hashtable launchData = null;
			if(editMode) launchData = editComponent.getLaunchData();
			else launchData = sco.getLaunchdata();
			Class applet = sco.getApplet().getClass();
			
	// licentie manager, via een parameter
			String licentie = "null";
			try { 
				User u = GuiCreator.instance().getUser();
				licentie = LicMan.getLicense(u.getSchool().getSchoolID(), sco.getCourse().getDwoProfile(), applet);
				launchData.put(LicMan.LICENSE_KEY, licentie);
			} catch (LicenseException e)
			{
				// TODO iets beters dan printstacktrace
				e.printStackTrace();
			}
			parameters[SCO_LAUNCH_DATA] = StringCodeObject.encodeObjectToString(launchData);
			launchData.remove(LicMan.LICENSE_KEY);

		}
		public void setUser(User u)
		{
			parameters[USER_FIRSTNAME] = u.getFirstname();
			parameters[USER_LASTNAME] = (u.getMiddleName() + " " + u.getLastName()).trim();
			parameters[USER_EMAIL] = u.getEmail();
		}
		
		private final DateFormat FMT = new SimpleDateFormat("ddMMyyyy");
		private static final String UTF8 = "UTF-8";
		public ScormParameters()
		{
			Date now = new Date();
			parameters[VERSION] = FMT.format(now);
			parameters[UUID] = randomstring();
			parameters[LANG] = TextMapper.getLanguage();
			parameters[BGCOLOR] =  "#" + Integer.toHexString(GuiConstants.MAIN_BACKGROUND.getRGB()).substring(2);
		}
		
		public void copy(BufferedReader in, PrintWriter out) throws IOException
		{
			String line;
			while( (line = in.readLine()) != null) 
			{
				out.println(MessageFormat.format(line, parameters));
			}
			out.flush();
			in.close();
		}
		
		public void copy(Reader in, Writer out) throws IOException
		{
			BufferedReader bin; 
			PrintWriter pout;
			if( in instanceof BufferedReader)
				bin = (BufferedReader) in;
			else
				bin = new BufferedReader(in);
			if( out instanceof PrintWriter)
				pout = (PrintWriter) out;
			else
				pout = new PrintWriter(out);
			copy(bin, pout);
		}
		
		public void copy(InputStream in, OutputStream out) throws IOException
		{
			copy(new InputStreamReader(in, UTF8), new OutputStreamWriter(out, UTF8));
		}
		byte[] buf = new byte[1024];

		public void rawCopy(InputStream in, OutputStream out) throws IOException {
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            in.close();
		}
		
	}
	
	
	private void createScorm2004(File file)
	{
		try {
			ZipOutputStream out = new ZipOutputStream(new FileOutputStream(file));

			AppletUtil au = DwoHelper.getAu();
			
			ScormParameters runner = new ScormParameters();
			runner.setSco(sco);
			runner.setUser(GuiCreator.instance().getUser());
			
// manifest
			out.putNextEntry(new ZipEntry("imsmanifest.xml"));
			runner.copy(au.getStream("resources/imsmanifest2004.txt"), out);
			out.closeEntry();
// metadata
			out.putNextEntry(new ZipEntry("metadata.xml"));
			runner.copy(au.getStream("resources/metadata.txt"), out);
			out.closeEntry();
// sco
			out.putNextEntry(new ZipEntry("sco/sco.html"));
// sco.txt is profiel afhankelijk!
			int profile = sco.getCourse().getDwoProfile();
			InputStream in = au.getStream("resources/sco-" + profile + ".txt");
			if(in == null)
				in = au.getStream("resources/sco.txt");
			runner.copy(in, out);
			out.closeEntry();
		
// copies.....
			// TODO meer xsd's?
	        String HTML_SOURCE = "http://www.fi.uu.nl/dwo/scorm/course/cp/";
	        String[] scormFileNames = {
	        		"adlcp_v1p3.xsd",
	        		"imscp_v1p1.xsd",
	        		"imsmd_v1p2p4.xsd",
	        		"sco/script/FiSco2004Script.js",
	        		"sco/Image1.png",
	        		"sco/Image2.png",
	        		"sco/Image3.png",
	        		"sco/Image4.png",
	        		"sco/Image5.png",
	        		"sco/Image6.png",
	        		"sco/Image7.png",
	        		"sco/Image8.png"
	        		};
	        
	        for (int i=0; i<scormFileNames.length; i++) 
	        {	String htmlSourceString = HTML_SOURCE + scormFileNames[i];
	        	URL htmlSource = new URL(htmlSourceString);
	        	URLConnection connection = htmlSource.openConnection();
	        	in =  connection.getInputStream();
	        	out.putNextEntry(new ZipEntry(scormFileNames[i]));
	        	runner.rawCopy(in, out);
	        	out.closeEntry();
	        }
		
			out.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
	}
	public void saveApplet()
	{	String directory,naam;
		saveDial.show();
		directory = saveDial.getDirectory();
		naam = saveDial.getFile();
		if(naam!=null)
		{	if(naam.indexOf(".")>-1)naam = naam.substring(0,naam.indexOf("."));
			scormTitel = naam;
			schrijfGrApplet(directory+""+sco.getScoName()+".htm");
		}
	}
	
	public void printManifest(PrintWriter out)
	{	String scoName = sco.getScoName();
	
		String[] arguments = {scoName};
		
		try {	
			URL htmlSource = new URL("http://www.fi.uu.nl/dwo/scorm/course/cp/imsmanifest.txt");
	        URLConnection connection = htmlSource.openConnection();
	        BufferedReader in = null;
	        try {
	            in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
	        } catch (FileNotFoundException exception) {
	            System.out.println(exception.toString());
	        }
	
	        if (in != null) {
	            String result = "";
	            String tmp = "";
	            while ((tmp = in.readLine()) != null) {
	                result += tmp + "\n";
	            }
	            in.close();
	            result = MessageFormat.format(result, arguments); 
	            out.print(result);
	        }
		}
	    catch (IOException e) 
	    {   }
	
		/*out.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		out.println("<manifest xmlns=\"http://www.imsproject.org/xsd/imscp_rootv1p1p2\" xmlns:imsmd=\"http://www.imsglobal.org/xsd/imsmd_rootv1p2p1\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:adlcp=\"http://www.adlnet.org/xsd/adlcp_rootv1p2\" identifier=\"MANIFEST-9ECDE6EE-4D8C-0E0A-E9B1-A1C808BC2ECD\" xsi:schemaLocation=\"http://www.imsproject.org/xsd/imscp_rootv1p1p2 imscp_rootv1p1p2.xsd http://www.imsglobal.org/xsd/imsmd_rootv1p2p1 imsmd_rootv1p2p1.xsd http://www.adlnet.org/xsd/adlcp_rootv1p2 adlcp_rootv1p2.xsd\">");
		out.println("  <metadata>");
		out.println("    <schema>ADL SCORM</schema>");
		out.println("    <schemaversion>1.2</schemaversion>");
		out.println("  </metadata>");
		out.println("  <organizations default=\"ORG-4863FF7A-0972-388A-6BD2-72A63860065C\">");
		out.println("    <organization identifier=\"ORG-4863FF7A-0972-388A-6BD2-72A63860065C\" structure=\"hierarchical\">");
		out.println("      <title>" + scoName + "</title>");
		out.println("      <item identifier=\"ITEM-E856E088-5802-D6DF-D54F-F2FA434622ED\" isvisible=\"true\" identifierref=\"RES-04C0E26B-E481-8F4A-42E5-D7B460A17750\">");
		out.println("        <title>" + scoName + "</title>");
		out.println("      </item>");
		out.println("    </organization>");
		out.println("  </organizations>");
		out.println("  <resources>");
		out.println("    <resource identifier=\"RES-04C0E26B-E481-8F4A-42E5-D7B460A17750\" type=\"webcontent\" adlcp:scormtype=\"sco\" href=\"sco/Sco.htm\">");
		out.println("      <file href=\"sco/Sco.htm\" />");
		out.println("      <file href=\"sco/script/FiScoScript.js\" />");
		out.println("    </resource>");
		out.println("  </resources>");
		out.println("</manifest>");*/
	}
	public void printScormHTML(PrintWriter out)
	{	Hashtable launchData = null;
		if(editMode) launchData = editComponent.getLaunchData();
		else launchData = sco.getLaunchdata();
		Class applet = sco.getApplet().getClass();
		String className = applet.getName();
		String jarName = className.substring(3,className.indexOf(".",3));
		String scoName = sco.getScoName();
// licentie manager, via een parameter
		String licentie = "null";
		try { 
			User u = GuiCreator.instance().getUser();
			licentie = LicMan.getLicense(u.getSchool().getSchoolID(), sco.getCourse().getDwoProfile(), applet);
			launchData.put(LicMan.LICENSE_KEY, licentie);
		} catch (LicenseException e)
		{
			// TODO iets beters dan printstacktrace
			e.printStackTrace();
		}
		String launchDataString = StringCodeObject.encodeObjectToString(launchData);
		launchData.remove(LicMan.LICENSE_KEY);
		
		String language = TextMapper.getLanguage();
		String bgcolor = "#" + Integer.toHexString(GuiConstants.MAIN_BACKGROUND.getRGB()).substring(2);
		
		String[] arguments = {scoName, className, jarName, language, bgcolor, launchDataString, licentie};
		
		try {	
			URL htmlSource = new URL("http://www.fi.uu.nl/dwo/scorm/course/cp/sco/Sco.htm");
			if(sco.getCourse().getDwoProfile()==1)htmlSource = new URL("http://www.fi.uu.nl/dwo/scorm/course/cp/sco/ScoN.htm");
			if(sco.getCourse().getDwoProfile()==22)htmlSource = new URL("http://www.fi.uu.nl/dwo/scorm/course/cp/sco/ScoRev.htm");
	        URLConnection connection = htmlSource.openConnection();
	        BufferedReader in = null;
	        try {
	            in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
	        } catch (FileNotFoundException exception) {
	            System.out.println(exception.toString());
	        }
	
	        if (in != null) {
	            String result = "";
	            String tmp = "";
	            while ((tmp = in.readLine()) != null) {
	                result += tmp + "\n";
	            }
	            in.close();
	            result = MessageFormat.format(result, arguments); 
	            out.print(result);
	        }
		}
        catch (IOException e) 
	    {   }
         
		/*		
		out.println("<HTML>");
		out.println("<HEAD>");
		out.println("	<TITLE>"+scoName+"</TITLE>");
		out.println("	<SCRIPT type=\"text/javascript\" src=\"script/FiScoScript.js\"></SCRIPT>");
		out.println("	<SCRIPT>");
		out.println("		var exitPageStatus;");
		out.println("		");
		out.println("		function quit()");
		out.println("		{	if (exitPageStatus != true)");
		out.println("			{	document.applets[0].stopSco();");
		out.println("				exit();");
		out.println("				exitPageStatus=true;");
		out.println("			}");
		out.println("		}");
		out.println("	</SCRIPT>");
		out.println("</HEAD>");
		out.println("<BODY bgcolor=\"#DDEEFF\" onload=\"javascript:init();\" onbeforeunload=\"javascript:quit();\" onunload=\"javascript:quit();\">");
		out.println("<center>");
		out.println("<h1>"+scoName+"</h1>");
		out.println("<APPLET");
		out.println("	id		= \"wiskopdr\"");
		out.println("	name	= \"wiskopdr\"");
		out.println("	codebase = \"http://www.fi.uu.nl/javaclasses/\"");
		out.println("	code	= \"" + className + "\"");
		out.println("	archive	= \"jars/" + jarName + ".jar\"");
		out.println("	width	= \"770\"");
		out.println("	height	= \"470\"");
		out.println("	mayscript=\"mayscript\">");
		out.println("");
		out.println("	<PARAM NAME=\"API\" VALUE=\"fi.beans.scorm.JSScormAPI\"/>");
		out.println("	<PARAM NAME=\"language\" VALUE=\"" + launchData.get("language") + "\"/>");
		out.println("	<PARAM NAME=\"bgcolor\" VALUE=\"" + launchData.get("bgcolor") + "\"/>");
		out.println("");
		out.println("	<PARAM NAME=\"launchData\" VALUE=\"" + launchDataString + "\"/>");
		out.println("");
		out.println("</APPLET>");
		out.println("</BODY>");
		out.println("</HTML>");*/
	}
	
	/*void schrijfTestFile(String naam)
	{	try
		{	PrintWriter out = new PrintWriter(new FileWriter(naam));
			printTestHTML(out);
			out.close();
		}
		catch(IOException ie)
		{
		}
	}*/
	
	void schrijfGrApplet(String naam)
	{	try
		{	PrintWriter out = new PrintWriter(new FileWriter(naam));
			GRHTML(out);
			out.close();
		}
		catch(IOException ie)
		{
		}
	}
	
	/*public void printTestHTML(PrintWriter out)
	{	Hashtable launchData = null;
		if(editMode) launchData = editComponent.getLaunchData();
		else launchData = sco.getLaunchdata();
		String className = sco.getApplet().getClass().getName();
		String jarName = className.substring(3,className.indexOf(".",3));
		String launchDataString = StringCodeObject.encodeObjectToString(launchData);
		String scoName = sco.getScoName();
				
		out.println("<HTML>");
		out.println("<HEAD>");
		out.println("	<TITLE>"+scoName+"</TITLE>");
		out.println("<SCRIPT>");
		out.println("   function NewPopUp(mypage, myname, w, h, scroll)"); 
		out.println("   {	var winl = (screen.width - w) / 2;");
		out.println("	    var wint = (screen.height - h) / 2;");
		out.println("	    winprops = 'height='+h+',width='+w+',top='+wint+',left='+winl+',scrollbars='+scroll+',resizable,status=no';");
		out.println("	    win = window.open(mypage, myname, winprops);");
		out.println("	    if (parseInt(navigator.appVersion) >= 4) ");
		out.println("	    {	win.window.focus(); ");
		out.println("	    }");
		out.println("    }");
		out.println("</SCRIPT>");
		out.println("</HEAD>");
		out.println("<BODY bgcolor=\"#DDEEFF\">");
		out.println("<center>");
		out.println("<h1>"+scoName+"</h1>");
		out.println("<APPLET");
		out.println("	id	= \"wiskopdr\"");
		out.println("	name	= \"wiskopdr\"");
		out.println("	code	= \"" + className + "\"");
		out.println("	archive	= \"" + jarName + ".jar\"");
		out.println("	width	= \"880\"");
		out.println("	height	= \"380\"");
		out.println("	mayscript=\"mayscript\">");
		out.println("");
		out.println("	<PARAM NAME=\"language\" VALUE=\"" + launchData.get("language") + "\"/>");
		out.println("	<PARAM NAME=\"bgcolor\" VALUE=\"#FFFFFF\"/>");
		out.println(" 	<PARAM NAME=\"deployVariant\" VALUE=\"GR\"/>");
		out.println("	<PARAM NAME=\"launchData\" VALUE=\"" + launchDataString + "\"/>");
		out.println("");
		out.println("</APPLET>");
		out.println("</BODY>");
		out.println("</HTML>");
	}*/
	
	public void GRHTML(PrintWriter out)
	{	Hashtable launchData = null;
		if(editMode) launchData = editComponent.getLaunchData();
		else launchData = sco.getLaunchdata();
		String className = sco.getApplet().getClass().getName();
		String jarName = className.substring(3,className.indexOf(".",3));
		String launchDataString = StringCodeObject.encodeObjectToString(launchData);
		String scoName = sco.getScoName();
		
		String language = TextMapper.getLanguage();
		//String bgcolor = "#" + Integer.toHexString(GuiConstants.MAIN_BACKGROUND.getRGB()).substring(2);
		String bgcolor = "#FFFFFF";
		/*
		out.println("<!-- saved from url=(0022)http://internet.e-mail -->");
		out.println("	<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">");
		out.println("	<html xmlns=\"http://www.w3.org/1999/xhtml\">");
		out.println("	  <head>");
		out.println("	    <title>Java template</title>");
		out.println("	    <meta http-equiv=\"Content-Type\" content=\"text/html; charset=iso-8859-1\" />");
		out.println("	    <style type=\"text/css\">");
		out.println("	    <!--body {margin-left:0px;margin-top:0px;}-->");
		out.println("	    </style>");
		out.println("	  </head>");
		out.println("	  <body style=\"background-color:#FFFFFF;\">");
		out.println("	    <p><div id=\"applet\">");
		out.println("	      <APPLET id       = \"wiskopdr\"");
		out.println("	              name     = \"wiskopdr\"");
		out.println("	              code     = \"" + className + "\"");
		out.println("	              archive  = \"" + jarName + ".jar\"");
		out.println("	              width    = \"880\"");
		out.println("	              height   = \"380\"");
		out.println("	              mayscript=\"mayscript\">");
		out.println("	        <PARAM NAME=\"language\" VALUE=\"" + launchData.get("language") + "\"/>");
		out.println("	        <PARAM NAME=\"bgcolor\" VALUE=\"#FFFFFF\"/>");
		out.println("	        <PARAM NAME=\"deployVariant\" VALUE=\"GR\"/>");
		out.println("	        <PARAM NAME=\"launchData\" VALUE=\"" + launchDataString + "\"/>");
		out.println("	      </APPLET>");
		out.println("	      <script type=\"text/javascript\" src=\"insertApplet.js\"></script>");
		out.println("	    </p><div>");
		out.println("	  </body>");
		out.println("	</html>");
		*/
		
		String[] arguments = {scoName, className, jarName, language, bgcolor, launchDataString};
		
		try {	
			URL htmlSource = new URL("http://webcluster.fi.uu.nl/dwo/scorm/applet/applet.htm");
			if(sco.getCourse().getDwoProfile()==13 || sco.getCourse().getDwoProfile()==57)htmlSource = new URL("http://webcluster.fi.uu.nl/dwo/scorm/applet/appletGR.htm");
			if(sco.getCourse().getDwoProfile()==27 || sco.getCourse().getDwoProfile()==51)htmlSource = new URL("http://webcluster.fi.uu.nl/dwo/scorm/applet/appletMW.htm");
			if(sco.getCourse().getDwoProfile()==46)htmlSource = new URL("http://webcluster.fi.uu.nl/dwo/scorm/applet/appletNWK.htm");
	        URLConnection connection = htmlSource.openConnection();
	        BufferedReader in = null;
	        try {
	            in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
	        } catch (FileNotFoundException exception) {
	            System.out.println(exception.toString());
	        }
	
	        if (in != null) {
	            String result = "";
	            String tmp = "";
	            while ((tmp = in.readLine()) != null) {
	                result += tmp + "\n";
	            }
	            in.close();
	            result = MessageFormat.format(result, arguments); 
	            out.print(result);
	        }
		}
        catch (IOException e) 
	    {   }
        
	}
	

	public void createZip(String zipName)
	{	String jarname = System.getProperty( "java.class.path" );
			    
	    try 
	    {   String outFilename = zipName + ".zip";
	        ZipOutputStream out = new ZipOutputStream(new FileOutputStream(outFilename));
	        
	        /*String scriptname = "sco/script/FiScoScript.js";
	        String[] scormFileNames = {"adlcp_rootv1p2.xsd","ims_xml.xsd","imscp_rootv1p1p2.xsd","imsmd_rootv1p2p1.xsd",scriptname};
	        JarFile jar = new JarFile(new File(jarname));
	        for (int i=0; i<scormFileNames.length; i++) 
	        {
	            JarEntry entry = jar.getJarEntry(scormFileNames[i]);
	            InputStream in = jar.getInputStream(entry);
	            out.putNextEntry(new ZipEntry(scormFileNames[i]));
	            
	            byte[] buf = new byte[1024];
	            int len;
	            while ((len = in.read(buf)) > 0) {
	                out.write(buf, 0, len);
	            }
	            out.closeEntry();
	            in.close();
	        }
	        out.putNextEntry(new ZipEntry("sco/Sco.htm"));
	    	PrintWriter pw = new PrintWriter(out);
	    	printScormHTML(pw);
	    	pw.flush();
	    	out.closeEntry();
	    	
	    	out.putNextEntry(new ZipEntry("imsmanifest.xml"));
	    	printManifest(pw);
	    	pw.flush();
	    	out.closeEntry();
	    	
	        out.close();*/
	        
	        String HTML_SOURCE = "http://www.fi.uu.nl/dwo/scorm/course/cp/";
	        String[] scormFileNames = {
	        		"adlcp_rootv1p2.xsd",
	        		"ims_xml.xsd",
	        		"imscp_rootv1p1p2.xsd",
	        		"imsmd_rootv1p2p1.xsd",
	        		"sco/script/FiScoScript.js",
	        		"sco/Image1.png",
	        		"sco/Image2.png",
	        		"sco/Image3.png",
	        		"sco/Image4.png",
	        		"sco/Image5.png",
	        		"sco/Image6.png",
	        		"sco/Image7.png",
	        		"sco/Image8.png"};
	        for (int i=0; i<scormFileNames.length; i++) 
	        {	String htmlSourceString = HTML_SOURCE + scormFileNames[i];
	        	URL htmlSource = new URL(htmlSourceString);
	        	URLConnection connection = htmlSource.openConnection();
	        	InputStream in =  connection.getInputStream();
	        	out.putNextEntry(new ZipEntry(scormFileNames[i]));
	        	byte[] buf = new byte[1024];
	            int len;
	            while ((len = in.read(buf)) > 0) {
	                out.write(buf, 0, len);
	            }
	            out.closeEntry();
	            in.close();
	        }
	        out.putNextEntry(new ZipEntry("sco/Sco.htm"));
	    	PrintWriter pw = new PrintWriter(out);
	    	printScormHTML(pw);
	    	pw.flush();
	    	out.closeEntry();
	    	
	    	out.putNextEntry(new ZipEntry("imsmanifest.xml"));
	    	printManifest(pw);
	    	pw.flush();
	    	out.closeEntry();
// TODO zipEntry metadata.xml 
	    	out.putNextEntry(new ZipEntry("metadata.xml"));
	    	printMetadata(pw);
	    	pw.flush();
	    	out.closeEntry();
// end...	    	
	        out.close();
	    } 
	    catch (IOException e) 
	    {   }
	       
	}
	
	/**
	 * print metadata volgens LOM.
	 * @param pw
	 */
	private void printMetadata(PrintWriter pw) {
		pw.println("<?xml version='1.0' ?>");
		pw.println("<lom>");
		String title = sco.getScoName();
		String description = sco.getDescription();
		String auteur = "Peter Boon"; // currentuser...
		String datum = new java.util.Date().toString(); // formateren!
		String uri = "MANIFEST-9ECDE6EE-4D8C-0E0A-E9B1-A1C808BC2ECD";
		String lang = getLocale().getLanguage();
		// print metadata: 
		pw.println("<general>");
		// titel
		pw.println("<title><langstring xml:lang='" + lang + "' >"+title+"</langstring></title>");
		// URI
		pw.println("<catalogentry>"
 		+	"<catalog>URI</catalog>"
 		+	"<entry><langstring xml:lang='x-none'>" + uri + "</langstring>"
 		+	"</entry>"
 		+   "</catalogentry>");
		// lang
		pw.println("<language>" + lang + "</language>");
		// description ok
		pw.println("<description><langstring xml:lang='" + lang + "' >" + description + "</langstring></description>");
		pw.println("</general>");	
		// TODO auteur/creator
		// TODO datum/version
		// TODEcopyright
		pw.println("</lom>");
	}

	public void readZip(String zipName)
	{	try 
		{	ZipFile zipFile = new ZipFile(zipName);
			ZipEntry entry = zipFile.getEntry("sco/Sco.htm");
			
			//om compatible te blijven:
			if(entry==null) entry = zipFile.getEntry("sco\\Sco.htm");
			if(entry==null) entry = zipFile.getEntry("sco/WiskOpdr.htm");
			if(entry==null) entry = zipFile.getEntry("sco\\WiskOpdr.htm");
			
		    InputStream in =  zipFile.getInputStream(entry);
		    BufferedReader bin = new BufferedReader(new InputStreamReader(in));
			
		    if (bin != null) {
		        String string = "";
		        String tmp = "";
		        while ((tmp = bin.readLine()) != null) {
		        	string += tmp + "\n";
		        }
		        in.close();
		        zipFile.close();
		        bin.close();
		       
		        Hashtable params = new Hashtable();
				int start = string.indexOf("<APPLET");
				int end = string.indexOf("</APPLET>");
				string = string.substring(start+7,end);
				start = string.indexOf("<PARAM");
				end = string.indexOf("/>");
				while(start>0)
				{	String param = string.substring(start+6,end);
					
					int naamBegin = param.indexOf("NAME=\"");
					int naamEind = param.indexOf("\"",naamBegin+6);
					String naam = param.substring(naamBegin+6,naamEind);
					
					int waardeBegin = param.indexOf("VALUE=\"");
					int waardeEind = param.indexOf("\"",waardeBegin+7);
					String waarde = param.substring(waardeBegin+7,waardeEind);
					
					params.put(naam,waarde);
					string = string.substring(end+2);
					start = string.indexOf("<PARAM");
					end = string.indexOf("/>");
				}
				if(editMode) editComponent.setState(params);
				else sco.setLaunchdata(params);
		    }
		} 
		catch (IOException ioe) 
		{	System.err.println("Unhandled exception:");
		    ioe.printStackTrace();
		    return;
		}
    }

	public Object getUserObject() {
		return sco;
	}

	public void windowActivated(WindowEvent e) {
	}

	public void windowClosed(WindowEvent e) {
	}

	public void windowClosing(WindowEvent e) {
		ActionEvent event = new ActionEvent(closeButton, ActionEvent.ACTION_PERFORMED, closeButton.getActionCommand() );
		actionPerformed(event);
		if(!e.getWindow().isShowing())
			e.getWindow().dispose();
	}

	public void windowDeactivated(WindowEvent e) {
	}

	public void windowDeiconified(WindowEvent e) {
	}

	public void windowIconified(WindowEvent e) {
	}

	public void windowOpened(WindowEvent e) {
	}
  
    
}