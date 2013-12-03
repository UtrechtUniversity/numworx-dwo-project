package nl.uu.fi.dwo.mobile.client.ui;

import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.TouchEvent;
import com.google.gwt.user.client.Window;

import nl.uu.fi.dwo.formule.client.formuleholder.FormuleEditor;
import nl.uu.fi.dwo.formule.client.formuleholder.FormuleHolder;
import nl.uu.fi.dwo.interaction.client.touch.TouchCancelEvent;
import nl.uu.fi.dwo.interaction.client.touch.TouchEndEvent;
import nl.uu.fi.dwo.interaction.client.touch.TouchHandler;
import nl.uu.fi.dwo.interaction.client.touch.TouchMoveEvent;
import nl.uu.fi.dwo.interaction.client.touch.TouchPanel;
import nl.uu.fi.dwo.interaction.client.touch.TouchStartEvent;

/**
 * 
 * @author Evertson Croes
 * 
 */
public class FormuleEditorTouchHandler implements TouchHandler
{
	protected FormuleKeyboard kb = null;
	protected FormuleHolder editor = null;
	private TouchPanel tp = null;
	final HashMap<String, Double> dif = new HashMap<String, Double>();
	int x,y;
	
	public FormuleEditorTouchHandler(TouchPanel tp, FormuleKeyboard kb, FormuleHolder editor)
	{
		//tp.getElement().getStyle().setBorderWidth(1, Unit.PX);
		//tp.getElement().getStyle().setBorderColor("#ff0000");
		this.tp = tp;
		this.kb = kb;
		this.editor = editor;

	}

	@Override
	public void onTouchStart(TouchStartEvent event)
	{

		event.preventDefault();
		event.stopPropagation();

		try
		{
			editor.requestFocus(kb); // in plaats van: kb.setEditor(editor);
			int x = event.touches().get(0).getPageX() - editor.getCanvas().getAbsoluteLeft();
			int y = event.touches().get(0).getPageY() - editor.getCanvas().getAbsoluteTop();
			if(!TouchStartEvent.isSupported()) y+=8;// vraag me niet waarom dit nodig is

			editor.clearSelection();
			editor.startSelection(x, y);
			editor.endSelection(x, y);
			this.x = x;
			this.y = y;

		}
		catch (Exception e)
		{
			//Window.alert("Error: " + e.getMessage());
			Logger.getLogger("FormuleEditorTouchHandler").log(Level.SEVERE, "onTouchStart: " + e, e);
		}

	}

	@Override
	public void onTouchMove(TouchMoveEvent event)
	{
		//NOTE: this is important for android otherwise the move method may not be triggered properly
		//if(TouchStartEvent.isSupported())
		{
			event.preventDefault();
			event.stopPropagation();
		}
		try
		{
			int x = event.touches().get(0).getPageX() - editor.getCanvas().getAbsoluteLeft();
			int y = event.touches().get(0).getPageY() - editor.getCanvas().getAbsoluteTop();
			if(!TouchStartEvent.isSupported()) y+=8; // vraag me niet waarom dit nodig is
			editor.endSelection(x, y);
			this.x = x; this.y = y;

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
			event.stopPropagation();
			//editor.endSelection(x, y);
			//Logger.getLogger("FormuleEditorTouchHandler").info("selection is " + editor.getSelectionString());
	}

	@Override
	public void onTouchCanceled(TouchCancelEvent event)
	{

	}

}
