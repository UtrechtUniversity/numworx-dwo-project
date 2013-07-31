package nl.uu.fi.dwo.mobile.client.ui.views;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import nl.uu.fi.dwo.interaction.client.InteractionView;
import nl.uu.fi.dwo.mobile.client.ui.views.interactionviews.FormuleEditorWithSteps;
import nl.uu.fi.dwo.mobile.client.ui.views.interactionviews.TekstVakPanel;
import nl.uu.fi.dwo.mobile.utils.TekstBuffer;
import nl.uu.fi.dwo.mobile.utils.VariableCollection;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.dom.client.Style.Float;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

public class DescriptionViewImpl extends XMLView implements DescriptionView, EntryPoint {

	private static final String GET_COURSE_DESCRIPTION = 
			"http://ws-dev.fisme.science.uu.nl:8080/DWOmAccess/getCourseDescription?c=";
	
	
	private SimplePanel main;
	private FlowPanel   contentPanel;
	private Label loading = new Label("Loading...");


	private FlowPanel tekst;
	
	@Override
	public Widget asWidget() {
		return main;
	}

	@Override
	public void onModuleLoad() {
		int courseID = 13033; // lessenseries/onderbouw/kwadratische vergelijkingen
		setupModule(courseID);		
		RootPanel.get().add(asWidget());
	}

	public DescriptionViewImpl() {
		super();
		main = new SimplePanel();
	}

	public DescriptionViewImpl(int id) {
		this();
		setupModule(id);
	}

	@Override
	public void setupModule(int id) {
		loading.setText("loading course description " + id);
		main.setWidget(loading);
		String xml = GET_COURSE_DESCRIPTION + id;
		loadXML(xml);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void setupView(HashMap<String, Object> launchData) {
		super.setupView(launchData);
		contentPanel = new FlowPanel();
		HashMap<String,Object> opdracht = (HashMap<String, Object>) launchData.get("opdracht_1_1");
		
		contentPanel.getElement().getStyle().setFontSize(font_size, Unit.PX);
		contentPanel.getElement().getStyle().setPadding(15, Unit.PX);

		zetOpdracht(opdracht);
		main.setWidget(contentPanel);
		
	}

	public void zetOpdracht(HashMap<String, Object> opdracht)
	{
		String randVarString = "";
		randVarString = (String) opdracht.get("randVarString");
		VariableCollection vc = new VariableCollection();
		boolean wellSet = vc.setVariables(randVarString);

		String[] varnamen = null;
		HashMap waarden = null;
		{
			try
			{
				varnamen = vc.getVariableNames();
				waarden = vc.getRandomValues();
			}
			catch (Exception ex)
			{
				wellSet = false;
			}
		}

		this.randomVarNamen = varnamen;
		this.randomVarWaarden = waarden;

		opdrachtObjects = new ArrayList<Object>();
		ArrayList<Object> opdrachtGegevens = (ArrayList<Object>) opdracht.get("interactiePanelLaunchData");
		TekstBuffer tb = new TekstBuffer(varnamen, waarden);
		newVersion = !(Boolean) opdracht.get("hasAntwoordVak");
		//New editor version
		if (opdrachtGegevens != null || newVersion)
		{
			if ((Boolean) opdracht.get("hasTitle"))
			{
				SimplePanel title = new SimplePanel();
				title.getElement().setInnerHTML((String) opdracht.get("titel") + "<br />");
				title.getElement().getStyle().setProperty("fontWeight", "bold");
				title.getElement().getStyle().setFontSize(font_size * 1.33, Unit.PX);
				title.getElement().getStyle().setPaddingBottom(5, Unit.PX);
				title.getElement().getStyle().setPaddingTop(5, Unit.PX);
				//title.getElement().getStyle().setFloat(Float.LEFT);
				contentPanel.add(title);
			}
			opdrachtObjects = tb.convertTekst(opdracht);
			int aantalVakken = 0;
			for (int i = 0; i < opdrachtObjects.size(); i++)
			{
				Object currentObject = opdrachtObjects.get(i);
				if (currentObject instanceof InteractionView)
					((InteractionView) currentObject).setCommunicationRoot(null);
				if (currentObject instanceof TekstVakPanel)
				{
					aantalVakken++;
					Object launchData = opdrachtGegevens.get(aantalVakken + 4); // FIXME Hier ook een +5-1 Wim
					HashMap<String, Object> launchState = (HashMap<String, Object>) ((HashMap<String, Object>) launchData).get("interactiePanelLaunchState");
					((TekstVakPanel) currentObject).zetInstellingen(instellingen);
					((TekstVakPanel) currentObject).setKeyboard(kb);
					((TekstVakPanel) currentObject).zetOpdracht(launchState);
				}
			}
			setObjects(opdrachtObjects, contentPanel);
		}
		else if (!newVersion)
		{ //Old editor version 
			if (opdrachtGegevens != null && opdrachtGegevens.size() == 1)
			{
				HashMap<String, Object> ips = (HashMap<String, Object>) opdrachtGegevens.get(0);
				HashMap<String, Object> state = (HashMap<String, Object>) ips.get("interactiePanelLaunchState");
				opdracht.put("antwoordString", state.get("antwoordString"));
			}

			setupOldVersion(opdracht, tb);
		}
	}
	//Sets up a FormuleEditorWithSteps for each assignment
	private void setupOldVersion(HashMap<String, Object> opdracht, TekstBuffer tb)
	{
		//ArrayList<Object> opdrachtObjects;
		tekst = new FlowPanel();
		tekst.getElement().getStyle().setWidth((Integer) opdracht.get("scheidingX") / 8, Unit.PCT);
		tekst.getElement().getStyle().setFloat(Float.LEFT);
		tekst.getElement().getStyle().setPadding(5, Unit.PX);
		SimplePanel title = new SimplePanel();
		title.getElement().setInnerText((String) opdracht.get("titel"));
		title.getElement().getStyle().setProperty("fontWeight", "bold");
		title.getElement().getStyle().setFontSize(font_size * 2, Unit.PX);
		title.getElement().getStyle().setPaddingBottom(5, Unit.PX);
		title.getElement().getStyle().setPaddingTop(5, Unit.PX);
		tekst.add(title);
		opdrachtObjects = tb.convertTekst(opdracht);
		setObjects(opdrachtObjects, tekst);
		contentPanel.add(tekst);
		FormuleEditorWithSteps fews = new FormuleEditorWithSteps(opdracht, false, tb.getVarNamen(), tb.getVarWaarden());

		//kb.setEditor(fews.getEditor());
		//fews.setKeyboard(kb);

		contentPanel.add(fews.getAsPanel());
	}


}
