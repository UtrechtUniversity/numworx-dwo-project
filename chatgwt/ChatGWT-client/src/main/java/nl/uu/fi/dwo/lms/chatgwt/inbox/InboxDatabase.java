package nl.uu.fi.dwo.lms.chatgwt.inbox;

import java.util.List;

import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.ProvidesKey;

public class InboxDatabase {

	public static class InboxInfo implements Comparable<InboxInfo> {

		/**
	     * The key provider that provides the unique ID of a contact.
	     */
	    public static final ProvidesKey<InboxInfo> KEY_PROVIDER = new ProvidesKey<InboxInfo>() {
	      @Override
	      public Object getKey(InboxInfo item) {
	        return item == null ? null : item.getId();
	      }
	    };

	    private Object id;

		
		public InboxInfo(Object id) {
			this.id = id;
		}


		@Override
		public int compareTo(InboxInfo o) {
			return getDate().compareTo(o.getDate());
		}


		protected Object getId() {
			return id;
		}


		public String getTitle() {
			// TODO Auto-generated method stub
			return "plat";
		}


		public String getDate() {
			// TODO Auto-generated method stub
			return "12-12 22:22";
		}
		
		public boolean isUnseen() {
			return true;
		}
		
		public String getAuthor() {
			return "Wim van Velthoven"; 
		}
		
	}
	  private ListDataProvider<InboxInfo> dataProvider = new ListDataProvider<InboxInfo>();

	  /**
	   * Add a display to the database. The current range of interest of the display
	   * will be populated with data.
	   * 
	   * @param display a {@Link HasData}.
	   */
	  public void addDataDisplay(HasData<InboxInfo> display) {
	    dataProvider.addDataDisplay(display);
	  }

	public InboxDatabase() {
		List<InboxInfo> list = dataProvider.getList();
		list.add(new InboxInfo(1));
		list.add(new InboxInfo(2));
		list.add(new InboxInfo(3));
		list.add(new InboxInfo(4));
		list.add(new InboxInfo(5));
	}

}
