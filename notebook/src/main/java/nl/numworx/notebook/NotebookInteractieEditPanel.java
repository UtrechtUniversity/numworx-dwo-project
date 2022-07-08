package nl.numworx.notebook;

import java.util.Collections;
import java.util.Hashtable;
import javax.swing.JPanel;

import org.cbook.cbookif.CBookContext;

import fi.beans.wiskopdrbeans.InteractieEditPanel;

public class NotebookInteractieEditPanel extends JPanel implements
		InteractieEditPanel, CBookContext {

	private static final long serialVersionUID = 4981417988189908524L;

	Editor editor;
	
	NotebookInteractieEditPanel(Notebook widget) {
		super(null);
		editor = new Editor(widget.getLocale());

		editor.setLocation(0, 0);
		editor.setInstanceWidth(500);
		editor.setInstanceHeight(450);
		editor.setLaunchData(Collections.<String, Object>emptyMap());
		add(editor);
	}

	@Override
	public void setBounds(int x, int y, int width, int height) {
		super.setBounds(x, y, width, height);
		editor.setSize(width, height);
		editor.invalidate();
		editor.doLayout();
	}

	public void setEditState(Hashtable b) {
		editor.setLaunchData(b);
		//editor.setObjectivesState(b);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Hashtable getEditState() {
		Hashtable map = new Hashtable( editor.getLaunchData() );
		//map.putAll(editor.getObjectivesState());
		map.put("premium", Boolean.TRUE);
		return map;
	}

	public void zetBreedte(int b) {
		editor.setInstanceWidth(b);
	}

	public void zetHoogte(int h) {
		editor.setInstanceHeight(h);
	}

	public void stop() {
		editor.stop();
	}

	public void start() {
		editor.start();
	}

	public Object getProperty(String arg0) {
		if("randomVars".equals(arg0))
			return Collections.EMPTY_MAP; // FIXME
		return null;
	}

}
