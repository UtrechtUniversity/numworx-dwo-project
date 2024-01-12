package nl.uu.fi.dwo.formule.client.formuleholder;

import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import nl.uu.fi.dwo.formule.client.formuleobjects.FormuleRegel;
import nl.uu.fi.dwo.interaction.client.FormuleEditorIF;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.HandlerRegistration;

import fi.wiskopdr.FormuleParser;

/**
 * Displays a formula
 * 
 * @author Danny Hendrix
 * 
 */
public class FormuleViewer extends FormuleHolder implements FormuleEditorIF, IsWidget
{
	private static Logger logger = Logger.getLogger("FormuleViewer");
	public static final int NONE = 0;
	public static final int ALMOSTCORRECT = 1;
	public static final int CORRECT = 2;
	public static final int WRONG = 3;

	private int showResult = NONE;

	Panel sp = null;
	FormuleRegel current;
	Image checkimg;
	
	boolean selectable = true;
	private HandlerRegistration registration;
	

	public FormuleViewer(String formule)
	{
		String currentFormule = formule;
		if (formule.length()>2 && formule.substring(0, 2).equalsIgnoreCase("$f"))
			currentFormule = formule.substring(2, formule.length() - 1);
		this.getMainRegel().insert(currentFormule);
		this.paint();

		sp = createPanel();
		checkimg = new Image(FORMULE_BUNDLE.goedkrul().getSafeUri());
		checkimg.getElement().getStyle().setMarginRight(10, Unit.PX);
		checkimg.setVisible(false);
		sp.add(checkimg);
		sp.add(this.getCanvas());
		registration = (new FormuleEditorTouchHandler(this)).initHandler();
		setCurrentRegel(getMainRegel());
		
	}
	
	//Voor plaatsen van viewers in FormuleEditorWithSteps; checkimg heeft hoogte 20, dus die hoogte moet minimaal worden gegeven.
	public int getHeightWithImage()
	{
		return Math.max(super.getHeight(), 20); 
	}
	
	public FormuleViewer(String formule, String[] randomVarNamen, HashMap randomVarWaarden)
	{
		String currentFormule = formule;

		if (!(currentFormule.length()>2 && currentFormule.substring(0, 2).equalsIgnoreCase("$f")))
			currentFormule = "$f" + currentFormule + "@";
		try
		{
			logger.fine("currentFormule: "+currentFormule);
			currentFormule = FormuleParser.randomizeString(currentFormule, randomVarNamen, randomVarWaarden);
		}
		catch (Exception e)
		{
			logger.log(Level.INFO, "currentFormule: " + currentFormule, e);
		}

		if (currentFormule.substring(0, 2).equalsIgnoreCase("$f"))
			currentFormule = currentFormule.substring(2, currentFormule.length() - 1);

		this.getMainRegel().insert(currentFormule);
		this.paint();

		sp = createPanel();
		checkimg = new Image(FORMULE_BUNDLE.goedkrul().getSafeUri());
		checkimg.setVisible(false);
		sp.add(this.getCanvas());
		sp.add(checkimg); // waarom een ouderwetse onzichtbare krul?
		registration = (new FormuleEditorTouchHandler(this)).initHandler();
		setCurrentRegel(getMainRegel());
	}

	public void showResult(int type)
	{
		this.showResult = type;
		checkimg.setVisible(true);
		switch (type)
		{
		case NONE:
			checkimg.setVisible(false);
			getCanvas().getElement().getStyle().setMarginLeft(23, Unit.PX);
			break;
		case ALMOSTCORRECT:
			checkimg.setUrl(FORMULE_BUNDLE.mw_vinkje_geel().getSafeUri());
			getCanvas().getElement().getStyle().setMarginLeft(0, Unit.PX);
			break;
		case CORRECT:
			checkimg.setUrl(FORMULE_BUNDLE.mw_vinkje_groen().getSafeUri());
			getCanvas().getElement().getStyle().setMarginLeft(0, Unit.PX);
			break;
		case WRONG:
			checkimg.setUrl(FORMULE_BUNDLE.mw_kruisje_rood().getSafeUri());
			getCanvas().getElement().getStyle().setMarginLeft(0, Unit.PX);
			break;
		}
	}
	
	public int getResult()
	{
		return showResult;
	}

//	public void showResult(String formule, boolean strict)
//	{
//		//check formules
//		String useranswer = "$f" + this.toString() + "@";
//
//		if (useranswer.equals("$f@"))
//			useranswer = "$f0@";
//
//		//inserted expresion
//		Expressie answer = FormuleParser.geefExpressie(useranswer);
//		Expressie correctanswer = FormuleParser.geefExpressie(formule);
//
//		if (strict == true)
//		{
//			if (Algebra.zijnGelijk(answer, correctanswer) == false)
//			{
//				showResult(CORRECT);
//				return;
//			}
//			if (Algebra.isGelijkwaardig(answer, correctanswer))
//			{
//				showResult(ALMOSTCORRECT);
//				return;
//			}
//		}
//		else if (Algebra.zijnGelijk(answer, correctanswer) == false)
//		{
//			showResult(CORRECT);
//			return;
//		}
//		showResult(WRONG);
//	}

//	@Override
	public Panel getAsPanel()
	{

		return sp;
	}

	@Override
	public Widget asWidget() {
		return sp;
	}
	
	@Override
	public void clearSelection() {
		if(!selectable) 
			return;
		if(hasSelection())
		{
			current.clearSelection();
			hasSelection = false;
		}
	}
	
	public void setSelection(int selectionStartX, int selectionStartY, int selectionEndX, int selectionEndY)
	{
		this.selectionStartX = selectionStartX;
		this.selectionStartY = selectionStartY;
		FormuleRegel l = this.getMainRegel().selection(selectionStartX, selectionStartY, selectionEndX, selectionEndY);
		hasSelection = l.hasSelection();
		setCurrentRegel(l);
		this.paint();
	}
	
	public void endSelection(int selectionEndX, int selectionEndY)
	{
		if(!selectable)
			return;
		//swap?
		int selectionStartX = this.selectionStartX;
		int selectionStartY = this.selectionStartY;
		//if (Math.abs(selectionStartX - selectionEndX)<4 && Math.abs(selectionStartY - selectionEndY)<4)
		//{	clearSelection();
		//	this.paint();
		//	return;
		//}

		if (selectionEndX < selectionStartX)
		{
			int temp = selectionStartX;
			selectionStartX = selectionEndX;
			selectionEndX = temp;
		}
		if (selectionEndY < selectionStartY)
		{
			int temp = selectionStartY;
			selectionStartY = selectionEndY;
			selectionEndY = temp;
		}

		FormuleRegel l = this.getMainRegel().selection(selectionStartX, selectionStartY, selectionEndX, selectionEndY);
		hasSelection = l.hasSelection();
		setCurrentRegel(l);
		deferredPaintSelection();
	}

	@Override
	public FormuleRegel getCurrentRegel() {
		return current;
	}

	@Override
	public void setCurrentRegel(FormuleRegel formuleRegel) {
		current = formuleRegel;
	}

	
//	@Override
//	public void requestFocus() {
//	}
	
	public void setSelectable(boolean b)
	{
		selectable = b;
	}
	
	public void removeTouchHandler() {
		if ( registration != null) 
			registration.removeHandler();
		registration = null;
	}

	@Override
	public void setCurrentElementRepaint() {
		clearSelection();
		super.setCurrentElementRepaint();
		deferredPaintSelection();
	}
	
}
