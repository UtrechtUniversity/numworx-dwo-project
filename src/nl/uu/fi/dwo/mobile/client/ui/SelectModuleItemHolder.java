package nl.uu.fi.dwo.mobile.client.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.google.gwt.xml.client.Node;

/**
 * Contains all items in module selection
 * 
 * @author Danny Hendrix
 * 
 */
public class SelectModuleItemHolder
{
	private static List<SelectModuleItem> list;
	private static HashMap<String, SelectModuleItem> map, scos;
	private static HashMap<Object, SelectModuleItem> searches;

	private static void init()
	{
		if (list != null)
			return;
		list = new ArrayList<SelectModuleItem>();
		map = new HashMap<String, SelectModuleItem>();
		scos = new HashMap<String, SelectModuleItem>();
		searches = new HashMap<Object, SelectModuleItem>();
		
		/*
				insert(0, "test1", "test.xml");
				insert(1, "test2", "test2.xml");
				insert(2, "test3", "test3.xml");
				insert(3, "test4", "test4.xml");
				insert(4, "test6", "test6.xml");
				insert(4, "diff", "diff.xml");
				*/
	}

	public static void insert(Object id, String name, String file)
	{
		init();
		SelectModuleItem item = new SelectModuleItem(id, name, file);
		map.put(id.toString(), item);
		list.add(item);
	}

	public static void insert(Object id, Node node)
	{
		init();
		SelectModuleItem item = new SelectModuleItem(id, node);
		map.put(id.toString(), item);
		list.add(item);
	}
	
	public static void insert(SelectModuleItem item) {
		init();
		switch(item.getType()) {
		case SCO: scos.put(item.getID().toString(), item);
				break;
		case SEARCH:
					searches.put(item.getID(), item);
					searches.put(item.getName(), item);
					break;
		default:
			map.put(item.getID().toString(), item);
			if(item.getParent() == null) list.add(item);
		}
	}

	public static List<SelectModuleItem> getItems()
	{
		init();
		return list;
	}

	public static SelectModuleItem getItemByID(Object id)
	{
		init();
		return map.get(id.toString());
	}

	public static SelectModuleItem getItemByID(String id) {
		init();
		return map.get(id);
	}
	
	public static SelectModuleItem getScoByID(Object id) {
		init();
		return scos.get(id.toString());
	}

	public static SelectModuleItem getSearch(Object id) {
		init();
		return searches.get(id);
	}
	
	public static void clear() {
		init();
		list.clear();
		map.clear();
		map.put("0",  SelectModuleItem.ROOT);
		map.put("", SelectModuleItem.ROOT);
		scos.clear();
		searches.clear();
	}
	
	public static void destroy() {
		list = null;
		map = null;
		scos = null;
		searches = null;
	}
}
