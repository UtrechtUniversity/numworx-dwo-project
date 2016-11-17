package nl.uu.fi.dwo.formule.client.formuleobjects.vakken;



import nl.uu.fi.dwo.formule.client.formuleobjects.FormuleElement;
import nl.uu.fi.dwo.formule.client.formuleobjects.FormuleElementWithChildren;

public class ConjugVak extends FormuleElementWithChildren
{
	public ConjugVak(FormuleElement holder)
	{
		super(holder, 1);
		getChild().setPosition(0, 0);
		this.setChanged(true);
	}
	
	@Override
	public void paintObject()
	{
		this.getChild().paint();

		width =  getChild().width;
		height = getChild().height;

		this.setSize(width, height);

		if (this.isSelected())
		{
			ctx.setFillStyle("#aaf");
			ctx.fillRect(0, 0, width, height);
		}

		ctx.setStrokeStyle(color);
		ctx.setFillStyle(color);
		
		ctx.setLineWidth(0.6 * fm.getStrokeWidth());

		ctx.beginPath();
		ctx.moveTo(2, 1);
		ctx.lineTo(width - 1, 1);
		ctx.stroke();

		this.getChild().draw(ctx);
		this.drawCursor();
	}	

	public int getAsHoogte()
	{
		return getChild().getAsHoogte()+2;
	}
	
	public String toString()
	{	return "$c" + getChild().toString() + "@";
	}

	public String toMathML() {
/* geen idee welke van de twee de meest gesupporte is */
		//return "<menclose notation='top' >" + getChild().toMathML() + "</menclose>";
		return "<mover>" + getChild().toMathML() + "<mo>\u00AF</mo></mover>";
	}

}
