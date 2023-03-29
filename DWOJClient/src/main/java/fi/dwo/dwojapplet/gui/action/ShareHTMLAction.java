package fi.dwo.dwojapplet.gui.action;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Locale;

import fi.dwo.commons.exceptions.PersistenceException;
import fi.dwo.commons.system.TextMapper;
import fi.dwo.dwojapplet.domain.Course;
import fi.dwo.dwojapplet.domain.DWO;
import fi.dwo.dwojapplet.domain.DwoHelper;
import fi.dwo.dwojapplet.gui.GuiConstants;
import fi.dwo.dwojapplet.persistence.PersistenceFacade;

@SuppressWarnings("serial")
public class ShareHTMLAction extends ShareAction {

	public ShareHTMLAction(int lesson, boolean deepest) {
		super(TextMapper.getText("copylinkhtml"), lesson, deepest);
		if(!deepest) {
			try {
				Course c = PersistenceFacade.instance().getCourse(lesson);
				setEnabled(!c.isWithChildren());
			} catch (PersistenceException e) {
				setEnabled(false);
			}
		}
	}

	@Override
	public Object getTransferData(DataFlavor flavor)
			throws UnsupportedFlavorException, IOException {	
		StringBuilder builder = new StringBuilder();
		String player = GuiConstants.STUDENT_PLAYER;
		URL base = DwoHelper.getURL(player);
        int profile = DWO.getDwoProfileID();
		Locale locale = DwoHelper.getAu().getLocale();
		String type = isDeepestLevel() ? "#s:": "#c:";
		int id = getID();
		builder.append( base );
		builder.append("?locale=").append(locale.getLanguage());
		builder.append("&profile=").append(profile);
		builder.append("&hash=").append(URLEncoder.encode(type)).append(id);
		builder.append(type).append(id);
		return builder.toString();
	}
	
}
