package fi.beans.scorm;

import java.applet.*;
import java.awt.*;
import java.io.*;
import java.util.*;
import java.awt.event.*;

import fi.beans.mainframe.*;
import fi.beans.private_base64code.*;

public class ScormEditMainFrame extends MainFrame implements ActionListener {

    ScormEditComponentIF scormEditComponent;
    String className, jarName;

    public ScormEditMainFrame(Applet applet, int width, int height) {
        super(applet, width, height);
        setTitle("ScormEditor");

        MenuItem mi;
        MenuBar mbalk = new MenuBar();
        setMenuBar(mbalk);
        Menu bestandMenu = new Menu("bestand");
        mbalk.add(bestandMenu);

        mi = new MenuItem("opslaan");
        mi.addActionListener(this);
        bestandMenu.add(mi);

        className = applet.getClass().getName();
        jarName = className.substring(3, className.indexOf(".", 3));
    }

    public void setScormEditComponent(ScormEditComponentIF scormEditComponent) {
        this.scormEditComponent = scormEditComponent;
    }

    public void save() {
        try {
            PrintWriter out = new PrintWriter(new FileWriter("test.htm"));
            printTestHTML(out);
            out.close();
        }
        catch (IOException ie) {
        }
    }

    public void printTestHTML(PrintWriter out) {
        Hashtable launchData = scormEditComponent.getLaunchData();
        String editModeState = (String) launchData.get("editModeState");
        out.println("<HTML>");
        out.println("<HEAD>");
        out.println("</HEAD>");
        out.println("<BODY bgcolor=\"#DDEEFF\">");
        out.println("<center>");
        out.println("<h1> Test </h1>");
        out.println("<APPLET");
        out.println("	id		= \"" + jarName + "\"");
        out.println("	name	= \"" + jarName + "\"");
        out.println("	code	= \"" + className + "\"");
        out.println("	archive	= \"" + jarName + ".jar\"");
        out.println("	width	= \"790\"");
        out.println("	height	= \"485\">");

        String launchDataString = StringCodeObject.encodeObjectToString(launchData);
        out.println("	<PARAM NAME=\"launchData\" VALUE=\"" + launchDataString + "\"/>");

        out.println("");
        out.println("</APPLET>");
        out.println("</BODY>");
        out.println("</HTML>");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        MenuItem item = (MenuItem) (e.getSource());
        String keuze = item.getLabel();

        if (keuze.equals("opslaan")) {
            this.save();
        }
    }
}
