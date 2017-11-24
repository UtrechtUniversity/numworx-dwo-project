package nl.uu.fi.dwo.formule.client.formuleobjects.vakken;

import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.TextMetrics;

import nl.uu.fi.dwo.interaction.client.FormuleFont;

public class CanvasBuilder implements PathBuilder {

	public CanvasBuilder(Context2d ctx2) {
			ctx = ctx2;
	}

	final Context2d ctx;

	@Override
	public void setStrokeStyle(String color) {
		ctx.setStrokeStyle(color);
	}

	@Override
	public void setLineWidth(double strokeWidth) {
		ctx.setLineWidth(strokeWidth);
	}

	@Override
	public void beginPath() {
		ctx.beginPath();
	}

	@Override
	public void moveTo(float i, float d) {
		ctx.moveTo(i, d);
	}

	@Override
	public void lineTo(float i, float j) {
		ctx.lineTo(i, j);		
	}

	@Override
	public void stroke() {
		ctx.stroke();
	}

	@Override
	public void drawline(float x1, float y1, float x2, float y2) {
		ctx.beginPath();
		ctx.moveTo(x1, y1);
		ctx.lineTo(x1, y2);
		ctx.stroke();
	}

	@Override
	public void setFillStyle(String color) {
		ctx.setFillStyle(color);
	}

	@Override
	public void setFont(FormuleFont fm) {
		ctx.setFont(fm.getFontStyle());
	}

	@Override
	public void fillText(String text, float x, float y) {
		ctx.fillText(text, x, y);	
	}

	@Override
	public FontMetrics measureText(String string) {
		TextMetrics result = ctx.measureText(string);
		return new FontMetrics((float) result.getWidth());
	}

	@Override
	public void arc(double x, double y, double radius, double startAngle, double endAngle, boolean anticlockwise) {
		ctx.arc(x, y, radius, startAngle, endAngle, anticlockwise);
	}
	
}
