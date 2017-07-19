package nl.uu.fi.dwo.mobile.client.ui.views;

import java.util.Collections;
import java.util.List;

import org.osgi.util.promise.Failure;
import org.osgi.util.promise.Promise;
import org.osgi.util.promise.Success;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.place.shared.Place;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.CellList;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.InlineHTML;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

import nl.uu.fi.dwo.account.client.DwoGlobalVars;
import nl.uu.fi.dwo.mobile.DWOplayer;
import nl.uu.fi.dwo.mobile.client.ui.SCO_TO_MODULEITEM;
import nl.uu.fi.dwo.mobile.client.ui.SelectModuleItem;
import nl.uu.fi.dwo.mobile.client.ui.SelectModuleItem.Type;
import nl.uu.fi.dwo.mobile.client.ui.places.LoginPlace;
import nl.uu.fi.dwo.mobile.client.ui.places.TreeModulePlace;
import nl.uu.fi.dwo.mobile.client.ui.places.ViewModulePlace;
import nl.uu.fi.dwo.mobile.client.ui.views.AnchorView.AnchorContext;

public class TreeModuleViewNumworx extends TreeModuleBase implements AnchorContext {

	final class ProvideCells implements Success<List<SelectModuleItem>, Void> {
		@Override
		public Promise<Void> call(
				Promise<List<SelectModuleItem>> resolved)
				throws Exception {
			tiles.setRowData(resolved.getValue());
			tiles.redraw();
			return null;
		}
	}

	class NavCell extends AbstractCell<SelectModuleItem> {

		@Override
		public void render(Context context, SelectModuleItem value,
				SafeHtmlBuilder sb) {
			sb.appendHtmlConstant("<div class='" + style.navItem() +"'>");
			sb.appendEscaped(value.getName());
			sb.appendHtmlConstant("</div>");
		}

		public NavCell() {
			super("click");
		}

		@Override
		public void onBrowserEvent(Context context, Element parent, SelectModuleItem value, NativeEvent event,
				ValueUpdater<SelectModuleItem> valueUpdater) {
		    String eventType = event.getType();
		    if("click".equals(eventType)) {
		    	GWT.log(value.getName());
		    	presenter.goTo(new TreeModulePlace(value.getID()));
		    	return;
		    }
			super.onBrowserEvent(context, parent, value, event, valueUpdater);
		}

	}
	
	class TileCell extends AbstractCell<SelectModuleItem> {

		@Override
		public void render(Context context,
				SelectModuleItem value, SafeHtmlBuilder sb) {
/* 					<g:FlowPanel styleName='{style.tile}' >
						<g:HTML styleName='{style.tileHeader}'>Exponentiele functies</g:HTML>
						<g:HTML styleName='{style.tileBody}'>TILE BODY</g:HTML>
						<g:FlowPanel styleName='{style.tileFooter}'>
							<g:InlineHTML styleName='{style.tileResult}'><span class='fa-stack fa-lg'><i class='fa fa-circle fa-stack-1x' style='color:red;'></i><i class='fa fa-times fa-stack-1x' style='color:white;'></i></span></g:InlineHTML>
							<g:InlineHTML styleName='{style.tileScore}'>10%</g:InlineHTML>
							<g:InlineHTML styleName='{style.tileType}'><i class='fa fa-file-text-o'></i></g:InlineHTML>
						</g:FlowPanel>					
					</g:FlowPanel>
*/			
			sb.appendHtmlConstant("<div class='"+style.tile()+"'>");
			  sb.appendHtmlConstant("<div class='" + style.tileHeader() + "'>");
			    sb.appendEscaped(value.getName());
			  sb.appendHtmlConstant("</div>");

			  sb.appendHtmlConstant("<div class='" + style.tileBody() + "'>");
			    String description = value.getDescription();
			    if(description.isEmpty()||description.startsWith(DescriptionView.GZIPPREFIX)) {
			    	switch(value.getType()) {
			    	case MODULE: 
			    		sb.appendHtmlConstant("<img style='height: 60%; margin-top: 10%; margin-left: 34%;' src='"
			    				+ r("images/numworx/module-numworx.png")
			    				+ "' />");
			    		break;
			    	case FOLDER:
			    		sb.appendHtmlConstant("<img style='height: 60%; margin-top: 10%; margin-left: 34%;' src='"
			    				+ r("images/numworx/folder-numworx.png")
			    				+ "' />");
			    		break;
			    	case SCO:
			    		sb.appendHtmlConstant("<img style='height: 60%; margin-top: 10%; margin-left: 34%;' src='"
			    				+ r("images/numworx/activiteit_numworx.png")
			    				+ "' />");
			    		break;
			    	default:
			    	}
			    	
			    	
			    } else {
			    	if(description.startsWith("<html"))
			    		sb.appendHtmlConstant(description);
			    	else
			    		sb.appendEscaped(description);
			    }
			    sb.appendHtmlConstant("</div>");

			  sb.appendHtmlConstant("<div class='" + style.tileFooter() + "'>");
			  	sb.appendHtmlConstant("<span class='"+style.tileResult()+ "'>");
			  	sb.appendHtmlConstant("<span class='fa-stack fa-lg'><i class='fa fa-circle fa-stack-1x' style='color:red;'></i><i class='fa fa-times fa-stack-1x' style='color:white;'></i></span>");
			  	sb.appendHtmlConstant("</span>");
			  	if(value.isShowScore()) {
			  		sb.appendHtmlConstant("<span class='"+style.tileScore()+ "'>");
			  		sb.append(value.getScore().intValue()); sb.appendEscaped("%");
			  		sb.appendHtmlConstant("</span>");
			  	}
			  	sb.appendHtmlConstant("<span class='"+style.tileType()+ "'>");
			  	sb.appendHtmlConstant("<i class='fa fa-file-text-o'></i>");
			  	sb.appendHtmlConstant("</span>");
			  sb.appendHtmlConstant("</div>");
		    sb.appendHtmlConstant("</div>");
			  
			  
			
		}
		
		private String r(String string) {
			return DWOplayer.PARAMETERS.getResource(string);
		}

		public TileCell() {
			super("click");
		}

		@Override
		public void onBrowserEvent(Context context, Element parent, SelectModuleItem value, NativeEvent event,
				ValueUpdater<SelectModuleItem> valueUpdater) {
		    String eventType = event.getType();
		    if("click".equals(eventType)) {
		    	Place place;
		    	if(value.getType() == Type.SCO)
		    		place = new ViewModulePlace(value.getID());
		    	else
		    		place = new TreeModulePlace(value.getID());
				presenter.goTo(place);
		    	return;
		    }
			super.onBrowserEvent(context, parent, value, event, valueUpdater);
		}
		
		
	}
	

	private static TreeModuleViewNumworxUiBinder uiBinder = GWT.create(TreeModuleViewNumworxUiBinder.class);

	@UiField DockLayoutPanel root;
	@UiField(provided=true)
	CellList<SelectModuleItem> cells;
	@UiField(provided=true)
	CellList<SelectModuleItem> tiles;
	@UiField HTML title;
	@UiField SimplePanel description;
	@UiField TreeModuleViewNumworxCss style;
	@UiField InlineHTML homeBtn, searchBtn;
	@UiField Label loginLabel;
	
	
	@UiHandler("homeBtn")
	void onHomeBtn(ClickEvent ev) {
		presenter.goTo(new TreeModulePlace());
	}
	@UiHandler("searchBtn")
	void onSearch(ClickEvent ev) {
		presenter.goTo(new LoginPlace());
	}
		
	private List<SelectModuleItem> list;
	
	interface TreeModuleViewNumworxUiBinder extends UiBinder<Widget, TreeModuleViewNumworx> {
	}

	public TreeModuleViewNumworx() {
		CellList.Resources cellResources;
		cellResources = GWT.create(CellList.Resources.class);
		cells = new CellList<SelectModuleItem>(new NavCell(), cellResources);
		cellResources = GWT.create(HorizontalCellListResources.class);
		tiles = new CellList<SelectModuleItem>(new TileCell(), cellResources);
		initWidget(uiBinder.createAndBindUi(this));
		root.forceLayout();
	}

	@Override
	public void render(List<SelectModuleItem> currentModel) {
		this.list = currentModel;
		cells.setRowData(list);
		cells.redraw();
		String login = DWOplayer.withUser()? DwoGlobalVars.instance().getCurrentUser().getDisplayName() : "GUEST";
		loginLabel.setText(login);
	}

	Promise<List<SelectModuleItem>> getChildrenPromise(final SelectModuleItem parent) {
		Promise<List<SelectModuleItem>> promise = parent.getChildrenAsync();

		if(promise == null || (promise.isDone() && promise.getFailure() != null)) {
			promise = DWOplayer.clientfactory.getRPCHandler().getCourses(parent.getID())
					.map(new COURSE_TO_MODULEITEM(parent));
			parent.setChildrenAsync(promise);
			promise
			.then(new Success<List<SelectModuleItem>, List<SelectModuleItem>>() {

				@Override
				public Promise<List<SelectModuleItem>> call(Promise<List<SelectModuleItem>> resolved) throws Exception {
					for(SelectModuleItem item: resolved.getValue()) {
						if(item.getType() == SelectModuleItem.Type.FOLDER) {
							if(item.getChildrenAsync() == null) {
								item.setChildrenAsync(DWOplayer.clientfactory.getRPCHandler().getCourses(item.getID())
										.map(new COURSE_TO_MODULEITEM(item)));
							}
						}
					}	
					return resolved;
				}}, new Failure() {
					
					@Override
					public void fail(Promise<?> resolved) throws Exception {
						Window.alert(resolved.getFailure().toString());
						parent.setChildrenAsync(null);
					}
				});
		}
		return promise;
	}
	Promise<List<SelectModuleItem>> getScosPromise(final SelectModuleItem parent) {
		Promise<List<SelectModuleItem>> promise = parent.getChildrenAsync();

		if(promise == null || (promise.isDone() && promise.getFailure() != null)) {
			promise = DWOplayer.clientfactory.getRPCHandler().getScos(parent.getID())
					.map(new SCO_TO_MODULEITEM(parent));
			parent.setChildrenAsync(promise);
			promise
			.then(null, new Failure() {
					
					@Override
					public void fail(Promise<?> resolved) throws Exception {
						Window.alert(resolved.getFailure().toString());
						parent.setChildrenAsync(null);
					}
				});
		}
		return promise;
	}
	
	
	@Override
	public void selectModule(SelectModuleItem item) {
		tiles.setRowData(Collections.<SelectModuleItem> emptyList());
		switch(item.getType()) {
		case ROOT:
				title.setText(item.getName());
				description.setWidget(getLabel(item));
			break;
		case FOLDER:
				title.setText(item.getName());
				description.setWidget(getLabel(item));
				if(item.showChildren());
				{
					getChildrenPromise(item).then(new ProvideCells());
				}
				
				
			break;
		case MODULE:
				title.setText(item.getName());
				description.setWidget(getLabel(item));
				if(item.showChildren());
				{
					getScosPromise(item).then(new ProvideCells());
				}
			break;
		default:
			
		}
		tiles.redraw();
	}

	private Widget getLabel(SelectModuleItem item) {
		Widget w;
		String description = item.getDescription();
		if(description.startsWith(DescriptionView.GZIPPREFIX))
		{
			w = new DescriptionViewImpl(item.getID(), this).asWidget();
		} else
		if(description.startsWith("<html>")) {
			w = new HTML(description);
		}else
		{
			w = new Label(description);
		}
		return w;
	}
	@Override
	public void close() {
		// TODO Auto-generated method stub
		
	}

	Presenter presenter;
	@Override
	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
	}

	@Override
	public void setMenuWidget(IsWidget w) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void gotoUrl(String href) {
		// TODO Auto-generated method stub
		
	}

}
