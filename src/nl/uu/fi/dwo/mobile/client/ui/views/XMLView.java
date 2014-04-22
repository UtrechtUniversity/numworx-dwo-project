package nl.uu.fi.dwo.mobile.client.ui.views;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import nl.uu.fi.dwo.formule.client.formuleholder.FormuleEditorTouchHandler;
import nl.uu.fi.dwo.formule.client.formuleholder.FormuleHolder;
import nl.uu.fi.dwo.formule.client.formuleholder.FormuleViewer;
import nl.uu.fi.dwo.formule.client.formuleobjects.FormuleTeken;
import nl.uu.fi.dwo.interaction.client.FormuleFont;
import nl.uu.fi.dwo.interaction.client.InteractionView;
import nl.uu.fi.dwo.interaction.client.touch.TouchPanel;
import nl.uu.fi.dwo.mobile.client.ui.FormuleKeyboard;
import nl.uu.fi.dwo.mobile.client.ui.views.interactionviews.FormuleEditorWithAnswer;
import nl.uu.fi.dwo.mobile.client.ui.views.interactionviews.FormuleEditorWithSteps;
import nl.uu.fi.dwo.mobile.client.ui.views.interactionviews.TekstVakPanel;
import nl.uu.fi.dwo.mobile.utils.StringCodeToHashMap;

import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.dom.client.Element;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.XMLParser;
/**
 * Common code voor ViewModuleViewImpl en DescriptionViewImpl.
 * @author wim
 *
 */
public abstract class XMLView {

	protected HashMap<String, Object> launchData;
	protected HashMap<String, Object> instellingen;
	protected int font_size = 12;
	protected String[] randomVarNamen = null;
	protected HashMap randomVarWaarden = null;
	protected ArrayList<Object> opdrachtObjects;
	protected boolean newVersion = true;
	protected FormuleKeyboard kb = null;
	private static Logger logger = Logger.getLogger("XMLView");

	protected void setupView(HashMap<String, Object> launchData)
	{
		this.launchData = launchData;
		Object imagemap = launchData.get("$IMAGE$MAP$");
		ImageView.setMap((Map<String, Object>) imagemap);
		
		if (launchData.get("instellingen") != null)
			instellingen = (HashMap<String, Object>) launchData.get("instellingen");
		if (instellingen.get("fontSize") != null)
			font_size = ((Number) instellingen.get("fontSize")).intValue();

		boolean maalTeken = (Boolean) instellingen.get("maalTeken");
		FormuleTeken.zetMaalTeken(maalTeken);
		
		String fontName = (String) instellingen.get("fontName");
		FormuleFont.zetDefaultFont(fontName);
		
		boolean formTimes = (Boolean) instellingen.get("formTimes");
		FormuleFont.zetFormTimes(formTimes);


	}

	protected void loadXML(String xmlPath) {
		RequestBuilder.Method method = RequestBuilder.GET;
		String url = xmlPath;
		RequestBuilder rb = new RequestBuilder(method, url);
		try
		{
			rb.sendRequest(null, new RequestCallback()
			{
	
				@Override
				public void onResponseReceived(Request request, Response response)
				{
					String responseText = response.getText();
					logger.info("Status: " + response.getStatusCode() + " " + response.getStatusText());
					logger.info(response.getHeadersAsString());
					logger.info("Data: " + responseText.substring(0, Math.min(300, responseText.length()) ));
					if (!responseText.isEmpty())
					{
						Document dom = XMLParser.parse(responseText);
//						if(dom == null) 
//						{
//						}
						StringCodeToHashMap sc = new StringCodeToHashMap();
						launchData = sc.decodeStringToHashMap(dom);
						setupView(launchData);
					} else {
						logger.severe("response empty");
					}
	
				}
	
				@Override
				public void onError(Request request, Throwable exception)
				{
					Window.alert("error");
				}
			});
	
		}
		catch (RequestException e)
		{
			RootPanel.get().add(new Label("cannot load xml: " + e.getMessage()));
		}
	}

	public void setObjects(ArrayList<Object> opdrachtObjects, Panel destination) {
	
		for (int i = 0; i < opdrachtObjects.size(); i++)
		{
	
			Object currentObject = opdrachtObjects.get(i);
			if (currentObject instanceof String)
			{
				Element element = DOM.createSpan();
				element.setInnerHTML((String) currentObject);
				//element.getElement().getStyle().setFloat(Float.LEFT);
				destination.getElement().appendChild(element);
	
				if (opdrachtObjects.size() > i + 1 && opdrachtObjects.get(i + 1) instanceof String)
					destination.getElement().appendChild(DOM.createElement("br"));
				//destination.add(new HTML((String) currentObject));
				//destination.getElement().setInnerHTML(destination.getElement().getInnerHTML() + ((String) currentObject));
				//element.setInnerHTML((String) currentObject);
				//element.getElement().getStyle().setPaddingBottom(5, Unit.PX);
				//element.getElement().getStyle().setPaddingTop(5, Unit.PX);
	
			}
			else if (currentObject instanceof FormuleEditorWithAnswer)
			{
				((FormuleEditorWithAnswer) currentObject).setFont(FormuleFont.createFromFontSize(font_size));
				int asHoogte = ((FormuleEditorWithAnswer) currentObject).getMainRegel().getAsHoogte();
				int hoogte = ((FormuleEditorWithAnswer) currentObject).getMainRegel().getHeight();
				Panel a = getPanelElement((FormuleEditorWithAnswer) currentObject);
				//((FormuleEditorWithAnswer) currentObject).getMainRegel().getCanvas().getElement().getStyle().setMarginBottom(-3, Unit.PX);
				//a.getElement().getStyle().setMarginBottom(-4, Unit.PX);
				a.getElement().getStyle().setProperty("display", "inline-block");
				a.getElement().getStyle().setProperty("position", "relative");
				a.getElement().getStyle().setProperty("top", (hoogte - asHoogte - Math.rint(font_size * 0.33) - 2) + "px");
				kb.setEditor((FormuleEditorWithAnswer) currentObject);
				destination.add(a);
			}
			else if (currentObject instanceof FormuleViewer)
			{
				((FormuleViewer) currentObject).setFont(FormuleFont.createFromFontSize(font_size));
				((FormuleViewer) currentObject).setColor(CssColor.make(0, 0, 0));
				int asHoogte = ((FormuleViewer) currentObject).getMainRegel().getAsHoogte();
				int hoogte = ((FormuleViewer) currentObject).getMainRegel().getHeight();
				Panel a = ((FormuleViewer) currentObject).getAsPanel();
				a.getElement().getStyle().setProperty("display", "inline-block");
				a.getElement().getStyle().setProperty("position", "relative");
				a.getElement().getStyle().setProperty("top", (hoogte - asHoogte - Math.rint(font_size * 0.33)) + "px");
	
				destination.add(a);
	
			}
			else if (currentObject instanceof FormuleEditorWithSteps)
			{
				Widget a = ((InteractionView) currentObject).asWidget();
				//a.getElement().getStyle().setFloat(Float.LEFT);
				((FormuleEditorWithSteps) currentObject).getEditor().requestFocus();
				a.getElement().getStyle().setProperty("display", "inline-block");
				a.getElement().getStyle().setProperty("verticalAlign", "top");
	
				destination.add(a);
			}
	
			else if (currentObject instanceof TekstVakPanel)
			{
				Widget a = ((InteractionView) currentObject).asWidget();
				//a.getElement().getStyle().setFloat(Float.LEFT);
				a.getElement().getStyle().setProperty("display", "inline-block");
				a.getElement().getStyle().setProperty("verticalAlign", "top");
	
				destination.add(a);
			}
			// all big interaction views
			else if (currentObject instanceof InteractionView)
			{
				Widget a = ((InteractionView) currentObject).asWidget();
				//a.getElement().getStyle().setFloat(Float.LEFT);
				a.getElement().getStyle().setProperty("display", "inline-block");
				a.getElement().getStyle().setProperty("verticalAlign", (-font_size * 0.45) + "px");
				//a.getElement().getStyle().setProperty("position", "relative");
				//a.getElement().getStyle().setProperty("top", (-font_size*0.1)+"px");
	
				destination.add(a);
			}
			else if (currentObject instanceof ImageView)
			{
				ImageView iv = (ImageView) currentObject;
				Widget w = iv.getImage();
				destination.add(w);
			}
	
		}
	
	}

	public Panel getPanelElement(final FormuleHolder editor) {
		FlowPanel fp = new FlowPanel();
		editor.paint();
	
		final Panel p = editor.getAsPanel();
	
		if (p instanceof TouchPanel)
		{
			TouchPanel tp = (TouchPanel) p;
			this.addFormulePanelListeners(tp, editor);
		}
	
		fp.add(p);
		return p;
	}

	private void addFormulePanelListeners(final TouchPanel tp, final FormuleHolder editor) {
		tp.addTouchHandler(new FormuleEditorTouchHandler(editor));
	}
}
