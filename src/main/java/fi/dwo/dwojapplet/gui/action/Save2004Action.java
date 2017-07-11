package fi.dwo.dwojapplet.gui.action;

import fi.beans.appletutil.AppletUtil;
import fi.beans.private_base64code.StringCodeObject;
import fi.dwo.commons.system.TextMapper;
import fi.dwo.dwojapplet.domain.CourseMap;
import fi.dwo.dwojapplet.domain.DWO;
import fi.dwo.dwojapplet.domain.DwoHelper;
import fi.dwo.dwojapplet.domain.Sco;
import fi.dwo.dwojapplet.gui.GuiConstants;
import fi.dwo.dwojapplet.gui.GuiCreator;
import fi.dwo.dwojapplet.gui.ScormChooser;
import fi.dwo.dwojapplet.gui.ScormParameters;

import java.applet.Applet;
import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.swing.JFileChooser;

public class Save2004Action extends GuiAction {

    private static final Logger LOG = Logger.getLogger(Save2004Action.class.getName());

    @Override
    public void actionPerformed(ActionEvent e) {
        if (sco0 == null) {
            CourseMap selection = Clipboard.getSelection();
            if (selection != null && selection.getUserObject() instanceof Sco) {
                save2004((Sco) selection.getUserObject());
            }
            return;
        }
        save2004(sco0);

    }

    public Save2004Action() {
        super("Export SCO");
        Clipboard.addPropertyChangeListener("selection", this);
        setEnabled(false);
    }

    public Save2004Action(Sco sco) {
        super("Export SCO");
        this.sco0 = sco;
    }

    private Sco sco, sco0; // in constructor

    ScormChooser scormChooser;

    public void save2004(Sco sco) {
        if (scormChooser == null) {
            scormChooser = new ScormChooser();
            //scormChooser.html5.setEnabled(false);
            scormChooser.html5.setText("Noordhoff HTML5");
        }
        this.sco = sco; // design error: sco is een parameter, geen field!  

        int result = scormChooser.showSaveDialog(GuiCreator.instance().getMainPanel());
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = scormChooser.getSelectedFile();
            boolean is2004 = scormChooser.isScorm2004();
            boolean ishtml5 = scormChooser.isHTML5();
            String naam = file.getName();
            if (naam.lastIndexOf(".") > -1) {
                naam = naam.substring(0, naam.indexOf("."));
            }
            if (ishtml5) {
                createHtml5(file, "2014_v1_0");
            } else if (is2004) {
                createScorm2004(file);
            } else {
                createZip(file.getAbsoluteFile().getParentFile().getAbsolutePath() + "/" + naam);
            }
        }
    }

    private void createHtml5(File file, String variant) {
        try {
            ZipOutputStream out = new ZipOutputStream(new FileOutputStream(file));

            ScormParameters runner = new ScormParameters();

            // ugly string creation
            String scormURL = DwoHelper.getAppURLPath().toString() + variant + "/";
            runner.setBase(scormURL);
            runner.setUser(GuiCreator.instance().getUser());

            createHtml5(variant, out, runner, sco);
	        
		
			out.close();
        } catch (IOException e) {
            LOG.log(Level.SEVERE, null, e);
        }

    }

	static void createHtml5(String variant, ZipOutputStream out, ScormParameters runner, Sco sco)
			throws IOException, UnsupportedEncodingException, MalformedURLException {
		createHTML_XML(variant, out, runner, sco, "");

		AppletUtil au = DwoHelper.getAu();
 // manifest
 		out.putNextEntry(new ZipEntry("imsmanifest.xml"));
 		runner.copy(au.getStream("resources/imsmanifestHtml5.txt"), out);
 		out.closeEntry();
 // metadata
 		out.putNextEntry(new ZipEntry("metadata.xml"));
 		runner.copy(au.getStream("resources/metadata.txt"), out);
 		out.closeEntry();

		makeCopies(out, runner, runner.getBase());
	}

	static void createHTML_XML(String variant, ZipOutputStream out, ScormParameters runner, Sco sco, String prefix)
			throws IOException, UnsupportedEncodingException {
		AppletUtil au = DwoHelper.getAu();
		runner.setSco(sco);
		String id;
		id = sco.getScoName();
// verboden characters worden _	: space, /, ? , #	
		id = en_code_(id);
		runner.setId(id);
		id = prefix + id;
// sco
		out.putNextEntry(new ZipEntry(id + ".html"));
// sco.txt is profiel afhankelijk!
		int profile = DWO.getDwoProfileID();
		InputStream in = au.getStream("resources/" + variant + "-" + profile + ".txt");
		if (in == null) {
		    in = au.getStream("resources/" + variant + ".txt");
		}
		runner.copy(in, out);
		out.closeEntry();

		out.putNextEntry(new ZipEntry(id + ".xml"));
		Map ld = new HashMap(runner.getLaunchData());
		OutputStreamWriter wr = new OutputStreamWriter(out, ScormParameters.UTF8);
		sco.jsonEncode(ld, wr);
		wr.flush();
		out.closeEntry();
	}

	static void makeCopies(ZipOutputStream out, ScormParameters runner, String scormURL)
			throws MalformedURLException, IOException {
		// copies.....
        String HTML_SOURCE = new URL(DwoHelper.getResourceUrlPath() , "resources/scorm/course/cp/").toString();
        String[] scormFileNames = {
        		"adlcp_v1p3.xsd",
        		"imscp_v1p1.xsd",
        		"imsmd_v1p2p4.xsd",
        };
        
        copyList(out, runner, HTML_SOURCE, scormFileNames, "");
       
        makeAuxilaryCopies(out, runner, scormURL, "");
	}

	static void makeAuxilaryCopies(ZipOutputStream out, ScormParameters runner, String scormURL, String prefix)
			throws MalformedURLException, IOException {
		InputStream in;
		String[] viewFileNames = new String[0];
    	HashSet<String> set = new HashSet<String>();
        try {
        	in = new URL(scormURL + "index.lst").openStream();
        	BufferedReader reader = new BufferedReader(new InputStreamReader(in, Charset.forName("UTF-8")));
        	String line;
        	while ( (line = reader.readLine())!= null) { set.add(line); }
        } catch(Exception e) {
        	LOG.log(Level.SEVERE, "makeCopies", e);;
        }
        viewFileNames = set.toArray(viewFileNames);
        
        copyList(out, runner, scormURL, viewFileNames, prefix);
	}

    static void copyList(ZipOutputStream out, ScormParameters runner,
            String HTML_SOURCE, String[] scormFileNames, String HTML_DEST)
            throws MalformedURLException, IOException {
        InputStream in;
        for (int i = 0; i < scormFileNames.length; i++) {
            String htmlSourceString = HTML_SOURCE + scormFileNames[i];
            try {
                URL htmlSource = new URL(htmlSourceString);
                URLConnection connection = htmlSource.openConnection();
                in = connection.getInputStream();
                out.putNextEntry(new ZipEntry(HTML_DEST + scormFileNames[i]));
                runner.rawCopy(in, out);
                out.closeEntry();
            } catch (Exception e) {
                LOG.log(Level.SEVERE, "Error in " + htmlSourceString, e);
            }
        }
    }

    public void createZip(String zipName) {
        try {
            String outFilename = zipName + ".zip";
            ZipOutputStream out = new ZipOutputStream(new FileOutputStream(outFilename));

            String HTML_SOURCE = new URL(DwoHelper.getResourceUrlPath() , "resources/scorm/course/cp/").toExternalForm();
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
            for (int i = 0; i < scormFileNames.length; i++) {
                String htmlSourceString = HTML_SOURCE + scormFileNames[i];
                URL htmlSource = new URL(htmlSourceString);
                URLConnection connection = htmlSource.openConnection();
                InputStream in = connection.getInputStream();
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
// zipEntry metadata.xml 
            out.putNextEntry(new ZipEntry("metadata.xml"));
            printMetadata(pw);
            pw.flush();
            out.closeEntry();
// end...	    	
            out.close();
        } catch (IOException e) {
        }

    }

    /**
     * print metadata volgens LOM.
     *
     * @param pw
     */
    private void printMetadata(PrintWriter pw) {
        pw.println("<?xml version='1.0' ?>");
        pw.println("<lom>");
        String title = sco.getScoName();
        String description = sco.getDescription();
//        String auteur = "Peter Boon"; // currentuser...
//        String datum = new java.util.Date().toString(); // formateren!
        String uri = "MANIFEST-9ECDE6EE-4D8C-0E0A-E9B1-A1C808BC2ECD";
        String lang = DwoHelper.getApplet().getLocale().getLanguage();
        // print metadata: 
        pw.println("<general>");
        // titel
        pw.println("<title><langstring xml:lang='" + lang + "' >" + title + "</langstring></title>");
        // URI
        pw.println("<catalogentry>"
                + "<catalog>URI</catalog>"
                + "<entry><langstring xml:lang='x-none'>" + uri + "</langstring>"
                + "</entry>"
                + "</catalogentry>");
        // lang
        pw.println("<language>" + lang + "</language>");
        // description ok
        pw.println("<description><langstring xml:lang='" + lang + "' >" + description + "</langstring></description>");
        pw.println("</general>");
        // TODO auteur/creator
        // TODO datum/version
        // TODO copyright
        pw.println("</lom>");
    }

    public void printManifest(PrintWriter out) {
        String scoName = sco.getScoName();

        Object[] arguments = {scoName};

        try {
            URL htmlSource = new URL(DwoHelper.getResourceUrlPath(), "resources/scorm/course/cp/imsmanifest.txt");
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
        } catch (IOException e) {
        }

    }

    public void printScormHTML(PrintWriter out) {
        Hashtable launchData = sco.getEditLaunchdata();
        if (launchData == null) {
            launchData = sco.getLaunchdata();
        }
        Class<? extends Applet> applet = sco.getApplet().getClass();
        String className = applet.getName();
        String jarName = className.substring(3, className.indexOf(".", 3));
        String scoName = sco.getScoName();
// licentie manager, via een parameter
        String licentie = "null";
//		try { 
//			User u = GuiCreator.instance().getUser();
//			licentie = LicMan.getLicense(u.getSchool().getSchoolID(), sco.getCourse().getDwoProfile(), applet);
//			launchData.put(LicMan.LICENSE_KEY, licentie);
//		} catch (LicenseException e)
//		{
//			// iets beters dan printstacktrace
//			LOG.log(Level.SEVERE,null,e);
//		}
        String launchDataString = StringCodeObject.encodeObjectToString(launchData);
//		launchData.remove(LicMan.LICENSE_KEY);

        String language = TextMapper.getLanguage();
        String bgcolor = "#" + Integer.toHexString(GuiConstants.MAIN_BACKGROUND.getRGB()).substring(2);

        Object[] arguments = {scoName, className, jarName, language, bgcolor, launchDataString, licentie};

        try {
            URL htmlSource = new URL(DwoHelper.getResourceUrlPath(), "resources/scorm/course/cp/sco/Sco.htm");
            if (sco.getCourse().getDwoProfile() == 1) {
                htmlSource = new URL(DwoHelper.getResourceUrlPath(), "resources/scorm/course/cp/sco/ScoN.htm");
            }
            if (sco.getCourse().getDwoProfile() == 22) {
                htmlSource = new URL(DwoHelper.getResourceUrlPath(), "resources/scorm/course/cp/sco/ScoRev.htm");
            }
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
        } catch (IOException e) {
        }

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
         out.println("	codebase = \WWWURL + "/javaclasses/\"");
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

    private void createScorm2004(File file) {
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
            if (in == null) {
                in = au.getStream("resources/sco.txt");
            }
            runner.copy(in, out);
            out.closeEntry();

// copies.....
            // TODO meer xsd's?
            String HTML_SOURCE = new URL( DwoHelper.getResourceUrlPath(), "resources/scorm/course/cp/").toExternalForm();
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

            copyList(out, runner, HTML_SOURCE, scormFileNames,"");

            out.close();
        } catch (IOException e) {
            LOG.log(Level.SEVERE, null, e);
        }

    }

    @Override
    void setMap(CourseMap map) {
        if (map != null) {
            setEnabled(map.getUserObject() instanceof Sco);
        } else {
            setEnabled(false);
        }
    }

	/**
	 * verboden characters worden _	: space, /, ? , #	
	 * @param id input
	 * @return id
	 */
		static String en_code_(String id) {
			id = id.replace(' ', '_');
			id = id.replace('/', '_');
			id = id.replace('?', '_');
			id = id.replace('#', '_');
			return id;
		}

}
