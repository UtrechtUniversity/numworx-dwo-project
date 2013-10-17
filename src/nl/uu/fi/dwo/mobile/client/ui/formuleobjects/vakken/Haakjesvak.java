package nl.uu.fi.dwo.mobile.client.ui.formuleobjects.vakken;

import nl.uu.fi.dwo.mobile.DWOplayer;
import nl.uu.fi.dwo.mobile.client.ui.formuleobjects.FormuleElement;
import nl.uu.fi.dwo.mobile.client.ui.formuleobjects.FormuleElementWithChildren;
import nl.uu.fi.dwo.mobile.client.ui.formuleobjects.FormuleFont;

import com.google.gwt.user.client.Window;

/**
 * 
 * @author Danny Hendrix
 * 
 */
public class Haakjesvak extends FormuleElementWithChildren
{

	public Haakjesvak(FormuleElement holder)
	{
		super(holder, 1);

		this.setChanged(true);

		setSize(5 * fm.getAscent() / 5 + getChild().width, 4 * fm.getAscent() / 3 + fm.getDescent());
		getChild().setPosition(5 * fm.getAscent() / 12, fm.getAscent() / 12);
	}

	@Override
	public FormuleElement setCurrentElementAt(int x, int y)
	{
		//check if one of the children is pressed
		return getChild().setCurrentElementAt(x - getChild().x, y - getChild().y);
	}

	@Override
	public void paint()
	{
		try
		{
			if (this.isChanged() == false)
				return;
			this.getChild().paint();

			//width = getChild().width;
			//height = getChild().height - 2 * fm.getAscent() / 3 - fm.getDescent() + vgh;

			setAsHoogte(getChild().getAsHoogte() + fm.getAscent() / 12);
			setSize(5 * fm.getAscent() / 6 + getChild().width, fm.getAscent() / 6 + getChild().height);
			//this.setSize(width, height);

			int hoogte = height;
			int breedte = width;
			int h = 3 * fm.getAscent() / 2;
			int hh = h / 2;
			int b = h / 6;
			int bb = b / 2;

			int c = fm.getAscent() / 6;
			int d = fm.getAscent() / 8;

			if (this.isSelected())
			{
				ctx.setFillStyle("#aaf");
				ctx.fillRect(0, 0, this.width, this.height);
			}

			ctx.setStrokeStyle("#000");
			ctx.setFillStyle("#000");
			ctx.setLineWidth(fm.getStrokeWidth());

			drawline(ctx, c + b, d, c + b - bb, d + bb);
			drawline(ctx, c + b - bb, d + bb, c, d + hh - b);
			drawline(ctx, c, d + hh - b, c, hoogte - hh + b - d);
			drawline(ctx, c + b - bb, hoogte - bb - d, c, hoogte - hh + b - d);
			drawline(ctx, c + b, hoogte - d, c + b - bb, hoogte - bb - d);

			drawline(ctx, breedte - b - 1 - c, d, breedte - b + bb - 1 - c, d + bb);
			drawline(ctx, breedte - b + bb - 1 - c, d + bb, breedte - 1 - c, d + hh - b);
			drawline(ctx, breedte - 1 - c, d + hh - b, breedte - 1 - c, hoogte - hh + b - d);
			drawline(ctx, breedte - b + bb - 1 - c, hoogte - bb - d, breedte - 1 - c, hoogte - hh + b - d);
			drawline(ctx, breedte - b - 1 - c, hoogte - d, breedte - b + bb - 1 - c, hoogte - bb - d);

			this.getChild().draw(ctx);

			this.drawCursor();
		}
		catch (Exception e)
		{
			Window.alert(e.getMessage());
		}
	}

	@Override
	public boolean setFont(FormuleFont fm)
	{
		if (super.setFont(fm) == false)
			return false;
		getChild().setPosition(5 * fm.getAscent() / 12, fm.getAscent() / 12);
		return true;
	}

	@Override
	public String toString()
	{
		return "$h" + getChild().toString() + "@";
	}
}
