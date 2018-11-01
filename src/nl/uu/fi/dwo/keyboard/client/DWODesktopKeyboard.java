/**
 * 
 */
package nl.uu.fi.dwo.keyboard.client;

import nl.uu.fi.dwo.interaction.client.FormuleEditorIF;
import nl.uu.fi.dwo.interaction.client.keyboard.FocusOnTouch;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Style.TextAlign;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author peterboon
 *
 */
public class DWODesktopKeyboard extends AbstractKeyboard {
	
	private static int HEIGHT = 90;
	/**
	 * Dialoog om de dimensie van vector te kiezen.
	 */
	private DialogBox vectorDimensionDialog;

	/**
	 * Dialoog om de dimensie van matrix te kiezen.
	 */
	private DialogBox matrixDimensionDialog;
	private ListBox rijBox;
	private ListBox kolomBox;

	int getKeyboardHeight() {
		return HEIGHT;
	}

	private static TabletKeyboardUiBinder uiBinder = GWT
			.create(TabletKeyboardUiBinder.class);

	interface TabletKeyboardUiBinder extends UiBinder<Widget, DWODesktopKeyboard> {
	}
	

	/**
	 * Because this class has a default constructor, it can
	 * be used as a binder template. In other words, it can be used in other
	 * *.ui.xml files as follows:
	 * &lt;ui:UiBinder xmlns:ui="urn:ui:com.google.gwt.uibinder"
	 *   xmlns:g="urn:import:**user's package**"&gt;
	 *  &lt;g:**UserClassName**&gt;Hello!&lt;/g:**UserClassName&gt;
	 * &lt;/ui:UiBinder&gt;
	 * Note that depending on the widget that is used, it may be necessary to
	 * implement HasHTML instead of HasText.
	 */
	public DWODesktopKeyboard() {
		initWidget(uiBinder.createAndBindUi(this));
		initVectorMatrixMenus();
		setPixelSize(-1, HEIGHT);
	}
	
	private void initVectorMatrixMenus()
	{
		initVectorMenu();
		initMatrixMenu();
	}

	private void initMatrixMenu()
	{
		FlexTable grid = new FlexTable();
		grid.getFlexCellFormatter().setColSpan(1, 0, 3);
		grid.getFlexCellFormatter().setAlignment(1, 0, HasHorizontalAlignment.ALIGN_CENTER, HasVerticalAlignment.ALIGN_MIDDLE);
		matrixDimensionDialog = new DialogBox(true);
		rijBox = new ListBox();
		initListBox(rijBox);
		rijBox.getElement().getStyle().setWidth(30, Unit.PX);
		Label keerLabel = new Label("x");
		kolomBox = new ListBox();
		initListBox(kolomBox);
		kolomBox.getElement().getStyle().setWidth(30, Unit.PX);
		
		ClickHandler handler = new ClickHandler() {
			public void onClick(ClickEvent event)
			{
				processMatrixDimension(Integer.parseInt(rijBox.getSelectedItemText()), 
					Integer.parseInt(kolomBox.getSelectedItemText()));
			}
		};
		PushButton klaarButton = new PushButton("OK", handler);
		klaarButton.getElement().getStyle().setWidth(30, Unit.PX);
		klaarButton.getElement().getStyle().setTextAlign(TextAlign.CENTER);
		keerLabel.getElement().getStyle().setTextAlign(TextAlign.CENTER);
		
		grid.setWidget(0, 0, rijBox);
		grid.setWidget(0, 1, keerLabel);
		grid.setWidget(0, 2, kolomBox);
		grid.setWidget(1, 0, klaarButton);
		matrixDimensionDialog.add(grid);
	}

	/**
	 * Matrix is max 6 x 6.
	 * 
	 * @param list
	 */
	private void initListBox(ListBox list)
	{
		for (int i = 1; i < 7; i++)
		{
			list.addItem("" + i);
		}
		
		list.setSelectedIndex(1);
		list.setVisibleItemCount(list.getItemCount());

	}

	private void initVectorMenu()
	{
		// menu om de dimensie te kiezen
		MenuBar vectorDimensionOptions = new MenuBar(true);
		MenuItem dimensie2 = new MenuItem(new SafeHtmlBuilder().appendEscaped("2").toSafeHtml());
		MenuItem dimensie3 = new MenuItem(new SafeHtmlBuilder().appendEscaped("3").toSafeHtml());
		MenuItem dimensie4 = new MenuItem(new SafeHtmlBuilder().appendEscaped("4").toSafeHtml());
		MenuItem dimensie5 = new MenuItem(new SafeHtmlBuilder().appendEscaped("5").toSafeHtml());
		MenuItem dimensie6 = new MenuItem(new SafeHtmlBuilder().appendEscaped("6").toSafeHtml());
		vectorDimensionOptions.addItem(dimensie2);
		vectorDimensionOptions.addItem(dimensie3);
		vectorDimensionOptions.addItem(dimensie4);
		vectorDimensionOptions.addItem(dimensie5);
		vectorDimensionOptions.addItem(dimensie6);
		dimensie2.setScheduledCommand(new ScheduledCommand()
		{
			public void execute()
			{
				processVectorDimension(2);
			}
		});
		dimensie3.setScheduledCommand(new ScheduledCommand()
		{
			public void execute()
			{
				processVectorDimension(3);
			}
		});
		dimensie4.setScheduledCommand(new ScheduledCommand()
		{
			public void execute()
			{
				processVectorDimension(4);
			}
		});
		dimensie5.setScheduledCommand(new ScheduledCommand()
		{
			public void execute()
			{
				processVectorDimension(5);
			}
		});
		dimensie6.setScheduledCommand(new ScheduledCommand()
		{
			public void execute()
			{
				processVectorDimension(6);
			}
		});
		
		vectorDimensionDialog = new DialogBox(true);
		vectorDimensionDialog.add(vectorDimensionOptions);
	}

	@Override
	public void setEditor(FormuleEditorIF formuleEditor) {
		super.setEditor(formuleEditor);
	}

	@UiField(provided=true)
	DWOkeyboardBundle resources = DWOTabletKeyboardFactory.resources;
	
	@UiField
	FKey t3_1,t3_2,t3_3,t3_4, t3_5,t3_6,t3_7,t3_8, t3_9,t3_10,t3_11,t3_12, t3_13,t3_14, t3_15, t3_16;//,t3_15;
	@UiField
	FKey t4_1,t4_2,t4_3,t4_4, t4_5,t4_6,t4_7,t4_8, t4_9,t4_10,t4_11,t4_12, t4_13,t4_14, t4_15;//,t4_15;


	@UiHandler("t3_1")
	void onT3_1(ClickEvent e)
	{
		getEditor().wortel();
	}

	@UiHandler("t3_2") void onT3_2(ClickEvent e) {getEditor().macht();}
	@UiHandler("t3_3") void onT3_3(ClickEvent e) {getEditor().kwadraat();}
	@UiHandler("t3_4") void onT3_4(ClickEvent e) {getEditor().breuk();}
	@UiHandler("t3_5") void onT3_5(ClickEvent e) {getEditor().haakjes();}
	@UiHandler("t3_6") void onT3_6(ClickEvent e) {getEditor().ndewortel();}
	@UiHandler("t3_7") void onT3_7(ClickEvent e) {getEditor().integraal();}
	@UiHandler("t3_8") void onT3_8(ClickEvent e) {getEditor().prv();}
	@UiHandler("t3_9") void onT3_9(ClickEvent e) {getEditor().ndelog();}
	@UiHandler("t3_10") void onT3_10(ClickEvent e) {getEditor().abs();}
	@UiHandler("t3_11") void onT3_11(ClickEvent e) {getEditor().subscript();}
	@UiHandler("t3_12") void onT3_12(ClickEvent e) {getEditor().bin();}
	@UiHandler("t3_13") void onT3_13(ClickEvent e) {getEditor().conjug();}
	@UiHandler("t3_14") void onT3_14(ClickEvent e) {switchGreek();}

	@UiHandler("t4_1") void onT4_1(ClickEvent e) {getEditor().diff();}
	@UiHandler("t4_2") void onT4_2(ClickEvent e) {getEditor().limiet0();}
	@UiHandler("t4_3") void onT4_3(ClickEvent e) {getEditor().limiet1();}
	@UiHandler("t4_4") void onT4_4(ClickEvent e) {getEditor().limiet2();}
	@UiHandler("t4_6") void onT4_6(ClickEvent e) {getEditor().primitieve();}
	
	@UiHandler("t4_7")
	void onT4_7(ClickEvent e)
	{
		getEditor().sigma();
	}
	
	@UiHandler("t3_15") 
	void onT3_15(ClickEvent e)
	{
		// toon dimensiekeuze
		vectorDimensionDialog.showRelativeTo(this.t3_15);
	}
	
	@UiHandler("t4_15")
	void onT4_15(ClickEvent e)
	{
		// toon dimensiekeuze
		matrixDimensionDialog.showRelativeTo(this.t4_15);
	}

	@UiHandler("t3_16")
	void onT3_16(ClickEvent e)
	{
		getEditor().vectornotatie();
	}

	@UiHandler({"t4_5", "t4_8", "t4_9", "t4_10", "t4_11", "t4_12"} )
	void insert(ClickEvent e) {
		doInsert(e);
	}

	@UiHandler("t4_13") void onT4_13(ClickEvent e) {getEditor().diff_partial();};
	@UiHandler("t4_14") void onT4_14(ClickEvent e) {blur();}
//	@UiHandler("t4_15") void onT4_15(ClickEvent e) {getEditor().insert('∞');}

	@Override
	public void blur() {
		getDelegate().blur();
	}

	@Override
	void switchABC() {
		getDelegate().switchABC();
	}

	@Override
	void switchHand() {
		getDelegate().switchHand();
	}

	@Override
	void switchGreek() {
		getDelegate().switchGreek();
	}

	AbstractKeyboard init() {
		return this;
	}

	void processVectorDimension(int aantalRijen)
	{
		FocusOnTouch.focus();
		getEditor().vector(aantalRijen);
		vectorDimensionDialog.hide();
		t3_15.removeStyleName("hover");	
	}
	
	protected void processMatrixDimension(int aantalRijen, int aantalKolommen)
	{
		FocusOnTouch.focus();
		getEditor().matrix(aantalRijen, aantalKolommen);
		matrixDimensionDialog.hide();
		t4_15.removeStyleName("hover");	
	}

	@Override
	public void setPremium(boolean premium) {
		if(!premium) {
			disableKey(t3_15);
			disableKey(t3_16);
			disableKey(t4_15);
		}
	}

}
