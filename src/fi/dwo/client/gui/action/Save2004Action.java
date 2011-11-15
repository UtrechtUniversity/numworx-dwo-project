package fi.dwo.client.gui.action;

import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.text.MessageFormat;
import java.util.Hashtable;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.swing.JFileChooser;

import fi.beans.appletutil.AppletUtil;
import fi.beans.base64code.StringCodeObject;
import fi.beans.licman.LicMan;
import fi.beans.licman.LicenseException;
import fi.dwo.client.domain.DwoHelper;
import fi.dwo.client.domain.Sco;
import fi.dwo.client.domain.User;
import fi.dwo.client.gui.GuiConstants;
import fi.dwo.client.gui.GuiCreator;
import fi.dwo.client.gui.ScormChooser;
import fi.dwo.client.gui.ScormParameters;
import fi.dwo.client.system.TextMapper;

public class Save2004Action extends GuiAction {

	public void actionPerformed(ActionEvent e) {
		if(sco == null)
		{
			if(Clipboard.getSelection() instanceof Sco)
			{
				save2004( (Sco) Clipboard.getSelection());
			}
			return;
		}
		save2004(sco);

	}
	
	public Save2004Action() {
		super("Export SCO");
		Clipboard.addPropertyChangeListener("selection", this);
	}

	public Save2004Action(Sco sco) {
		super("Export SCO");
		this.sco = sco;
	}

	private Sco sco; // in constructor
	
    ScormChooser scormChooser;
	
	public void save2004(Sco sco)
	{
		if(scormChooser == null)
		{
			scormChooser = new ScormChooser();
			//scormChooser.scorm2004.setEnabled(false);
		}
		int result = scormChooser.showSaveDialog(GuiCreator.instance().getMainPanel());
		if(result == JFileChooser.APPROVE_OPTION)
		{
			File file = scormChooser.getSelectedFile();
			boolean is2004 = scormChooser.isScorm2004();
			String naam = file.getName();
			if(naam.lastIndexOf(".")>-1)naam = naam.substring(0,naam.indexOf("."));
			if(is2004)
				createScorm2004(file);
			else
				createZip(file.getAbsoluteFile().getParentFile().getAbsolutePath() + "/" + naam);
		}		
	}

	public void createZip(String zipName)
	{	String jarname = System.getProperty( "java.class.path" );
			    
	    try 
	    {   String outFilename = zipName + ".zip";
	        ZipOutputStream out = new ZipOutputStream(new FileOutputStream(outFilename));
	        
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
		String lang = DwoHelper.getApplet().getLocale().getLanguage();
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
	    
	}
	public void printScormHTML(PrintWriter out)
	{	
		Hashtable launchData = sco.getEditLaunchdata();
		if(launchData != null)
			launchData = sco.getLaunchdata();
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

}
