package fi.dwo.dwojapplet.gui.action;

import java.awt.Component;
import java.awt.FileDialog;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Hashtable;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import fi.dwo.dwojapplet.domain.CourseMap;
import fi.dwo.dwojapplet.domain.DwoHelper;
import fi.dwo.dwojapplet.domain.Sco;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ImportScorm extends GuiAction {
    private static final Logger log = Logger.getLogger(ImportScorm.class.getName());

    private FileDialog openDial;
    private Sco sco;

    public ImportScorm() {
        super("Import SCO");
        setEnabled(false);
        Clipboard.addPropertyChangeListener("selection", this);
    }

    public ImportScorm(Sco sco) {
        super("Import SCO");
        this.sco = sco;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Component source = (Component) e.getSource();
        final Frame topFrame = DwoHelper.getFrameForComponent(source);
        openDial = new FileDialog(topFrame, "openen", FileDialog.LOAD);
        openDial.setDirectory(System.getProperty("user.dir", "."));

        if (sco == null) {
            open((Sco) Clipboard.getSelection());
        } else {
            open(sco);
        }
    }

    private void open(Sco sco) {
        String directory, naam;
        openDial.show();
        directory = openDial.getDirectory();
        naam = openDial.getFile();
        if (naam != null) {
            readZip(directory + naam, sco);
        }
    }

    private void readZip(String zipName, Sco sco) {
        try {
            ZipFile zipFile = new ZipFile(zipName);
            ZipEntry entry = zipFile.getEntry("sco/Sco.htm");

            //om compatible te blijven:
            if (entry == null) {
                entry = zipFile.getEntry("sco\\Sco.htm");
            }
            if (entry == null) {
                entry = zipFile.getEntry("sco/WiskOpdr.htm");
            }
            if (entry == null) {
                entry = zipFile.getEntry("sco\\WiskOpdr.htm");
            }

            InputStream in = zipFile.getInputStream(entry);
            BufferedReader bin = new BufferedReader(new InputStreamReader(in));

            if (bin != null) {
                String string = "";
                String tmp;
                while ((tmp = bin.readLine()) != null) {
                    string += tmp + "\n";
                }
                in.close();
                zipFile.close();
                bin.close();

                Hashtable params = new Hashtable();
                int start = string.indexOf("<APPLET");
                int end = string.indexOf("</APPLET>");
                string = string.substring(start + 7, end);
                start = string.indexOf("<PARAM");
                end = string.indexOf("/>");
                while (start > 0) {
                    String param = string.substring(start + 6, end);

                    int naamBegin = param.indexOf("NAME=\"");
                    int naamEind = param.indexOf("\"", naamBegin + 6);
                    String naam = param.substring(naamBegin + 6, naamEind);

                    int waardeBegin = param.indexOf("VALUE=\"");
                    int waardeEind = param.indexOf("\"", waardeBegin + 7);
                    String waarde = param.substring(waardeBegin + 7, waardeEind);

                    params.put(naam, waarde);
                    string = string.substring(end + 2);
                    start = string.indexOf("<PARAM");
                    end = string.indexOf("/>");
                }
                sco.setEditLaunchdata(params);
            }
        } catch (IOException e) {
            log.log(Level.SEVERE,null,e);
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

}
