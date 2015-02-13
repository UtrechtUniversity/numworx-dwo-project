package fi.beans.scorm;

import java.applet.Applet;
import java.applet.AppletContext;
import java.applet.AppletStub;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.util.Locale;


public class ScormDecorator extends Applet implements SCORM12APIInterface {

	private Applet applet;
	private String lineSeparator = System.getProperty("line.separator", "\n");
	
	private class Stub implements AppletStub
	{

		public boolean isActive() {
			return ScormDecorator.this.isActive();
		}

		public URL getDocumentBase() {
			return ScormDecorator.this.getDocumentBase();
		}

		public URL getCodeBase() {
			return ScormDecorator.this.getCodeBase();
		}

		public String getParameter(String name) {
			return ScormDecorator.this.getParameter(name);
		}

		public AppletContext getAppletContext() {
			return ScormDecorator.this.getAppletContext();
		}

		public void appletResize(int width, int height) {
			ScormDecorator.super.resize(width, height);
		}
		
	}
	
	
	public void destroy() {
		applet.destroy();
	}

	public void disable() {
		applet.disable();
	}

	
	public String getAppletInfo() {
		return applet.getAppletInfo();
	}

	public Dimension getMaximumSize() {
		return applet.getMaximumSize();
	}

	public Dimension getMinimumSize() {
		return applet.getMinimumSize();
	}

	public String[][] getParameterInfo() {
		return applet.getParameterInfo();
	}

	public void init() {
		applet.init();
	}

	public Dimension minimumSize() {
		return applet.minimumSize();
	}

	public Dimension preferredSize() {
		return applet.preferredSize();
	}

	public void print(Graphics g) {
		applet.print(g);
	}

	public void printAll(Graphics g) {
		applet.printAll(g);
	}

	public void printComponents(Graphics g) {
		applet.printComponents(g);
	}

	public void repaint(long tm, int x, int y, int width, int height) {
		applet.repaint(tm, x, y, width, height);
	}

	public void resize(int width, int height) {
		super.resize(width,height);
		applet.resize(width, height);
	}

	public void setBackground(Color c) {
		applet.setBackground(c);
	}

	public void setBounds(int x, int y, int width, int height) {
        super.setBounds(x,y,width, height);
		applet.resize(width, height);
	}

	public void setForeground(Color c) {
		applet.setForeground(c);
	}

	public void setLocale(Locale l) {
		applet.setLocale(l);
	}

	public void setName(String name) {
		applet.setName(name);
	}

	public void start() {
		applet.start();
	}

	public void stop() {
		applet.stop();
	}

	public String toString() {
		return applet.toString();
	}

	/**
	 * @param applet
	 */
	public ScormDecorator(Applet applet) {
        setLayout(null);
		this.applet = applet;
		applet.setStub(new Stub());
        add(applet);
	}

	public String LMSInitialize(String arg0) {
		// TODO Auto-generated method stub
		return "";
	}

	public String LMSFinish(String arg0) {
		return "";
	}

	public String LMSGetValue(String key) {
	try {
		FileReader f = new FileReader(key);
		StringBuffer sb = new StringBuffer(100);
		int c; 
		while( (c = f.read()) != -1)
			sb.append((char)c);
		f.close();
		sb.setLength(sb.length()-lineSeparator.length());
		return sb.toString();
	} catch (IOException e) {
	}
		return "";
	}

	public String LMSSetValue(String key, String value) {
	    FileWriter f;
		try {
			f = new FileWriter(key);
		    f.write(value);
		    f.write(lineSeparator);
		    f.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
        return "";
	}

	public String LMSCommit(String arg0) {
		// TODO Auto-generated method stub
		return "";
	}

	public String LMSGetLastError() {
		// TODO Auto-generated method stub
		return "";
	}

	public String LMSGetErrorString(String arg0) {
		// TODO Auto-generated method stub
		return "";
	}

	public String LMSGetDiagnostic(String arg0) {
		// TODO Auto-generated method stub
		return "";
	}

    public void validate()
    {
        applet.validate();
    }
    
}
