package nl.uu.fi.dwo.mobile.client.sco;

import java.util.HashMap;
import java.util.Map;

import com.fredhat.gwt.xmlrpc.client.XmlRpcClient;
import com.fredhat.gwt.xmlrpc.client.XmlRpcRequest;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class SCORM_DWOmAccess extends SCORM_guest implements Scorm2004IF {

	private int userID;
	private int scoID;
	private boolean pending;

	private Map<String,String> map = new HashMap<String, String>();
	private Map<String,String> dirty = new HashMap<String, String>();
	
	private XmlRpcClient client = new XmlRpcClient("http://dwo.fisme.science.uu.nl/DWOmAccess/scormaccess");
		
	public SCORM_DWOmAccess(int userID) {
		this.userID = userID;
		pending = false;
		client.setTimeoutMillis(1000000);
	}

	public int getScoID() {
		return scoID;
	}

	public void setScoID(int scoID) {
		if(scoID!=this.scoID)
		{
			map.clear();
			dirty.clear(); // als niet clear dan hebben we een probleem!!!!
		}
		this.scoID = scoID;
	}

	@Override
	public synchronized String Commit() {
		if(!pending && !dirty.isEmpty())
		{	pending = true;
			XmlRpcRequest<Boolean> request;
			final HashMap<String, String> copy = new HashMap<String, String>(dirty);
			dirty.clear();
			Object[] params = new Object[] { userID, scoID, copy };
			AsyncCallback<Boolean> callback = new AsyncCallback<Boolean>() {

				@Override
				public void onFailure(Throwable caught) {
					pending = false;
					copy.putAll(dirty);
					dirty.putAll(copy);
					if (!dirty.isEmpty()) Commit(); // continue until dirty is empty
				}

				@Override
				public void onSuccess(Boolean result) {
					pending = false;
					if(!Boolean.TRUE.equals(result)) onFailure(null);
					else
					if (!dirty.isEmpty()) Commit(); // continue until dirty is empty
						
				}};
				request = new XmlRpcRequest<Boolean>(client, "Commit", params, callback );
				request.execute();
		}
		return super.Commit();
	}

	@Override
	public String GetValue(String name) {
		String result = map.get(name);
		if(result == null) return "";
		return result;
	}

	@Override
	public String SetValue(String name, String value) {
		map.put(name, value);
		dirty.put(name, value);
		return super.SetValue(name, value);
	}

	public synchronized void Initialize(final AsyncCallback<Void> callback) {
		if(!pending) {
			pending = true;
			XmlRpcRequest<HashMap<String,String>> request;
			Object[] params = new Object[] { userID, scoID };
			
			AsyncCallback<HashMap<String,String>> cb = new AsyncCallback<HashMap<String,String>>() {
		
				@Override
				public void onFailure(Throwable caught) {
					pending = false;
					if(callback!=null)callback.onFailure(caught);
				}
		
				@Override
				public void onSuccess(HashMap<String, String> result) {
					
					map.putAll(result);
					map.putAll(dirty);
					pending = false;
					if(callback!=null)callback.onSuccess(null);
				}};
			
			request = new XmlRpcRequest<HashMap<String,String>>(client, "Initialize", params, cb );
			request.execute();
		}
	}

	@Override
	public String Terminate() {
		Commit();
		return super.Terminate();
	}

	
}
