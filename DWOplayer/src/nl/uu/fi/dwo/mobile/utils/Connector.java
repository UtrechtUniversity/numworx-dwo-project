package nl.uu.fi.dwo.mobile.utils;

import nl.uu.fi.dwo.interaction.client.InteractionView;
import nl.uu.fi.dwo.interaction.client.JSONUtilities;
import nl.uu.fi.dwo.interaction.client.json.ObjectList;
import nl.uu.fi.dwo.interaction.client.json.ObjectMap;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public final class Connector {
	public Connector(InteractionView view, String widgetId, ObjectList connections, ObjectMap subscriptions) {
		this.v = view;
		this.widgetId = widgetId;
		this.connections = connections;
		this.subscriptions = subscriptions;
	}
	
	public InteractionView v;
	public ObjectList connections;
	public ObjectMap  subscriptions;
	public String widgetId;
	public Map<String, Set<Map<String,String>>> backing;
	public Set<String> commands = new TreeSet<String>();

	public List<String> getDest(String command) {
		ArrayList<String> result = new ArrayList<String>();
		int len = connectionSize();
		for(int i=0; i < len; i++) {
			ObjectMap m = connections.getObjectMap(i);
			String u = m.getString(command);
			if(u != null) result.add(u);
		}
		return result;
	}
	
	private void subscriptions(Map<String, Connector> xMap) {
		int len = connectionSize();
		for (int i = 0; i < len; i++) {
			ObjectMap m = connections.getObjectMap(i);
			String command = m.keySet().iterator().next();
			String xwid = m.getString(command);
			Connector c = xMap.get(xwid);
			if(c != null)
			{
				c.subscribe(command, Collections.singletonMap(widgetId, command));
			}
		}
		if(subscriptions != null)
		for(String key: subscriptions.keySet()) {
			ObjectList list = subscriptions.getObjectList(key);
			int size = list.size();
			for(int i = 0; i < size; i++) {
				ObjectMap map = list.getObjectMap(i);
				for(String command: map.keySet()) {
					Connector connector = xMap.get(command);
					if(connector != null)
						connector.commands.add(map.getString(command));					
				}
			}
		}

	}

	private synchronized void subscribe(String command, Map<String, String> item) {
		if(backing == null)
			backing = new HashMap<String, Set<Map<String,String>>>();
		Set<Map<String,String>> list = backing.get(command);
		if(list == null)
			backing.put(command, list = new HashSet<Map<String,String>>());
		list.add(item);
	}
	

	public Collection<Map.Entry<String,String>> getSubscriptions(String command)
	{
		if(subscriptions == null && backing != null)
		{
			subscriptions = JSONUtilities.wrapMap(backing);
		}
		
		Collection<Map.Entry<String, String>> result;
		if(subscriptions == null || subscriptions.isEmpty())
			return Collections.emptySet();
		ObjectList set = subscriptions.getObjectList(command);
		int size;
		if( set == null || (size = set.size()) == 0)
			return Collections.emptySet();	
		result = new HashSet<Map.Entry<String,String>>();
		for( int i = 0; i < size; i++) {
			ObjectMap item = set.getObjectMap(i);
			final String wid = item.keySet().iterator().next();
			final String cmd = item.getString(wid);
			result.add(new Map.Entry<String,String>() {

				@Override
				public String getKey() {
					return wid;
				}

				@Override
				public String getValue() {
					return cmd;
				}

				@Override
				public String setValue(String value) {
					return cmd;
				}
			});
		}
		return result;
	}

	private int connectionSize() {
		return connections == null ? 0 : connections.size();
	}
	
	/**
	 * reverse all <em>connections</em> to <em>subscriptions</em>.
	 * @param set
	 */
	public static void calculateSubscriptions(Iterable<Connector> set) {
		HashMap<String,Connector> xMap = new HashMap<String, Connector>();
		for(Connector c : set) {
			xMap.put(c.widgetId, c);
		}
		for(Connector c : set) {
			c.subscriptions(xMap);
		}
	}
	
	
}
