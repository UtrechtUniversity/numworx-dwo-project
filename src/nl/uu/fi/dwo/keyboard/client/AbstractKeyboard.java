package nl.uu.fi.dwo.keyboard.client;

import nl.uu.fi.dwo.interaction.client.FormuleEditorIF;
import nl.uu.fi.dwo.interaction.client.FormuleKeyboardIF;
import nl.uu.fi.dwo.interaction.client.keyboard.AbstractEditor;
import nl.uu.fi.dwo.interaction.client.keyboard.EnterType;
import nl.uu.fi.dwo.interaction.client.keyboard.FocusOnTouch;

import java.util.function.Consumer;

import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Style.TextAlign;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.DataResource;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.ResizeComposite;

public abstract class AbstractKeyboard extends Composite implements FormuleKeyboardIF, RequiresResize {

	public interface HasHeight {
		void setHeight(int px);
	}
	
	
	protected static final int DEFAULT = 0;
	FormuleEditorIF formuleEditor = AbstractEditor.NULL;
	AbstractKeyboard delegate;
	/**
	 * Dialoog om de dimensie van vector te kiezen.
	 */
	protected DialogBox vectorDimensionDialog;
	/**
	 * Dialoog om de dimensie van matrix te kiezen.
	 */
	protected DialogBox matrixDimensionDialog;
    /**
     * Dialoog om de dimensie van stelsel te kiezen.
     */
    protected DialogBox stelselDimensionDialog;
	protected ListBox rijBox;
	protected ListBox kolomBox;

	AbstractKeyboard getDelegate() {
		return delegate;
	}

	void setDelegate(AbstractKeyboard delegate) {
		this.delegate = delegate;
	}

	AbstractKeyboard() {
	}

	public FormuleEditorIF getEditor() {
		return formuleEditor;
	}

	public void setEditor(FormuleEditorIF formuleEditor) {
		setEditor0(formuleEditor);
	}

	private void setEditor0(FormuleEditorIF formuleEditor) {
		if(formuleEditor == null) formuleEditor = AbstractEditor.NULL;
		this.formuleEditor = formuleEditor;
	}

	protected void disableKey(FKey key) {
		key.setHTML("");key.addStyleName("disabled");
	}

	@Override
	public void backspace() {
		getEditor().removeCurrentElement();
		
	}

	@Override
	public void delete() {
		getEditor().removeNextElement();
		
	}

	@Override
	public void enter() {
		getEditor().enter();
		
	}

	@Override
	public void focus() {
		this.setVisible(true);
		
	}

	@Override
	public void softFocus() {
	}

	public void blur() {
		this.setVisible(false);
		getEditor().setCurrentElementRepaint();
	}

	void switchABC() {
	}

	void switchGreek() {
	}
	
	void switch123() {	
	}
	
	void switchHand() {
	}

	void switchUpper() {
	}
	
	void switchLower() {
	}

	public void setScrollPanel(HasHeight w, int h) {
	}

	public void setKeyboard(int nr) {
	}

	public void setWriteMathSet(int nr) {
	}

	public void setPremium(boolean premium) {
		
	}
	
	boolean isPremium() {
		return false;
	}
	
	final protected void setActiveEditor(FormuleEditorIF formuleEditor) {
		FormuleEditorIF old = getEditor();
		setEditor0(formuleEditor);
		if(old != getEditor())
		{
			old.setFont(old.getDefaultFont());
			old.setCurrentElementRepaint();
		}
	}

	public abstract int getKeyboardHeight();

	protected void doInsert(ClickEvent e) {
		Object s = e.getSource(); // HTML mischien getElement en dan DOM methoden?
		String string = s.toString();
		int i = string.indexOf('>');
		int j = string.indexOf('<', i);
		string = string.substring(i+1, j);
		getEditor().insert(string);
	}
	
	public void functionKey(int code) {
		FormuleEditorIF editor = getEditor();
		switch(code) {
		case  1: editor.wortel();   break;
		case  2: editor.macht();    break;
		case  3: editor.kwadraat(); break;
		case  4: editor.breuk();    break;
		case  5: editor.haakjes();  break;
		case  6: editor.ndewortel();break;
		case  7: editor.integraal();break;
		case  8: editor.prv();		break;
		case  9: editor.ndelog();    break;
		case 10: editor.abs();       break;
		case 11: editor.subscript(); break;
		case 12: editor.bin();       break;
		default:
		}
	}
	
	void close() {}
	
	public void setEnterType(EnterType type) {
	}

  void setEnterImage(DataResource resource) { 
  }

protected void initMatrixMenu(ClickHandler clickHandler) {
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
	
	PushButton klaarButton = new PushButton("OK", clickHandler);
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
void initListBox(ListBox list) {
	for (int i = 1; i < 7; i++)
	{
		list.addItem("" + i);
	}
	
	list.setSelectedIndex(1);
	list.setVisibleItemCount(list.getItemCount());

}

protected void processVectorDimension(int aantalRijen)
{
    FocusOnTouch.focus();
    getEditor().vector(aantalRijen);
    vectorDimensionDialog.hide();
}

protected void processStelselDimension(int aantalRijen)
{
    FocusOnTouch.focus();
    getEditor().stelsel(aantalRijen);
    stelselDimensionDialog.hide();
}

protected void initVectorMenu(Consumer<Integer> consume)
{
    // menu om de dimensie te kiezen
    MenuBar vectorDimensionOptions = getDimensionOptions(consume);
    
    vectorDimensionDialog = new DialogBox(true);
    vectorDimensionDialog.add(vectorDimensionOptions);
}

protected void initStelselMenu(Consumer<Integer> consume)
{
    // menu om de dimensie te kiezen
    MenuBar stelselDimensionOptions = getDimensionOptions(consume);
    
    stelselDimensionDialog = new DialogBox(true);
    stelselDimensionDialog.add(stelselDimensionOptions);
}

private MenuBar getDimensionOptions(Consumer<Integer> consume)
{
    MenuBar options = new MenuBar(true);
    
    MenuItem dimensie2 = new MenuItem(new SafeHtmlBuilder().appendEscaped("2").toSafeHtml());
    MenuItem dimensie3 = new MenuItem(new SafeHtmlBuilder().appendEscaped("3").toSafeHtml());
    MenuItem dimensie4 = new MenuItem(new SafeHtmlBuilder().appendEscaped("4").toSafeHtml());
    MenuItem dimensie5 = new MenuItem(new SafeHtmlBuilder().appendEscaped("5").toSafeHtml());
    MenuItem dimensie6 = new MenuItem(new SafeHtmlBuilder().appendEscaped("6").toSafeHtml());
    options.addItem(dimensie2);
    options.addItem(dimensie3);
    options.addItem(dimensie4);
    options.addItem(dimensie5);
    options.addItem(dimensie6);
    dimensie2.setScheduledCommand(new ScheduledCommand()
    {
        public void execute()
        {
            consume.accept(2);
        }
    });
    dimensie3.setScheduledCommand(new ScheduledCommand()
    {
        public void execute()
        {
            consume.accept(3);
        }
    });
    dimensie4.setScheduledCommand(new ScheduledCommand()
    {
        public void execute()
        {
            consume.accept(4);
        }
    });
    dimensie5.setScheduledCommand(new ScheduledCommand()
    {
        public void execute()
        {
            consume.accept(5);
        }
    });
    dimensie6.setScheduledCommand(new ScheduledCommand()
    {
        public void execute()
        {
            consume.accept(6);
        }
    });
    
    return options;
 }

protected void processMatrixDimension(int aantalRijen, int aantalKolommen) {
	FocusOnTouch.focus();
	getEditor().matrix(aantalRijen, aantalKolommen);
	matrixDimensionDialog.hide();
}

	public void setSoortKeyboard(int soort) {
		
	}

	@Override
	public void onResize() {
	}
	
}
