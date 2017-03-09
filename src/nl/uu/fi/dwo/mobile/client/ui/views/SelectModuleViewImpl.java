package nl.uu.fi.dwo.mobile.client.ui.views;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.osgi.util.function.Function;
import org.osgi.util.promise.Promise;
import org.osgi.util.promise.Success;

import nl.uu.fi.dwo.mobile.DWOplayer;
import nl.uu.fi.dwo.mobile.client.ui.SelectModuleCell;
import nl.uu.fi.dwo.mobile.client.ui.SelectModuleItem;
import nl.uu.fi.dwo.mobile.client.ui.SelectModuleItemHolder;
import nl.uu.fi.dwo.mobile.client.ui.views.AnchorView.AnchorContext;
import nl.uu.fi.dwo.rest.dom.entities.DomScoContext;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.HandlerRegistration;
import com.googlecode.mgwt.dom.client.event.tap.HasTapHandlers;
import com.googlecode.mgwt.ui.client.MGWT;
import com.googlecode.mgwt.ui.client.widget.CellList;
import com.googlecode.mgwt.ui.client.widget.HeaderButton;
import com.googlecode.mgwt.ui.client.widget.HeaderPanel;
import com.googlecode.mgwt.ui.client.widget.celllist.CellSelectedEvent;
import com.googlecode.mgwt.ui.client.widget.celllist.CellSelectedHandler;
import com.googlecode.mgwt.ui.client.widget.celllist.HasCellSelectedHandler;
import fi.wiskopdr.text.TextConstants;

/**
 * 
 * @author Danny Hendrix
 * 
 */
public class SelectModuleViewImpl extends Composite implements SelectModuleView, AnchorContext, HasCellSelectedHandler
{
	
//	class GetScosCallback implements AsyncCallback<List<Map<String,Object>>> {
//
//		private SelectModuleItem parent;
//		
//		public GetScosCallback(SelectModuleItem item) {
//			parent = item;
//		}
//
//		@Override
//		public void onFailure(Throwable caught) {
//			Window.alert(caught.toString());
//		}
//
//		@Override
//		public void onSuccess(List<Map<String,Object>> result) {
//			ArrayList<SelectModuleItem> items = new ArrayList<SelectModuleItem>(result.size());
//			for (Iterator<Map<String, Object>> iterator = result.iterator(); iterator.hasNext();) {
//				Map<String, Object> map = (Map<String, Object>) iterator.next();
//				SelectModuleItem item = new SelectModuleItem(map, SelectModuleItem.Type.SCO);
//				item.setParent(parent);
//				items.add(item);
//				SelectModuleItemHolder.insert(item);
//			}
//			parent.setChildren(items);
//			render(items, parent.showChildren());
//		}
//		
//	};


	@UiField (provided=true) CellList<SelectModuleItem> list;
	@UiField (provided=true) TextConstants rb;
	@UiField HeaderButton backbutton;
	private HeaderButton logoutbutton;
	private List<SelectModuleItem> items;
	@UiField HeaderPanel header;
	@UiField SimplePanel description;
	@UiField ScrollPanel outer;
	private HasTapHandlers taphandler;

	public HasTapHandlers getBackBtn() {
		return taphandler;
	}
	
	private static SelectModuleViewImplUiBinder uiBinder = GWT
			.create(SelectModuleViewImplUiBinder.class);

	interface SelectModuleViewImplUiBinder extends
			UiBinder<Widget, SelectModuleViewImpl> {
	}
	
	public SelectModuleViewImpl()
	{	rb = fi.wiskopdr.text.Text.constants;
		list = new CellList<SelectModuleItem>(new SelectModuleCell());
		list.addStyleName(DWOplayer.PARAMETERS.navigationcss().bodyText());
		initWidget(uiBinder.createAndBindUi(this));
// fix height for android, header is 50 pixels + 2 px bottom margin FIXME compile time?
		if(MGWT.getOsDetection().isAndroid())
			outer.getElement().getStyle().setTop(52, Unit.PX);
		taphandler = backbutton;
	}

	HandlerRegistration back,sel;
	
	@Deprecated
	@Override
	public void render(List<SelectModuleItem> items)
	{
		this.items = items;
		list.render(items);
	}

	public void render(List<SelectModuleItem> items, boolean showChildren) {
		this.items = items;
		if(!showChildren) items = Collections.emptyList();
		list.render(items);
	}

	public HasCellSelectedHandler getList()
	{
		return this;
	}

//	@Override
//	public void render(final SelectModuleItem item) {
//		GetScosCallback getScosCallback;
//		if(item.getChildren() != null)
//			render(item.getChildren());
//		else
//		{
//			getScosCallback = new GetScosCallback(item);
//			DWOplayer.clientfactory.getRPCHandler().getScos(item.getID(), getScosCallback);
//		}
//	}

	private static class SCO_TO_MODULEITEM implements Function<List<DomScoContext>, List<SelectModuleItem>> {

		private final SelectModuleItem parent;
		public SCO_TO_MODULEITEM(SelectModuleItem item) {
			this.parent = item;
		}

		@Override
		public List<SelectModuleItem> apply(List<DomScoContext> t) {
			List<SelectModuleItem> items = new ArrayList<SelectModuleItem>(t.size());
			for(DomScoContext sco: t) {
				SelectModuleItem item = new SelectModuleItem(sco);
				item.setParent(parent);
				items.add(item);
				SelectModuleItemHolder.insert(item);
			}
			return items;
		}
	}
	
	@Override
	public void render(final SelectModuleItem item) {
		Promise<List<SelectModuleItem>> promise = item.getChildrenAsync();

		if(promise == null || (promise.isDone() && promise.getFailure() != null)) {
			promise = DWOplayer.clientfactory.getRPCHandler().getScos(item.getID())
					.map(new SCO_TO_MODULEITEM(item));
			item.setChildrenAsync(promise);
		}

		promise.then(new Success<List<SelectModuleItem>, Void>() {

			@Override
			public Promise<Void> call(Promise<List<SelectModuleItem>> resolved) throws Exception {
				render(resolved.getValue(), item.showChildren());
				return null;
			}

			
		});
	}

	
	
	
	
	
	@Override
	public List<SelectModuleItem> getItems() {
		return items;
	}

	@Override
	public void setDescription(SelectModuleItem item) {
		header.setCenter(item.getName());
		String description = item.getDescription();
		if(description != null)
		{
			if(description.startsWith(DescriptionView.GZIPPREFIX))
			{
				this.description.setWidget(new DescriptionViewImpl(item.getID(), this));
			} else
			if(description.startsWith("<html>"))
				this.description.setWidget(new HTML(description));
			else
			{
				this.description.setWidget(new Label(description));
			}
		} else
			this.description.setWidget(new Label(""));


	}

	boolean logout;
	@Override
	public void setLogout(boolean b) {
		logout = b;
		if(b) {
			//backbutton.setText(Text.constants.login());
			if (logoutbutton == null) 
			{
				logoutbutton = new HeaderButton();
				logoutbutton.getElement().setInnerHTML("<span class='fa fa-2x fa-power-off' ></span>");
			}
			//backbutton.getElement().setInnerHTML("<span class='fa fa-2x fa-power-off' ></span>");
			header.setLeftWidget(logoutbutton);
			taphandler = logoutbutton;
		} else {
			header.setLeftWidget(backbutton);
			taphandler = backbutton;
		}
	}

	private CellSelectedHandler gotoHandler;
	@Override
	public void gotoUrl(String href) {
		int index = 0;
		CellSelectedEvent event = new CellSelectedEvent(index, getElement());
		gotoHandler.onCellSelected(event);
	}

	@Override
	public com.google.gwt.event.shared.HandlerRegistration addCellSelectedHandler(
			CellSelectedHandler cellSelectedHandler) {
		this.gotoHandler = cellSelectedHandler;
		return list.addCellSelectedHandler(cellSelectedHandler);
	}

	@Override
	public void setMenuWidget(IsWidget w) {
		header.setRightWidget(Widget.asWidgetOrNull(w));
	}

}
