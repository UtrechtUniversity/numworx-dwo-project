package nl.uu.fi.dwo.mobile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import nl.uu.fi.dwo.account.client.RPCHandlerV1;
import nl.uu.fi.dwo.mobile.client.sco.SCORM_MC2mAccess;
import nl.uu.fi.dwo.mobile.client.sco.SCORM_guest;
import nl.uu.fi.dwo.mobile.client.ui.ClientFactory;
import nl.uu.fi.dwo.mobile.client.ui.ClientFactoryImpl;
import nl.uu.fi.dwo.mobile.client.ui.SelectModuleItem;
import nl.uu.fi.dwo.mobile.client.ui.SelectModuleItemHolder;

import com.fredhat.gwt.xmlrpc.client.XmlRpcClient;
import com.fredhat.gwt.xmlrpc.client.XmlRpcRequest;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * Main class (entry point) Sets up the DWO player.
 * 
 * @author Evertson Croes, Danny Hendrix
 * 
 */
public class MC2LMS extends DWOplayer implements EntryPoint
{
		
	private final class MC2RPCHandler extends RPCHandlerV1 implements nl.uu.fi.dwo.mobile.client.ui.RPCHandler {
		private MC2RPCHandler(String server, int profile) {
			super(server, profile);
		}

		public void getCourses(Map<String, Object> userData,
				AsyncCallback<List<Map<String,Object>>> getCoursesCallback) {
			
			String method = "getCoursesJS"; // sort sequencenr
			int profileID = PROFILE_ID;
			int guestID = PROFILE_OFFSET - profileID;
			
			XmlRpcClient client = getClient();

			Object[] params = { guestID };

			XmlRpcRequest<List<Map<String,Object>>> request = new XmlRpcRequest<List<Map<String,Object>>>(client, method, params, getCoursesCallback);

			request.execute();
		}

		public void getCourses(Object id, AsyncCallback<List<Map<String,Object>>> getCoursesCallback) {
			HashMap<String, Object> g = new HashMap<String,Object>();
			g.put("parentID", id);
			String method = "getTableJS";
			Object[] params = {"tblCourse", g, "name" };
			XmlRpcClient client = getClient();
			XmlRpcRequest<List<Map<String,Object>>> request = new XmlRpcRequest<List<Map<String,Object>>>(client, method, params, getCoursesCallback);
			request.execute();
		}

		public void getCoursesSchool(Object schoolID, AsyncCallback<List<Map<String,Object>>> getCoursesCallback) {
			String method = "getTableJS";
			HashMap<String,Object> g = new HashMap<String,Object>();
			g.put("parentID", 0);
			g.put("schoolID", schoolID);
			g.put("dwoProfileID", PROFILE_ID);
			Object[] params = {"tblCourse", g, "name" };
			XmlRpcClient client = getClient();
			XmlRpcRequest<List<Map<String,Object>>> request = new XmlRpcRequest<List<Map<String,Object>>>(client, method, params, getCoursesCallback);
			request.execute();
		}

		public void getCoursesClass(Map<String,Object> userData, AsyncCallback<List<Map<String,Object>>> getCoursesCallback) {
			String method = "getCoursesForClassJS";
			Object classid = userData.get("classID");
			Object[] params = { classid };
			XmlRpcClient client = getClient();
			XmlRpcRequest<List<Map<String,Object>>> request = new XmlRpcRequest<List<Map<String,Object>>>(client, method, params, filterProfile(getCoursesCallback));
			request.execute();
		}

		@Override
		protected Object objectToKey(Object courseID) {
			return courseID.toString();
		}
	}

	public MC2LMS() {
		super();
		PROFILE_ID = 78;
	}
	
	
	@Override
	protected ClientFactory createClientFactory() {
		ClientFactoryImpl factory = new ClientFactoryImpl() { 
			
			public SCORM_guest setupAPI() {
				SCORM_guest api;
				if(!withUser()) {
					api = new SCORM_guest();
				} else {
					String userID = (String) getUserID();
					String username = (String) profiledata.get("username");
					String fullname = profiledata.get("middlename") + " " + profiledata.get("lastname") + ", " + profiledata.get("firstname");
					fullname = fullname.trim();
					api = new SCORM_MC2mAccess(userID, username, fullname);
				}
				return api;
			}

		};
		String host = PARAMETERS.getHost();
		String http = Window.Location.getProtocol();

		factory.setRPCHandler(new MC2RPCHandler(http + "//" + host + "/dwoapp", PROFILE_ID) );
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
		if(!withUser())
			api = clientfactory.setupAPI();
		else
		{
			api = clientfactory.setupAPI();
			if(!"".equals(clientfactory.getClassID()))
			{
				boolean iconizer = clientfactory.isIconizer();
				if(iconizer)
					callback = GETCOURSES_CALLBACK_CLASS_TREE;
				else
					callback = GETCOURSES_CALLBACK_CLASS_FLAT;
				clientfactory.getRPCHandler().getCoursesClass(clientfactory.getClassID(), callback);
				return;
			}
			if(!"".equals(clientfactory.getSchoolID()))
			{
				count = 2;
				clientfactory.getRPCHandler().getCoursesSchool(clientfactory.getSchoolID(), callback);
			}
		
		}
		
		
		clientfactory.getRPCHandler().getCourses(callback);
		
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
