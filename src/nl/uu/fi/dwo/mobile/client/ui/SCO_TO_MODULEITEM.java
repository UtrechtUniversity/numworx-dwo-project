package nl.uu.fi.dwo.mobile.client.ui;

import java.util.ArrayList;
import java.util.List;

import nl.uu.fi.dwo.rest.dom.entities.DomScoContext;

import org.osgi.util.function.Function;

public class SCO_TO_MODULEITEM implements Function<List<DomScoContext>, List<SelectModuleItem>> {

	private final SelectModuleItem parent;
	public SCO_TO_MODULEITEM(SelectModuleItem item) {
		this.parent = item;
	}

	@Override
	public List<SelectModuleItem> apply(List<DomScoContext> t) {
		List<SelectModuleItem> items = new ArrayList<SelectModuleItem>(t.size());
		for(DomScoContext sco: t) {
			SelectModuleItem item = new SelectModuleItem(sco);
			item.setParent(parent);
			items.add(item);
			SelectModuleItemHolder.insert(item);
		}
		return items;
	}
}