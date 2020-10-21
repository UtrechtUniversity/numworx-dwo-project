package fi.dwo.dwojapplet.gui.domainmodel.graph;

import java.awt.Dimension;
import java.net.URL;
import java.util.List;

import javax.swing.JFrame;

import fi.dwo.commons.persistence.Dwo2ExceptionJavaTranslator;
import fi.dwo.commons.system.MD5;
import fi.dwo.dwojapplet.domain.DWO;
import fi.dwo.dwojapplet.domain.DwoHelper;
import nl.uu.fi.dwo.lms.jclient.lib.rest.managers.LoginManager;
import nl.uu.fi.dwo.lms.jclient.lib.rest.managers.SecureTeacherSchoolClassManager;
import nl.uu.fi.dwo.lms.jclient.lib.rest.managers.SecureTeacherStudentModelManager;
import nl.uu.fi.dwo.lms.jclient.lib.rest.managers.SecureUserAccountLoginsManager;
import nl.uu.fi.dwo.lms.jclient.lib.rest.transport.RestAuthenticator;
import nl.uu.fi.dwo.rest.dom.entities.DomContext;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContext;
import nl.uu.fi.dwo.rest.dom.entities.DomUserFullwLoginContext;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
import nl.uu.fi.dwo.rest.util.Dwo2ExceptionTranslator;

public class ShowDomainGraphPanel extends JFrame {

	private LeerdomeinGraphPanel panel;

	public ShowDomainGraphPanel() {
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		panel = new LeerdomeinGraphPanel();
		setContentPane(panel);
		pack();
	}

	public static void main(String[] args) throws Exception {
		ShowDomainGraphPanel main = new ShowDomainGraphPanel();
		Dimension screenSize = (new DWO()).getToolkit().getScreenSize();
	    int x = (screenSize.width-main.getSize().width)/2;
	    int y = (screenSize.height-main.getSize().height)/2;
	    main.setLocation(x , y);
	    main.setVisible(true);
	}
}
