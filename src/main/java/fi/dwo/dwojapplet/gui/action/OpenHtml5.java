package fi.dwo.dwojapplet.gui.action;

import java.awt.event.ActionEvent;

import fi.dwo.dwojapplet.domain.CourseMap;
import fi.dwo.dwojapplet.domain.DwoProfile;
import fi.dwo.dwojapplet.domain.Sco;
import fi.dwo.dwojapplet.gui.CenterPanel;
import fi.dwo.dwojapplet.gui.CenterSubPanel;

public class OpenHtml5 extends GuiAction {

    Sco sco;

    public OpenHtml5() {
        super("Open as HTML");
    }

    public OpenHtml5(String text) {
        super(text);
    }

    public OpenHtml5(Sco sco) {
        this();
        this.sco = sco;
        setEnabled(sco.hasFeature(Sco.JSON_OUT) && DwoProfile.hasRight(DwoProfile.PREVIEW));
    }

    public OpenHtml5(CourseMap map) {
        this((Sco) map.getUserObject());
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        final CenterPanel center = instance().getMainPanel().getCenter();
        center.end();
        final WrapSco wrap = new WrapSco(sco);
        CenterSubPanel csp = instance().getScoPanel(wrap);
        wrap.dwo.setCurrentSco(wrap);
        center.loadTotal(csp);
    }
}
