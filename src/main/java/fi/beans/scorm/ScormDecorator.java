package fi.beans.scorm;

import java.applet.Applet;
import java.applet.AppletContext;
import java.applet.AppletStub;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.Locale;

public class ScormDecorator extends Applet implements SCORM12APIInterface {

    @SuppressWarnings("FieldMayBeFinal")
    private Applet applet;
    private final String lineSeparator = System.getProperty("line.separator", "\n");

    private class Stub implements AppletStub {

        @Override
        public boolean isActive() {
            return ScormDecorator.this.isActive();
        }

        @Override
        public URL getDocumentBase() {
            return ScormDecorator.this.getDocumentBase();
        }

        @Override
        public URL getCodeBase() {
            return ScormDecorator.this.getCodeBase();
        }

        @Override
        public String getParameter(String name) {
            return ScormDecorator.this.getParameter(name);
        }

        @Override
        public AppletContext getAppletContext() {
            return ScormDecorator.this.getAppletContext();
        }

        @Override
        public void appletResize(int width, int height) {
            ScormDecorator.super.resize(width, height);
        }

    }

    @Override
    public void destroy() {
        applet.destroy();
    }

    @Override
    public void disable() {
        applet.disable();
    }

    @Override
    public String getAppletInfo() {
        return applet.getAppletInfo();
    }

    @Override
    public Dimension getMaximumSize() {
        return applet.getMaximumSize();
    }

    @Override
    public Dimension getMinimumSize() {
        return applet.getMinimumSize();
    }

    @Override
    public String[][] getParameterInfo() {
        return applet.getParameterInfo();
    }

    @Override
    public void init() {
        applet.init();
    }

    @Override
    public Dimension minimumSize() {
        return applet.minimumSize();
    }

    @Override
    public Dimension preferredSize() {
        return applet.preferredSize();
    }

    @Override
    public void print(Graphics g) {
        applet.print(g);
    }

    @Override
    public void printAll(Graphics g) {
        applet.printAll(g);
    }

    @Override
    public void printComponents(Graphics g) {
        applet.printComponents(g);
    }

    @Override
    public void repaint(long tm, int x, int y, int width, int height) {
        applet.repaint(tm, x, y, width, height);
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        applet.resize(width, height);
    }

    @Override
    public void setBackground(Color c) {
        applet.setBackground(c);
    }

    @Override
    public void setBounds(int x, int y, int width, int height) {
        super.setBounds(x, y, width, height);
        applet.resize(width, height);
    }

    @Override
    public void setForeground(Color c) {
        applet.setForeground(c);
    }

    @Override
    public void setLocale(Locale l) {
        applet.setLocale(l);
    }

    @Override
    public void setName(String name) {
        applet.setName(name);
    }

    @Override
    public void start() {
        applet.start();
    }

    @Override
    public void stop() {
        applet.stop();
    }

    @Override
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

    @Override
    public String LMSInitialize(String arg0) {
        // TODO Auto-generated method stub
        return "";
    }

    @Override
    public String LMSFinish(String arg0) {
        return "";
    }

    @Override
    public String LMSGetValue(String key) {
        try {
            FileReader f = new FileReader(key);
            StringBuffer sb = new StringBuffer(100);
            int c;
            while ((c = f.read()) != -1) {
                sb.append((char) c);
            }
            f.close();
            sb.setLength(sb.length() - lineSeparator.length());
            return sb.toString();
        } catch (IOException e) {
        }
        return "";
    }

    @Override
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

    @Override
    public String LMSCommit(String arg0) {
        // TODO Auto-generated method stub
        return "";
    }

    @Override
    public String LMSGetLastError() {
        // TODO Auto-generated method stub
        return "";
    }

    @Override
    public String LMSGetErrorString(String arg0) {
        // TODO Auto-generated method stub
        return "";
    }

    @Override
    public String LMSGetDiagnostic(String arg0) {
        // TODO Auto-generated method stub
        return "";
    }

    @Override
    public void validate() {
        applet.validate();
    }

}
