package fi.dwo.dwojapplet.gui.action;

import fi.dwo.dwojapplet.domain.DwoProfile;
import fi.dwo.dwojapplet.domain.Sco;
import fi.dwo.dwojapplet.gui.CenterPanel;
import fi.dwo.dwojapplet.gui.ParameterManagementPanel;
import fi.dwo.dwojapplet.gui.ScoPanel;
import java.awt.event.ActionEvent;
import java.util.Hashtable;

public class PreviewHtml5 extends GuiAction {

    ParameterManagementPanel panel;
    Sco sco;

    public PreviewHtml5() {
        super("Preview HTML");
    }

    public PreviewHtml5(String text) {
        super(text);
    }

    public PreviewHtml5(ParameterManagementPanel panel, Sco sco) {
        this();
        this.panel = panel;
        this.sco = sco;
        setEnabled(sco.hasFeature(Sco.JSON_OUT) && DwoProfile.hasRight(DwoProfile.PREVIEW));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        final CenterPanel center = instance().getMainPanel().getCenter();
        final WrapSco wrap = new WrapSco(sco);
        Hashtable tmp = panel.getLaunchdata();
        panel.tmp = sco.getLaunchdata(); //????
        panel.done = true;
        sco.setLaunchdata(tmp);
        ScoPanel sp = instance().previewSco(wrap);
        sp.tmp = panel;
        wrap.dwo.setCurrentSco(wrap);
        center.loadCenter(sp);
    }
}
