package fi.dwo.dwojapplet.gui.action;

import fi.dwo.commons.system.TextMapper;
import fi.dwo.dwojapplet.domain.CourseMap;
import fi.dwo.dwojapplet.domain.DwoHelper;
import fi.dwo.dwojapplet.domain.Sco;
import fi.beans.loader.Loader;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JOptionPane;

public class MergeAction extends GuiAction {
    private static final Logger LOG = Logger.getLogger(MergeAction.class.getName());

    Sco dest, src;
    CourseMap map;

    public MergeAction() {
        super(TextMapper.getText("merge"));
        setEnabled(false);
    }

    public MergeAction(CourseMap map) {
        this();
        this.map = map;
        CourseMap clip = Clipboard.getClipboard();
        Object d = map.getUserObject();
        Object s = clip.getUserObject();
        if (d instanceof Sco && s instanceof Sco && "copy".equals(Clipboard.cmd)) {
            dest = (Sco) d;
            src = (Sco) s;
            if (dest.isMergable(src)) {
                System.out.println(Clipboard.cmd + " " + src + " into " + dest);
                setEnabled(true);
            }
        }
    }

    private int confirm(String message, Object source) {
        Component component = DwoHelper.getApplet();
        if (source instanceof Component) {
            component = (Component) source;
        }
        return JOptionPane.showConfirmDialog(component, message, TextMapper.getText(TextMapper.GUIPA_MSG_TTL_PARAM_SAVE), JOptionPane.YES_NO_OPTION);
    }

    static final String MERGECMD = "mergeLaunchData";

    @Override
    public void actionPerformed(ActionEvent ev) {
        instance().setWait();
        try {
            String clazz = dest.getAppletData().getClassName();
            String jar   = dest.getAppletData().getJarName();
            Class cls = Loader.create(jar).loadClass(clazz);
            Method method = cls.getMethod(MERGECMD, new Class[]{String.class, String.class});
            Object o = cls.newInstance();
            String sdata = src.getLaunchdataString();
            String ddata = dest.getLaunchdataString();
            ddata = (String) method.invoke(o, new String[]{ddata, sdata});

            String message = TextMapper.getText(TextMapper.GUIPA_MSG_PARAM_SAVE);
            int result = JOptionPane.NO_OPTION;
            if ((result = confirm(message, ev.getSource())) == JOptionPane.YES_OPTION || result == JOptionPane.CANCEL_OPTION) {
                dest.setLaunchdataString(ddata);
                instance().updateSco(dest);
                getCenter().updateMap(map);
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            LOG.log(Level.SEVERE,null,e);
        }
        instance().setReady();
    }

}
