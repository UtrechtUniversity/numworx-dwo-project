package nl.uu.fi.dwo.mobile.client.ui.views;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import nl.uu.fi.dwo.mobile.client.ui.SelectModuleItem;
import nl.uu.fi.dwo.mobile.client.ui.SelectModuleItemHolder;
import nl.uu.fi.dwo.rest.dom.entities.DomClassCourse;
import nl.uu.fi.dwo.rest.dom.entities.DomCourseStudent;

import org.osgi.util.function.Function;

final class COURSE_TO_MODULEITEM implements Function<List<DomCourseStudent>, List<SelectModuleItem>> {
	private final SelectModuleItem item;

	COURSE_TO_MODULEITEM(SelectModuleItem item) {
		this.item = item;
	}

	@Override
	public List<SelectModuleItem> apply(List<DomCourseStudent> t) {
		List<SelectModuleItem> items = new ArrayList<SelectModuleItem>(t.size());
		for (Iterator<DomCourseStudent> iterator = t.iterator(); iterator.hasNext();) {
			DomCourseStudent map = iterator.next();
			SelectModuleItem item = new SelectModuleItem(map, (DomClassCourse) null);
			item.setParent(this.item);
			SelectModuleItemHolder.insert(item);
			items.add(item);
		}
		return items;
	}
}