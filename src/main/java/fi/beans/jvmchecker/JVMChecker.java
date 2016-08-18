package fi.beans.jvmchecker;

import java.applet.*;
import java.awt.*;
import java.awt.event.*;
import java.net.*;

import javax.swing.JOptionPane;

import fi.beans.stringutils.StringUtils;

public class JVMChecker extends Dialog implements WindowListener, ActionListener {

    private TekstPanel tp;
    private Button downloadKnop;
    private Applet applet;

    public JVMChecker(Applet ap) {
    	super(JOptionPane.getFrameForComponent(ap), true);
        applet = ap;
        setBounds(300, 200, 300, 300);
        setBackground(new Color(255, 200, 200));
        addWindowListener(this);
        setTitle("JVMChecker");
        setLayout(null);

        tp = new TekstPanel(15, 15, 40, 300, 200);
        tp.setBackground(getBackground());
        {
            tp.setText(0, "Uw browser werkt momenteel met een ");
            tp.setText(1, "verouderde versie van Java.");
            tp.setText(2, "We kunnen daarom niet garanderen dat");
            tp.setText(3, "de applicatie probleemloos zal werken. ");
            tp.setText(4, "");
            tp.setText(5, "Met behulp van de knop hieronder gaat ");
            tp.setText(6, "u naar http://java.com/nl");
            tp.setText(7, "Via deze site kunt u de nieuwste versie");
            tp.setText(8, "van Java downloaden. Daarna dient u de ");
            tp.setText(9, "browser opnieuw te starten.");

        }
        add(tp);

        downloadKnop = new Button("Download");
        downloadKnop.setBounds(100, 250, 100, 25);
        downloadKnop.addActionListener(this);
        add(downloadKnop);
    }

    public void check() {
        boolean jvmOK = true;
        final String vendor = System.getProperty("java.vendor");
        final String version = System.getProperty("java.specification.version");
        
		if (vendor.equals("Microsoft Corp.")) {
            jvmOK = false;
        } else {
        	// format a.b.c.
        	String[] numbers = StringUtils.split(version, ".");
        	try {
        		int major = Integer.parseInt(numbers[0]);
        		if(major < 1) 
        			jvmOK = false;
        		else if(major > 1)
        			jvmOK = true;
        		else {
        			int minor = Integer.parseInt(numbers[1]);
        			jvmOK = minor >= 8;
        		}
        	
        	} catch(Exception _ignore) {
        		jvmOK = false;
        	}
        }
        if (!jvmOK) {
            this.show();
        }
    }

    public void actionPerformed(ActionEvent e) {
        URL url = null;
        try {
            url = new URL("http://java.com/nl");
        }
        catch (Exception exception) {
        }
        applet.getAppletContext().showDocument(url);//,"main");
        setVisible(false);
        dispose();
    }

    public void windowClosing(WindowEvent e) {
        dispose();
    }

    public void windowOpened(WindowEvent e) {
    }

    public void windowIconified(WindowEvent e) {
        doLayout();
    }

    public void windowDeiconified(WindowEvent e) {
        doLayout();
    }

    public void windowClosed(WindowEvent e) {
    }

    public void windowActivated(WindowEvent e) {
    }

    public void windowDeactivated(WindowEvent e) {
        
    }

}
