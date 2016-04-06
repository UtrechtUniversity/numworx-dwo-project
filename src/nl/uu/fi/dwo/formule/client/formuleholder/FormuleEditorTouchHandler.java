package nl.uu.fi.dwo.formule.client.formuleholder;

import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.googlecode.mgwt.dom.client.event.touch.TouchCancelEvent;
import com.googlecode.mgwt.dom.client.event.touch.TouchEndEvent;
import com.googlecode.mgwt.dom.client.event.touch.TouchHandler;
import com.googlecode.mgwt.dom.client.event.touch.TouchMoveEvent;
//import com.googlecode.mgwt.dom.client.event.touch.TouchStartEvent;


import com.googlecode.mgwt.dom.client.event.touch.TouchStartEvent;

import nl.uu.fi.dwo.interaction.client.FormuleKeyboardIF;

/**
 * 
 * @author Evertson Croes
 * 
 */
public class FormuleEditorTouchHandler implements TouchHandler
{
	protected FormuleHolder editor = null;
	final HashMap<String, Double> dif = new HashMap<String, Double>();
	int x,y;
	boolean soft;
	
	public FormuleEditorTouchHandler(FormuleHolder editor)
	{
		//tp.getElement().getStyle().setBorderWidth(1, Unit.PX);
		//tp.getElement().getStyle().setBorderColor("#ff0000");
		this.editor = editor;

	}

	@Override
	public void onTouchStart(TouchStartEvent event)
	{
		if(isSupported())
		{
			event.preventDefault();
			event.stopPropagation();
		}
		
		try
		{
			editor.requestFocus();
			int x = event.getTouches().get(0).getPageX() - editor.getCanvas().getAbsoluteLeft();
			int y = event.getTouches().get(0).getPageY() - editor.getCanvas().getAbsoluteTop();
			//if(!isSupported()) y+=8;// vraag me niet waarom dit nodig is 
			//21-09-2015: weggehaald, want zorgt dat je voor aanklikken/selecteren te hoog moet klikken.

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

	private static boolean isSupported() {
		return com.google.gwt.event.dom.client.TouchStartEvent.isSupported();
	}

	@Override
	public void onTouchMove(TouchMoveEvent event)
	{
		//NOTE: this is important for android otherwise the move method may not be triggered properly
		//if(TouchStartEvent.isSupported())
		if(isSupported())
		{
			event.preventDefault();
			event.stopPropagation();
		}
		try
		{
			int x = event.getTouches().get(0).getPageX() - editor.getCanvas().getAbsoluteLeft();
			
			int y = event.getTouches().get(0).getPageY() - editor.getCanvas().getAbsoluteTop();
			//01-04-2016: onderstaande was al weggehaald in onTouchStart, dus kan waarschijnlijk hier ook weg. 
			//(in de hoop dat selecteren dan soepeler gaat)
			//if(!isSupported()) y+=8; // vraag me niet waarom dit nodig is
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
		if(isSupported())
		{
			event.preventDefault();
			event.stopPropagation();
		}	
		try
		{
			int x = event.getChangedTouches().get(0).getPageX() - editor.getCanvas().getAbsoluteLeft();
			int y = event.getChangedTouches().get(0).getPageY() - editor.getCanvas().getAbsoluteTop();
			editor.endSelection(x, y);
			this.x = x; this.y = y;
		}
		catch (Exception e)
		{
			//Window.alert("Error: " + e.getMessage());
		}
		
	}

	@Override
	public void onTouchCanceled(TouchCancelEvent event)
	{
		
	}

}
