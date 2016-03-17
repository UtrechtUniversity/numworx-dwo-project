package nl.uu.fi.dwo.mobile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import nl.uu.fi.dwo.mobile.client.DWOplayerClientBundle;
import nl.uu.fi.dwo.mobile.client.DWOplayerParameters;
import nl.uu.fi.dwo.mobile.client.sco.SCORM_DWOmAccess;
import nl.uu.fi.dwo.mobile.client.sco.SCORM_MC2mAccess;
import nl.uu.fi.dwo.mobile.client.sco.SCORM_guest;
import nl.uu.fi.dwo.mobile.client.ui.AppPlaceHistoryMapper;
import nl.uu.fi.dwo.mobile.client.ui.ClientFactory;
import nl.uu.fi.dwo.mobile.client.ui.ClientFactoryImpl;
import nl.uu.fi.dwo.mobile.client.ui.SelectModuleItem;
import nl.uu.fi.dwo.mobile.client.ui.SelectModuleItem.Type;
import nl.uu.fi.dwo.mobile.client.ui.SelectModuleItemHolder;
import nl.uu.fi.dwo.mobile.client.ui.TabletActivityMapper;
import nl.uu.fi.dwo.mobile.client.ui.TabletAnimationMapper;
import nl.uu.fi.dwo.mobile.client.ui.activities.RPCHandler;
import nl.uu.fi.dwo.mobile.client.ui.places.FlatModulePlace;
import nl.uu.fi.dwo.mobile.client.ui.places.LoginPlace;
import nl.uu.fi.dwo.mobile.client.ui.places.SelectModulePlace;
import nl.uu.fi.dwo.mobile.client.ui.places.TreeModulePlace;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceHistoryHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.RootPanel;
import com.googlecode.mgwt.mvp.client.AnimatableDisplay;
import com.googlecode.mgwt.mvp.client.AnimatingActivityManager;
import com.googlecode.mgwt.ui.client.MGWT;
import com.googlecode.mgwt.ui.client.MGWTSettings;
import com.googlecode.mgwt.ui.client.MGWTSettings.ViewPort;
import com.googlecode.mgwt.ui.client.MGWTSettings.ViewPort.DENSITY;

import fi.wiskopdr.text.Text_nl;

/**
 * Main class (entry point) Sets up the DWO player.
 * 
 * @author Evertson Croes, Danny Hendrix
 * 
 */
public class MC2LMS extends DWOplayer implements EntryPoint
{
		
	public MC2LMS() {
		super();
		PROFILE_ID = 78;
	}
	
	
	@Override
	protected ClientFactory createClientFactory() {
		ClientFactoryImpl factory = new ClientFactoryImpl() { 
			
			public SCORM_guest setupAPI(final Map<String, Object> profiledata) {
				SCORM_guest api;
				if(profiledata == null) {
					api = new SCORM_guest();
					menuWidget = null;
				} else {
					String userID = (String) profiledata.get("userID");
					String username = (String) profiledata.get("username");
					String fullname = profiledata.get("middlename") + " " + profiledata.get("lastname") + ", " + profiledata.get("firstname");
					fullname = fullname.trim();
					api = new SCORM_MC2mAccess(userID, username, fullname);
					getUserBar().setProfile(profiledata);
				}
				return api;
			}

		};
		String host = PARAMETERS.getHost();
		String http = Window.Location.getProtocol();

		factory.setRPCHandler(new RPCHandler(http + "//" + host + "/dwoapp") {

			@Override
			protected Object objectToKey(Object courseID) {
				return courseID.toString();
			} } );
		return factory;
	}



	public static final AsyncCallback<List<Map<String,Object>>>
	GETCOURSES_CALLBACK_CLASS_TREE = new AsyncCallback<List<Map<String,Object>>>(){

		@Override
		public void onFailure(Throwable caught) {
			Window.alert(caught.toString());
			goTree();
		}

		@Override
		public void onSuccess(List<Map<String, Object>> result) {
			sort(result);
			insertTree(result);
			goTree();
		}
		
		private String getParentID(Map<String,Object> course) {
			try {
				return (String) course.get("parentID");
			} catch (Exception e) {
				return "";
			}
		}
		
		private String getID(Map<String, Object> course) {
			try {
				return (String) course.get("courseID");
			} catch (Exception e) {
				return "";
			}
		}
		private void sort(List<Map<String,Object>> courses) {
			boolean again;
			if(courses == null || courses.isEmpty())
				return;
			do {
				again = false;
				more:
				for(int i = 0; i < courses.size(); i++ ) {
					Map<String,Object> course = courses.get(i);
					if( getParentID(course) .length() > 0) {
						int j;
						for(j = i-1; j >= 0; j--) {
							if(getParentID(courses.get(j)).length() > 0 ) {
								if(j == i-1)
									break;
								courses.add(j+1, courses.remove(i));
								continue more;
							}
						}
						if(j == -1) {
							courses.add(0, courses.remove(i));
							continue more;
						}
					} else {
						String pid = getParentID(course); int j;
						for(j = i-1; j>=0; j--) {
							if(getParentID(courses.get(j))==pid || getID(courses.get(j)) == pid) {
								if(j == i-1) break;
								courses.add(j+1, courses.remove(i));
								continue more;
							}
						}
						if(j == -1) {
							again = true;
						}
					}
				}
			} while(again);
		}
		
	};

	


	@Override
	protected void gotoCourses_impl() {
		SelectModuleItemHolder.clear(); // hier leegmaken of elders?
		count = 1;
		AsyncCallback<List<Map<String, Object>>> callback = GETCOURSES_CALLBACK;
		if(profiledata == null)
			api = clientfactory.setupAPI(profiledata);
		else
		{
//			String userID = ((String) profiledata.get("userID"));
//			String username = (String) profiledata.get("username");
//			String fullname = profiledata.get("middlename") + " " + profiledata.get("lastname") + ", " + profiledata.get("firstname");
//			fullname = fullname.trim();
			api = clientfactory.setupAPI(profiledata);
			//api = new SCORM_MC2mAccess(userID, username, fullname);
			if(!"".equals(profiledata.get("classID")))
			{
				boolean iconizer = Boolean.TRUE.equals(profiledata.get("iconizer"));
				if(iconizer)
					callback = GETCOURSES_CALLBACK_CLASS_TREE;
				else
					callback = GETCOURSES_CALLBACK_CLASS_FLAT;
				clientfactory.getRPCHandler().getCoursesClass(profiledata, callback);
				return;
			}
			if(!"".equals(profiledata.get("schoolID")))
			{
				count = 2;
				clientfactory.getRPCHandler().getCoursesSchool(profiledata, callback);
			}
		
		}
		
		
		clientfactory.getRPCHandler().getCourses(profiledata, callback);
		
	}

	private static void insertTree(List<Map<String,Object>> result) {
		for (Iterator<Map<String, Object>> iterator = result.iterator(); iterator.hasNext();) {
			Map<String, Object> map = iterator.next();
			SelectModuleItem item = new SelectModuleItem(map, SelectModuleItem.Type.MODULE);
			String parentID = (String) map.get("parentID");
			if(parentID != null && parentID.length()>0 )
			{
				SelectModuleItem parent = SelectModuleItemHolder.getItemByID(parentID);
				if( parent != null)
				{
					List<SelectModuleItem> children = parent.getChildren();
					if(children == null)
						parent.setChildren(children = new ArrayList<SelectModuleItem>());
					children.add(item);
					item.setParent(parent);
				} 
			} 
			SelectModuleItemHolder.insert(item);
			
		}
	}
}
