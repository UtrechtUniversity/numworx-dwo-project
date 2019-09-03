package fi.dwo.dwojapplet.gui.action;

import fi.dwo.dwojapplet.domain.CourseMap;
import fi.dwo.dwojapplet.domain.Sco;
import fi.dwo.dwojapplet.gui.CenterPanel;
import fi.dwo.dwojapplet.gui.CenterSubPanel;

import java.awt.event.ActionEvent;

public class OpenApplet extends GuiAction {

    Sco sco;

    public OpenApplet() {
        super("Open as Applet");
    }

    public OpenApplet(String text) {
        super(text);
    }

    public OpenApplet(Sco sco) {
        this();
        this.sco = sco;
    }

    public OpenApplet(CourseMap map) {
        this((Sco) map.getUserObject());
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        final CenterPanel center = instance().getMainPanel().getCenter();
        center.end();        
        CenterSubPanel csp = instance().getScoPanel(sco);
        center.loadTotal(csp);
    }
}
