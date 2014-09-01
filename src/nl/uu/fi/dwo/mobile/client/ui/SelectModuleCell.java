package nl.uu.fi.dwo.mobile.client.ui;

import nl.uu.fi.dwo.mobile.client.ui.SelectModuleItem.Type;
import nl.uu.fi.dwo.mobile.client.ui.places.SelectModulePlace;
import nl.uu.fi.dwo.mobile.client.ui.places.TreeModulePlace;
import nl.uu.fi.dwo.mobile.client.ui.places.ViewModulePlace;

import com.google.gwt.core.client.GWT;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.googlecode.mgwt.ui.client.widget.celllist.Cell;

/**
 * Module selection display item
 * 
 * @author Danny Hendrix
 * 
 */
public class SelectModuleCell implements Cell<SelectModuleItem>
{
	private static Template TEMPLATE = GWT.create(Template.class);

	public interface Template extends SafeHtmlTemplates
	{
		@SafeHtmlTemplates.Template("<div class='listItem-dwo'><i class='fa {1} fa-2x listItem-dwo-icon'></i><span>{0}</span></div>")
		SafeHtml content(String text, String type);
	}

	@Override
	public void render(SafeHtmlBuilder safeHtmlBuilder, SelectModuleItem model)
	{
		switch (model.getType()) {
		default:
		case ROOT:
			safeHtmlBuilder.append(TEMPLATE.content(model.getName(), "fa-folder"));
			break;
		case SCO:
			safeHtmlBuilder.append(TEMPLATE.content(model.getName(), "fa-file"));
			break;
		case MODULE:
			safeHtmlBuilder.append(TEMPLATE.content(model.getName(), "fa-book"));
			break;
		case FOLDER:
			safeHtmlBuilder.append(TEMPLATE.content(model.getName(), "fa-folder"));
			break;
		}
		
	}

	@Override
	public boolean canBeSelected(SelectModuleItem model)
	{
		return true;
	}

}
