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
	private static final SafeHtml EMPTY = new SafeHtmlBuilder().toSafeHtml();
	private static Template TEMPLATE = GWT.create(Template.class);

	public interface Template extends SafeHtmlTemplates
	{
		@SafeHtmlTemplates.Template("<div class='listItem-dwo'><i class='fa {1} fa-2x listItem-dwo-icon'></i><span> {0}</span>{2}</div>")
		SafeHtml content(String text, String type, SafeHtml extra);
	}

	@Override
	public void render(SafeHtmlBuilder safeHtmlBuilder, SelectModuleItem model)
	{
		switch (model.getType()) {
		default:
		case ROOT:
			safeHtmlBuilder.append(TEMPLATE.content(model.getName(), "fa-folder", EMPTY));
			break;
		case SCO:
			SafeHtml p = EMPTY;
			if(model.isShowScore())
			{
				p = (SimpleProgressBar.statusCellSafeHTMLTemplate.status(model.getScore()));
			}
			safeHtmlBuilder.append(TEMPLATE.content(model.getName(), "fa-file", p));
			break;
		case MODULE:
			safeHtmlBuilder.append(TEMPLATE.content(model.getName(), "fa-book", EMPTY));
			break;
		case FOLDER:
			safeHtmlBuilder.append(TEMPLATE.content(model.getName(), "fa-folder", EMPTY));
			break;
		}
		
	}

	@Override
	public boolean canBeSelected(SelectModuleItem model)
	{
		return true;
	}

}
