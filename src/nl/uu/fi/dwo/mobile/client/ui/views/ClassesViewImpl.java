package nl.uu.fi.dwo.mobile.client.ui.views;

import java.util.List;

import javax.inject.Inject;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.LegacyHandlerWrapper;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.CellList;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ProvidesKey;
import com.google.gwt.view.client.SingleSelectionModel;
import com.google.web.bindery.event.shared.EventBus;

import dagger.Reusable;
import nl.uu.fi.dwo.mobile.client.ui.ActivityComponent;
import nl.uu.fi.dwo.mobile.client.ui.ActivityComponent.Builder;
import nl.uu.fi.dwo.mobile.client.ui.RPCHandler;
import nl.uu.fi.dwo.mobile.client.ui.SelectModuleItem;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClass;

@Reusable
public class ClassesViewImpl extends Composite  implements AnchorContext {

	public class NavCell extends AbstractCell<DomSchoolClass> implements HasValueChangeHandlers<DomSchoolClass>{
		private boolean pointer;
		int x, y;
		final int RADIUS = 20;

		boolean click(String eventType) {
			return "click".equals(eventType) && !pointer || "pointerup".equals(eventType);
		}
		private boolean close(NativeEvent event) {
			int r = Math.abs(x - event.getScreenX()) + Math.abs(y - event.getScreenY());
			return !pointer || r < RADIUS;
		}

		@Override
		public void render(Context context, DomSchoolClass value, SafeHtmlBuilder sb) {
			String clazz = style.classItem();
			sb.appendHtmlConstant("<div class='" + clazz +"'>");
			sb.appendEscaped(value.getSchoolClassName());		
			sb.appendHtmlConstant("</div>");
		}
		public NavCell() {
			super("click", "pointerdown", "pointerup");
		}
		@Override
		public void onBrowserEvent(Context context, Element parent, DomSchoolClass value, NativeEvent event,
				ValueUpdater<DomSchoolClass> valueUpdater) {
		    String eventType = event.getType();
			if("pointerdown".equals(eventType)) {
				pointer = true;
				x = event.getScreenX();
				y = event.getScreenY();
			}
		    if(click(eventType) && close(event)) {
		    	GWT.log(value.getSchoolClassName());
		    	//ev.fireIfNotEqual(this, oldValue, newValue);
		    	ValueChangeEvent.fire(this, value);
		    	return;
		    }
			super.onBrowserEvent(context, parent, value, event, valueUpdater);
		}

		@Override
		public void fireEvent(GwtEvent<?> event) {
			eventBus.fireEventFromSource(event, ClassesViewImpl.this);
		}

		@Override
		public HandlerRegistration addValueChangeHandler(ValueChangeHandler<DomSchoolClass> handler) {
			return new LegacyHandlerWrapper(eventBus.addHandlerToSource(ValueChangeEvent.getType(), ClassesViewImpl.this, handler));
		}

	}
	class ProvideTileKey implements ProvidesKey<DomSchoolClass> {
		
		@Override
		public Object getKey(DomSchoolClass item) {
			if(item != null)
				return item.getId();
			return null;
		}
		
	}
	ProvideTileKey keyprovider = new ProvideTileKey();

	private final EventBus eventBus;
	private final RPCHandler rpc;
	private Builder builder;

	private static ClassesViewImplUiBinder uiBinder = GWT.create(ClassesViewImplUiBinder.class);

	interface ClassesViewImplUiBinder extends UiBinder<Widget, ClassesViewImpl> {
	}

	@Inject ClassesViewImpl(EventBus bus, RPCHandler rpc, ActivityComponent.Builder builder) {
		this.eventBus = bus;
		this.rpc = rpc;
		this.builder = builder;
		CellList.Resources cellResources;
		cellResources = GWT.create(ClassesCellListResources.class);
		cells = new CellList<DomSchoolClass>(new NavCell(),cellResources);
		cells.setSelectionModel(new SingleSelectionModel<DomSchoolClass>(keyprovider));
		cells.setKeyboardSelectionPolicy(com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy.KeyboardSelectionPolicy.DISABLED);
		initWidget(uiBinder.createAndBindUi(this));
		SelectModuleItem item = SelectModuleItem.ROOT;
		title.setText(item.getName());
		//description.setWidget(getLabel(item));
	}

	@UiField(provided=true) CellList<DomSchoolClass> cells; 
	@UiField TreeModuleViewNumworxCss style;
	@UiField HTML title;
	@UiField SimplePanel description;
	@UiField ScrollPanel centerPanel;

	public void setSchoolClasses(List<DomSchoolClass> list) {
		cells.setRowData(list);
		cells.redraw();
	}
	
	public void setActiveSchoolClass(DomSchoolClass sc) {
		cells.getSelectionModel().setSelected(sc, true);
	}
	
	private Widget getLabel(SelectModuleItem item) {
		Widget w;
		String description = item.getDescription();
		if(description.startsWith(DescriptionView.GZIPPREFIX))
		{
			ActivityComponent activity = builder.build();
			w = new DescriptionViewImpl(rpc, item.getID(), (AnchorContext) this, activity).asWidget();
		} else
		if(description.startsWith("<html>")) {
			w = new HTML(description);
			w.setStyleName(style.description());
		}else
		{
			w = new Label(description);
			w.setStyleName(style.description());
		}
		return w;
	}

	@Override
	public void gotoUrl(String href) {
	}


}
