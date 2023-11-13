/**
 * 
 */
package nl.uu.fi.dwo.lms.chatgwt;

import java.util.Optional;
import java.util.logging.Logger;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.resources.client.ClientBundle.Source;
import com.google.gwt.resources.client.ImageResource.ImageOptions;
import com.google.gwt.resources.client.ImageResource.RepeatStyle;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.CellList;
import com.google.gwt.user.cellview.client.CellList.Resources;
import com.google.gwt.user.cellview.client.HasKeyboardPagingPolicy.KeyboardPagingPolicy;
import com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy.KeyboardSelectionPolicy;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SelectionModel;
import com.google.gwt.view.client.SingleSelectionModel;

import nl.uu.fi.dwo.lms.chatgwt.entities.ChatRoom;
import nl.uu.fi.dwo.lms.chatgwt.entities.ChatUser;
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
		      sb.appendHtmlConstant("<div class='inbox1'><span>");
		      	sb.appendEscaped(value.getTitle());
		      sb.appendHtmlConstant("</span> <span>");
		      	sb.appendEscaped(value.getDate());
		      sb.appendHtmlConstant("</span></div><div class='inbox2'><span>");
		      	sb.appendEscaped(value.getAuthor());
		      sb.appendHtmlConstant("</span> <span> ");
		      	sb.appendHtmlConstant(value.isUnseen()? " ●" : "");
		      sb.appendHtmlConstant("</span></div>");
			
		}
		
	}
	private CellList<InboxInfo> cellList;
	@UiField ShowMorePager pagerPanel;
	
	private ChatGWT parent;
	private boolean fuse;
	
	
	public interface MyResources extends Resources {

		@Override
	    @Source("resources/cellListSelectedBackground.png")
	    @ImageOptions(repeatStyle = RepeatStyle.Both, flipRtl = true)
		ImageResource cellListSelectedBackground();
		
	}
	
	public InboxPanel(InboxDatabase database) {
		
		parent = database.parent;
		InboxCell cell = new InboxCell();
		Resources resources = GWT.create(MyResources.class);
		cellList = new CellList<InboxInfo>(cell, resources, InboxDatabase.KEY_PROVIDER);
		cellList.addStyleName("inbox");
		
	    cellList.setPageSize(30);
	    cellList.setKeyboardPagingPolicy(KeyboardPagingPolicy.INCREASE_RANGE);
	    cellList.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.DISABLED);

	    selectionModel = new SingleSelectionModel<InboxInfo>(
	    		InboxDatabase.KEY_PROVIDER);
	    cellList.setSelectionModel(selectionModel);
	    selectionModel.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
	      public void onSelectionChange(SelectionChangeEvent event) {
	        if (!fuse) selectInbox(selectionModel.getSelectedObject());
	      }
	    });
		initWidget(uiBinder.createAndBindUi(this));
		
		database.addDataDisplay(cellList);
		pagerPanel.setDisplay(cellList);
		
	}

	static Logger LOG = Logger.getLogger(InboxPanel.class.getName());
	private SingleSelectionModel<InboxInfo> selectionModel;
	protected void selectInbox(InboxInfo selectedObject) {
		if (selectedObject == null) return;
		String key = (String) InboxDatabase.KEY_PROVIDER.getKey(selectedObject);
		LOG.info("selected " + key);
		if (selectedObject.isRoom()) {
			Optional<ChatRoom> room = parent.getRoom((String) key);
			if (room.isPresent())
			{
				parent.updateRoomExtra(room.get());
			}
		} else {
			ChatUser user = parent.get(key);
			// switch to room of user, select usermodel
			if (user != null) {
				parent.updateUserExtra(user);
			}
		}
	}
	
	void setSelection(Optional<InboxInfo> item, boolean f) {
		try { 
			fuse = !f; // DON'T call selectionhandler!!!!!!!!
			if (!item.isPresent()) {
				selectionModel.clear();
			} else if (!selectionModel.isSelected(item.get())) {
				selectionModel.setSelected(item.get(), true);
				LOG.info("selected: " + selectionModel.isSelected(item.get())); // flush event....
			}
		} finally {
			fuse = false;
		}
	}
	

}
