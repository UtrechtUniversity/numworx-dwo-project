package fi.dwo.dwojapplet.gui.action;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.lang.reflect.Method;

import javax.swing.JOptionPane;

import fi.dwo.client.domain.CourseMap;
import fi.dwo.client.domain.DwoHelper;
import fi.dwo.client.domain.Sco;
import fi.dwo.client.gui.GuiConstants;
import fi.dwo.client.persistence.PersistenceFacade;
import fi.dwo.client.system.TextMapper;

public class MergeAction extends GuiAction {
	
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
		if(d instanceof Sco && s instanceof Sco && "copy" .equals( Clipboard.cmd ) ) {
			dest = (Sco) d;
			src  = (Sco) s;
			if(dest.isMergable(src))
			{
				System.out.println( Clipboard.cmd  + " " + src + " into " + dest);
				setEnabled(true);
			}
		}
	}

	private int confirm(String message, Object source) {
			Component component = DwoHelper.getApplet();
			if(source instanceof Component) component = (Component) source;
			return JOptionPane.showConfirmDialog(component, message, TextMapper.getText(TextMapper.GUIPA_MSG_TTL_PARAM_SAVE), JOptionPane.YES_NO_OPTION);
	}
	
	static final String MERGECMD = "mergeLaunchData";
	public void actionPerformed(ActionEvent ev) {
		instance().setWait();
		try {
			String clazz = dest.getAppletData().getClassName();
			Class  cls   = Class.forName(clazz);
			Method method = cls.getMethod(MERGECMD, new Class[] { String.class, String.class });
			Object o = cls.newInstance();
			String sdata = src.getLaunchdataString();
			String ddata = dest.getLaunchdataString();
			ddata = (String) method.invoke(o, new String[] { ddata, sdata });
			
	        String message = TextMapper.getText(TextMapper.GUIPA_MSG_PARAM_SAVE);
	        int result = JOptionPane.NO_OPTION;
	        if (
	        		(result = confirm(message,ev.getSource())) == JOptionPane.YES_OPTION || result == JOptionPane.CANCEL_OPTION)
	        {
	        	dest.setLaunchdataString(ddata);
	        	instance().updateSco(dest);
	        	getCenter().updateMap(map);
	        }
		} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
		}
		instance().setReady();
	}

}
