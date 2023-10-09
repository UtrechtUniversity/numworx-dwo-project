/**
 * 
 */
package nl.uu.fi.dwo.lms.chatgwt;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.CellList;
import com.google.gwt.user.cellview.client.HasKeyboardPagingPolicy.KeyboardPagingPolicy;
import com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy.KeyboardSelectionPolicy;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SingleSelectionModel;

import nl.uu.fi.dwo.lms.chatgwt.inbox.InboxDatabase;
import nl.uu.fi.dwo.lms.chatgwt.inbox.InboxDatabase.InboxInfo;
import nl.uu.fi.dwo.lms.chatgwt.inbox.ShowMorePager;

/**
 * @author peterboon
 *
 */
public class InboxPanel extends Composite {

	private static InboxPanelUiBinder uiBinder = GWT.create(InboxPanelUiBinder.class);

	interface InboxPanelUiBinder extends UiBinder<Widget, InboxPanel> {
	}

	static class InboxCell extends AbstractCell<InboxInfo> {

		@Override
		public void render(Context context, InboxInfo value, SafeHtmlBuilder sb) {
		     // Value can be null, so do a null check..
		      if (value == null) {
		        return;
		      }
		      sb.appendHtmlConstant("<span>");
		      	sb.appendEscaped(value.getTitle());
		      sb.appendHtmlConstant("</span> <span>");
		      	sb.appendEscaped(value.getDate());
		      sb.appendHtmlConstant("</span><br><span>");
		      	sb.appendEscaped(value.getAuthor());
		      sb.appendHtmlConstant("</span> <span> ");
		      	sb.appendHtmlConstant(value.isUnseen()? " ●" : "");
		      sb.appendHtmlConstant("</span>");
			
		}
		
	}
	private CellList<InboxInfo> cellList;
	@UiField ShowMorePager pagerPanel;
	
	public InboxPanel(InboxDatabase database) {
		
		InboxCell cell = new InboxCell();
		cellList = new CellList<InboxInfo>(cell, InboxInfo.KEY_PROVIDER);		
	    cellList.setPageSize(30);
	    cellList.setKeyboardPagingPolicy(KeyboardPagingPolicy.INCREASE_RANGE);
	    cellList.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.BOUND_TO_SELECTION);

	    // Add a selection model so we can select cells.
	    final SingleSelectionModel<InboxInfo> selectionModel = new SingleSelectionModel<InboxInfo>(
	        InboxInfo.KEY_PROVIDER);
	    cellList.setSelectionModel(selectionModel);
	    selectionModel.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
	      public void onSelectionChange(SelectionChangeEvent event) {
	        selectInbox(selectionModel.getSelectedObject());
	      }
	    });
		initWidget(uiBinder.createAndBindUi(this));
		
		database.addDataDisplay(cellList);
		pagerPanel.setDisplay(cellList);
		
	}

	protected void selectInbox(InboxInfo selectedObject) {
		GWT.log("selected " + InboxInfo.KEY_PROVIDER.getKey(selectedObject));
	}

}
