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
	private static HashMap<Integer, SelectModuleItem> map, scos;

	private static void init()
	{
		if (list != null)
			return;
		list = new ArrayList<SelectModuleItem>();
		map = new HashMap<Integer, SelectModuleItem>();
		scos = new HashMap<Integer, SelectModuleItem>();
		
		/*
				insert(0, "test1", "test.xml");
				insert(1, "test2", "test2.xml");
				insert(2, "test3", "test3.xml");
				insert(3, "test4", "test4.xml");
				insert(4, "test6", "test6.xml");
				insert(4, "diff", "diff.xml");
				*/
	}

	public static void insert(int id, String name, String file)
	{
		init();
		SelectModuleItem item = new SelectModuleItem(id, name, file);
		map.put(id, item);
		list.add(item);
	}

	public static void insert(int id, Node node)
	{
		init();
		SelectModuleItem item = new SelectModuleItem(id, node);
		map.put(id, item);
		list.add(item);
	}
	
	public static void insert(SelectModuleItem item) {
		init();
		switch(item.getType()) {
		case SCO: scos.put(item.getID(), item);
				break;
		default:
			map.put(item.getID(), item);
			if(item.getParent() == null) list.add(item);
		}
	}

	public static List<SelectModuleItem> getItems()
	{
		init();
		return list;
	}

	public static SelectModuleItem getItemByID(int id)
	{
		init();
		return map.get(id);
	}

	public static SelectModuleItem getScoByID(int id) {
		return scos.get(id);
	}

	public static void clear() {
		init();
		list.clear();
		map.clear(); map.put(0,  SelectModuleItem.ROOT);
		scos.clear();
	}
	
	public static void destroy() {
		list = null;
		map = null;
		scos = null;
	}
}
