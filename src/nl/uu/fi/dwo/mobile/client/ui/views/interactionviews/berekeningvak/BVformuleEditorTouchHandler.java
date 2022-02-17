package nl.uu.fi.dwo.mobile.client.ui.views.interactionviews.berekeningvak;

import nl.uu.fi.dwo.formule.client.formuleholder.FormuleEditorTouchHandler;
import nl.uu.fi.dwo.formule.client.formuleholder.FormuleHolder;
import nl.uu.fi.dwo.formule.client.formuleholder.MainFormuleRegel;
import nl.uu.fi.dwo.formule.client.formuleobjects.FormuleElement;

public class BVformuleEditorTouchHandler extends FormuleEditorTouchHandler {

	public BVformuleEditorTouchHandler(FormuleHolder editor) {
		super(editor);
	}
	
	@Override
	protected void onStart(StartEvent event) {
		super.onStart(event);
		
		MainFormuleRegel mainRegel = editor.getMainRegel();
		int elementCount = mainRegel.getElementCount();
		if(elementCount > 0) {
			FormuleElement element = mainRegel.getElementAt(elementCount-1);
			int elementX = element.x;
			if(elementX < event.x);
				mainRegel.setIndexAt(elementCount-1);
		}
	}
}
