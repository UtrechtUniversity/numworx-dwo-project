package fi.dwo.dwojapplet.gui;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

class PreviewAction extends AbstractAction {

    private ScoPanel scoPanel;

    PreviewAction(ScoPanel scoPanel) {
        super("stop preview");
        this.scoPanel = scoPanel;
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        scoPanel.getSco().setLaunchdata(scoPanel.tmp.tmp);
        scoPanel.getSco().setDataChanged(false);
        GuiCreator.instance().getMainPanel().getCenter().loadCenter(scoPanel.tmp);
    }

}
