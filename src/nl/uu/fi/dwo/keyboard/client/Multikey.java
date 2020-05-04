package nl.uu.fi.dwo.keyboard.client;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.PopupPanel;

import nl.uu.fi.dwo.interaction.client.FormuleEditorIF;

public class Multikey extends Composite {

	private LayoutPanel root;
	private PopupPanel  popup;
	private int offset = 10;
	private FormuleEditorIF editor;
	private MultikeyCss css = DWOTabletKeyboardFactory.resources.multikeycss();
	
	public Multikey() {
		root = new LayoutPanel();
		initWidget(root);
		popup = new PopupPanel(true);
		popup.setGlassEnabled(false);
		popup.setWidget(this);
		css.ensureInjected();
		root.setStyleName(css.root());
		popup.setStylePrimaryName(css.popup());
	}
	
	public void setAltStyle() {
		root.addStyleName(css.gray());
	}
	public void setVarStyle() {
		root.addStyleName(css.italic());
	}

	public void setKeys(char... chs) {
		root.clear();
		int len = chs.length;
		root.setPixelSize(15+37*len, 52);
		for(int i = 0; i < len; i++ ) {
			final char ch = chs[i];
			FKey fkey = new FKey();
			fkey.setText(String.valueOf(ch));
			fkey.setStyleName(css.key());
			fkey.addClickHandler(e -> {
				getEditor().insert(ch);
				hide();
			});
			root.add(fkey);
			root.setWidgetTopHeight(fkey, 10, Unit.PX, 32, Unit.PX);
			root.setWidgetLeftWidth(fkey, 10+i*37, Unit.PX, 32, Unit.PX);
		}
	}
	
	public void show(int x, int y) {
		popup.setPopupPositionAndShow(new PopupPanel.PositionCallback() {
			
			@Override
			public void setPosition(int offsetWidth, int offsetHeight) {
				popup.setPopupPosition(x-offset, y-offset);
			}
		});
				
	}
	public void hide() {
		popup.hide();
	}

	public FormuleEditorIF getEditor() {
		return editor;
	}

	public void setEditor(FormuleEditorIF editor) {
		this.editor = editor;
	}
}
