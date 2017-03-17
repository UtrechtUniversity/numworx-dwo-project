package nl.uu.fi.dwo.formule.client.formuleobjects.vakken;

import nl.uu.fi.dwo.formule.client.formuleobjects.FormuleElement;
import nl.uu.fi.dwo.formule.client.formuleobjects.FormuleElementWithChildren;
import nl.uu.fi.dwo.interaction.client.FormuleFont;

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
	public void paintObject()
	{
		try
		{
			this.getChild().paint();

			//width = getChild().width;
			//height = getChild().height - 2 * fm.getAscent() / 3 - fm.getDescent() + vgh;

			setAsHoogte(getChild().getAsHoogte() + fm.getAscent() / 12);
			setSize(5 * fm.getAscent() / 6 + getChild().width, fm.getAscent() / 6 + getChild().height);
			//this.setSize(width, height);

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

			ctx.setStrokeStyle(color);
			ctx.setLineWidth(fm.getStrokeWidth());

			ctx.beginPath();
			ctx.moveTo(c+b, d);
			ctx.lineTo(c+b-bb, d+bb);
			ctx.lineTo(c, d+hh-b);
			ctx.lineTo(c, height - hh + b - d);
			ctx.lineTo(c+b-bb, height-bb-d);
			ctx.lineTo(c+b, height-d);
//			ctx.stroke();
//			
//			ctx.beginPath();
			ctx.moveTo(width-b-1-c, d);
			ctx.lineTo(width-1-c, d+hh-b);
			ctx.lineTo(width-1-c, height-hh+b-d);
			ctx.lineTo(width-b+bb-1-c, height-bb-d);
			ctx.lineTo(width-b-1-c, height-d);
			ctx.stroke();
			
			this.getChild().draw(ctx);

			this.drawCursor();
		}
		catch (Exception e)
		{
			Window.alert(e.getMessage());
		}
	}
	
	public int getAsHoogte()
	{
		return getChild().getAsHoogte() + fm.getAscent() / 12;
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

	public String toMathML() 
	{
		return "<mfenced>" + getChild().toMathML() + "</mfenced>";
	}

}
