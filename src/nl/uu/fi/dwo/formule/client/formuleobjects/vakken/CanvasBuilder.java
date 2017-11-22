package nl.uu.fi.dwo.formule.client.formuleobjects.vakken;

import com.google.gwt.canvas.dom.client.Context2d;

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
	
}
