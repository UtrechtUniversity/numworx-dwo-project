package nl.uu.fi.dwo.mobile.client.ui;

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
		@SafeHtmlTemplates.Template("<div class='listitem'>{0}</div>")
		SafeHtml content(String text);
	}

	@Override
	public void render(SafeHtmlBuilder safeHtmlBuilder, SelectModuleItem model)
	{
		safeHtmlBuilder.append(TEMPLATE.content(model.getName()));
	}

	@Override
	public boolean canBeSelected(SelectModuleItem model)
	{
		return true;
	}

}
