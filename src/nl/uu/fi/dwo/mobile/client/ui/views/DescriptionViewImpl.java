package nl.uu.fi.dwo.mobile.client.ui.views;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import nl.uu.fi.dwo.interaction.client.FormuleClipboardIF;
import nl.uu.fi.dwo.interaction.client.FormuleKeyboardIF;
import nl.uu.fi.dwo.interaction.client.InteractionView;
import nl.uu.fi.dwo.interaction.client.JSONUtilities;
import nl.uu.fi.dwo.interaction.client.LessonMode;
import nl.uu.fi.dwo.interaction.client.OpdrNavIF;
import nl.uu.fi.dwo.interaction.client.Role;
import nl.uu.fi.dwo.interaction.client.event.CBookEvent;
import nl.uu.fi.dwo.interaction.client.event.CBookEventListener;
import nl.uu.fi.dwo.mobile.DWOplayer;
import nl.uu.fi.dwo.mobile.client.ui.views.interactionviews.FormuleEditorWithSteps;
import nl.uu.fi.dwo.mobile.client.ui.views.interactionviews.TekstVakPanel;
import nl.uu.fi.dwo.mobile.utils.TekstBuffer;
import nl.uu.fi.dwo.mobile.utils.VariableCollection;

import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.dom.client.Style.Float;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.HandlerRegistration;

public class DescriptionViewImpl extends XMLView implements DescriptionView, EntryPoint, OpdrNavIF {

	private static final String GET_COURSE_DESCRIPTION = 
			Window.Location.getProtocol() +"//"+ DWOplayer.PARAMETERS.getHost()+"/DWOmAccess/getCourseDescription?c=";
	
	
	private SimplePanel main;
	private Label loading = new Label("Loading...");
	private AnchorView.AnchorContext anchorContext;

	public AnchorView.AnchorContext getAnchorContext() {
		return anchorContext;
	}

	public void setAnchorContext(AnchorView.AnchorContext anchorContext) {
		this.anchorContext = anchorContext;
	}

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
		main.setStylePrimaryName("descriptionView");
		contentPanel = new FlowPanel();
	}

	public DescriptionViewImpl(Object id) {
		this();
		setupModule(id);
	}
	public DescriptionViewImpl(Object id, AnchorView.AnchorContext context) {
		this();
		setAnchorContext(context);
		setupModule(id);
	}
	

	@Override
	public void setupModule(Object id) {
		loading.setText("loading course description " + id);
		main.setWidget(loading);
		String xml = GET_COURSE_DESCRIPTION + id;
		loadJSON(xml);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void setupView(HashMap<String, Object> launchData) {
		super.setupView(launchData);
		contentPanel.clear();
		HashMap<String,Object> opdracht = (HashMap<String, Object>) launchData.get("opdracht_1_1");
		
		contentPanel.getElement().getStyle().setFontSize(font_size, Unit.PX);
		//contentPanel.getElement().getStyle().setPadding(15, Unit.PX);

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
		List<Object> opdrachtGegevens = JSONUtilities.toArrayList( opdracht.get("interactiePanelLaunchData") );
		TekstBuffer tb = new TekstBuffer(varnamen, waarden, getAnchorContext());
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
					((InteractionView) currentObject).setCommunicationRoot(this);
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
			//setObjects(opdrachtObjects, contentPanel);
			setObjects(opdracht, contentPanel, this);
			hoofdPanel.getAsPanel().getElement().getStyle().clearWidth();
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
		//setObjects(opdrachtObjects, tekst);
		setObjects(opdracht, tekst, null);
		contentPanel.add(tekst);
		FormuleEditorWithSteps fews = new FormuleEditorWithSteps(opdracht, false, tb.getVarNamen(), tb.getVarWaarden(), null);

		//kb.setEditor(fews.getEditor());
		//fews.setKeyboard(kb);

		contentPanel.add(fews.getAsPanel());
	}

	@Override
	public void setChanged(boolean fout) {
	}

	@Override
	public FormuleKeyboardIF getKeyboard() {
		return kb;
	}

	@Override
	public FormuleClipboardIF getFormuleClipboard() {
		return cb;
	}

	@Override
	public int getMode() {
		return 0;
	}

	@Override
	public String getLearnerId() {
		return "guest";
	}

	@Override
	public String getLearnerName() {
		return "Guest, Anonymous";
	}

	@Override
	public CssColor getBackground() {
		return CssColor.make("white");
	}

	@Override
	public String getUUID() {
		return "0-0-0";
	}

	@Override
	public LessonMode getLessonMode() {
		return LessonMode.browse;
	}

	@Override
	public Role getRole() {
		return Role.Learner;
	}

	@Override
	public HandlerRegistration addCBookEventListener(String command,
			CBookEventListener listener) {
		return null;
	}

	@Override
	public void fireEvent(CBookEvent event) {
	}

	@Override
	public boolean hasListeners(String command) {
		return false;
	}

	@Override
	public void pause() {
	}

	@Override
	public void unpause() {
	}


}
