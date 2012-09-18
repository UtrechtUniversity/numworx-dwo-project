package nl.uu.fi.dwo.mobile.client.ui;

import java.util.HashMap;

import nl.uu.fi.dwo.mobile.client.ui.formuleholder.FormuleEditor;

import com.googlecode.mgwt.dom.client.event.touch.TouchCancelEvent;
import com.googlecode.mgwt.dom.client.event.touch.TouchEndEvent;
import com.googlecode.mgwt.dom.client.event.touch.TouchHandler;
import com.googlecode.mgwt.dom.client.event.touch.TouchMoveEvent;
import com.googlecode.mgwt.dom.client.event.touch.TouchStartEvent;
import com.googlecode.mgwt.ui.client.widget.touch.TouchPanel;

/**
 * 
 * @author Evertson Croes
 * 
 */
public class FormuleEditorTouchHandler implements TouchHandler
{
	private FormuleKeyboard kb = null;
	private FormuleEditor editor = null;
	private TouchPanel tp = null;
	final HashMap<String, Double> dif = new HashMap<String, Double>();

	public FormuleEditorTouchHandler(TouchPanel tp, FormuleKeyboard kb, FormuleEditor editor)
	{
		this.tp = tp;
		this.kb = kb;
		this.editor = editor;

	}

	@Override
	public void onTouchStart(TouchStartEvent event)
	{

		//NOTE: this is important for android otherwise the move method may not be triggered properly
		event.preventDefault();
		event.stopPropagation();

		try
		{
			kb.setEditor(editor);
			int x = event.touches().get(0).getPageX() - editor.getCanvas().getAbsoluteLeft();
			int y = event.touches().get(0).getPageY() - editor.getCanvas().getAbsoluteTop();

			editor.clearSelection();
			editor.startSelection(x, y);
			editor.endSelection(x, y);

		}
		catch (Exception e)
		{
			//Window.alert("Error: " + e.getMessage());
		}

	}

	@Override
	public void onTouchMove(TouchMoveEvent event)
	{
		//NOTE: this is important for android otherwise the move method may not be triggered properly
		event.preventDefault();
		event.stopPropagation();
		try
		{
			int x = event.touches().get(0).getPageX() - editor.getCanvas().getAbsoluteLeft();
			int y = event.touches().get(0).getPageY() - editor.getCanvas().getAbsoluteTop();
			editor.endSelection(x, y);

		}
		catch (Exception e)
		{
			//Window.alert("Error: " + e.getMessage());
		}

	}

	@Override
	public void onTouchEnd(TouchEndEvent event)
	{
		event.preventDefault();
	}

	@Override
	public void onTouchCanceled(TouchCancelEvent event)
	{

	}

}
