// Source file:
// C:\\parameters\\fi\\dwo\\client\\gui\\ParameterManagementPanel.java

package fi.dwo.client.gui;

import java.applet.Applet;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.FlowLayout;
import java.awt.FontMetrics;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.text.MessageFormat;
import java.util.Hashtable;
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
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

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
import fi.dwo.client.system.TextMapper;
import fi.dwo.parameters.domain.ConvertorCreator;
import fi.dwo.parameters.domain.ConvertorIF;
import fi.dwo.parameters.gui.MainParameterComponent;
import fi.dwo.parameters.gui.ParameterComponent;
import fi.dwo.server.persistence.DbAccess;

/**
 * This class is a panel for editing the parameters of a SCO.
 * If the SCO has an edit-mode, a dialog with the editmode is showed.<br> 
 * The editmode is showed in a dialog, because of the possible size of the editmode.<br>
 * If the SCO has not an edit-mode, a MainParameterComponent is showed with the parameters of the sco.<br>
 * @author M.J.B. Kupers
 *
 */
public class ParameterManagementPanel extends JPanel implements CenterSubPanel, ActionListener {
    private CenterPanel center;

    private ScormEditComponentIF editComponent;
    private Parameter[] parameters;
    private ParameterComponent parameterComponent;

    private boolean editMode;
    
    private Sco sco;
    
    private JButton previewButton;
    private JButton saveButton;
    private JButton resetButton;
    private JButton cancelButton;
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
        GuiCreator.instance().setWait();
        this.sco = sco;
        this.setBackground(GuiConstants.MAIN_BACKGROUND);
        JPanel buttonPanel;
		JPanel mainPanel;  // import van awt componenten
        buttonPanel = new JPanel(new FlowLayout()/*,BorderedPanel.SOUTH*/);
        buttonPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.black));
       // buttonPanel.setSize(800, 40);
        buttonPanel.setBackground(GuiConstants.MAIN_BACKGROUND);
        this.add(buttonPanel, BorderLayout.NORTH);
        
        //mainPanel = new Panel(new BorderLayout());
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
            String title = TextMapper.getText(TextMapper.GUIPA_DLG_TTL);
            String[] tmp = {sco.getScoName()};
            title = MessageFormat.format(title, tmp);
            
            editModeDialog = new JDialog(DwoHelper.getFrameForComponent(DwoHelper.getApplet()), title, false);
            //editModeDialog = new Frame(title);
            
            editModeDialog.setSize(800, 620);
            //editModeDialog.setLayout(new BorderLayout());
            editModeDialog.setContentPane(this);
            
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
            //editModeDialog.addWindowListener(this);
            editModeDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
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
        //fm = previewButton.getFontMetrics(previewButton.getFont());
        //previewButton.setSize(90, fm.getHeight() + 10);
        //previewButton.setLocation(20, 20);
        previewButton.addActionListener(this);
        buttonPanel.add(previewButton);
        
        if(sco.getAppletID()==12) previewButton.setEnabled(false);// geen preview mogelijk bij popupurlapplet
        
        saveButton = new JButton(TextMapper.getText(TextMapper.GUIPA_BTN_SAVE));
        //fm = saveButton.getFontMetrics(saveButton.getFont());
        //saveButton.setSize(90, fm.getHeight() + 10);
        //saveButton.setLocation(previewButton.getLocation().x + previewButton.getSize().width + 10, 20);
        saveButton.addActionListener(this);
        buttonPanel.add(saveButton);

        resetButton = new JButton(TextMapper.getText(TextMapper.GUIPA_BTN_RESET));
        //fm = resetButton.getFontMetrics(resetButton.getFont());
        //resetButton.setSize(90, fm.getHeight() + 10);
        //resetButton.setLocation(saveButton.getLocation().x + saveButton.getSize().width + 10, 20);
        resetButton.addActionListener(this);
        buttonPanel.add(resetButton);

        cancelButton = new JButton(TextMapper.getText(TextMapper.GUIPA_BTN_CANCEL));
        //fm = cancelButton.getFontMetrics(cancelButton.getFont());
        //cancelButton.setSize(90, fm.getHeight() + 10);
        //cancelButton.setLocation(resetButton.getLocation().x + resetButton.getSize().width + 10, 20);
        cancelButton.addActionListener(this);
        buttonPanel.add(cancelButton);
        
        if(DwoHelper.isApplication())
        {	if(DwoHelper.isAdminLoggedIn() || DwoHelper.isScormExportLoggedIn() || sco.getCourse().getSchoolID()==190  || sco.getCourse().getSchoolID()==264 || sco.getCourse().getSchoolID()==385) 
        	{   importScormButton = new JButton("Import Scorm");
		        //fm = cancelButton.getFontMetrics(cancelButton.getFont());
		        //importScormButton.setSize(90, fm.getHeight() + 10);
		        //importScormButton.setLocation(cancelButton.getLocation().x + resetButton.getSize().width + 10, 20);
		        importScormButton.addActionListener(this);
		        buttonPanel.add(importScormButton);
		        
		        exportScormButton = new JButton("Export Scorm");
		        //fm = cancelButton.getFontMetrics(cancelButton.getFont());
		        //exportScormButton.setSize(90, fm.getHeight() + 10);
		        //exportScormButton.setLocation(cancelButton.getLocation().x + resetButton.getSize().width + 10, 20);
		        exportScormButton.addActionListener(this);
		        buttonPanel.add(exportScormButton);
        	}
	        if(DwoHelper.isAdminLoggedIn() || DwoHelper.isAppletExportLoggedIn() && (sco.getCourse().getDwoProfile()==13 || sco.getCourse().getDwoProfile()==20 || sco.getCourse().getDwoProfile()==27)) 
	    	{   exportAppletButton = new JButton("Export Applet");
		        //fm = cancelButton.getFontMetrics(cancelButton.getFont());
		        //exportAppletButton.setSize(90, fm.getHeight() + 10);
		        //exportAppletButton.setLocation(cancelButton.getLocation().x + resetButton.getSize().width + 10, 20);
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
        //scrollPanel.setSize(633, 455);
        //scrollPanel.setLocation(-3, -3);
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
            scrollPanel.setSize(editModeDialog.getSize());
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
            editModeDialog.setVisible(true);
        } else {
            GuiCreator.instance().getMainPanel().getCenter().loadCenter(this);            
        }
    }

    public void end() {

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
    public Component getComponent() {
        return this;
    }

    /**
     * Invoked when an action occurs.
     * 
     * @param e The ActionEvent.
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == cancelButton) {
            if(editMode) {
                editComponent.end();
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
            String message;
            message = TextMapper.getText(TextMapper.GUIPA_MSG_PARAM_SAVE);
            if (JOptionPane.showConfirmDialog(this, message, TextMapper.getText(TextMapper.GUIPA_MSG_TTL_PARAM_SAVE), JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
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
	            sco.setLaunchdata(tmp);
	            GuiCreator.instance().updateSco(sco);
	            MapperCreator.instance(Applet.class).removeObject(sco.getAppletID());
	            if(editMode) {
	                editModeDialog.setVisible(false);
	            } else {
	                center.loadCenter(GuiCreator.instance().getScoManagementPanel(sco.getCourse()));
	            }    
            }
        } else if(e.getSource() == exportScormButton) {
        	save();
        } else if(e.getSource() == importScormButton) {
	    	open();
        } else if(e.getSource() == exportAppletButton) {
        	saveApplet();
        }
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
			DWO dwo = (DWO) DwoHelper.getApplet();
			User u = dwo.getUser();
			licentie = LicMan.getLicense(u.getSchool().getSchoolID(), dwo.getDwoProfile().getID(), applet);
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
			URL htmlSource = new URL("http://www.fi.uu.nl/dwo/scorm/applet/applet.htm");
			if(sco.getCourse().getDwoProfile()==13)htmlSource = new URL("http://www.fi.uu.nl/dwo/scorm/applet/appletGR.htm");
			if(sco.getCourse().getDwoProfile()==27)htmlSource = new URL("http://www.fi.uu.nl/dwo/scorm/applet/appletMW.htm");
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
	    	
	        out.close();
	    } 
	    catch (IOException e) 
	    {   }
	       
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
  
    
}