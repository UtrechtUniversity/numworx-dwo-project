package nl.uu.fi.dwo.formule.client.formuleobjects.vakken;

import nl.uu.fi.dwo.interaction.client.FormuleFont;

public interface PathBuilder {

	public final class FontMetrics {
		private final float width;
		public FontMetrics(float width) {
			this.width = width;
		}
		public float getWidth() { return width; }
	}
	
	void setStrokeStyle(String color);

	void setLineWidth(double strokeWidth);

	void beginPath();

	void moveTo(float i, float d);

	void lineTo(float i, float j);

	void stroke();

	void drawline(float x1, float y1, float x2, float y2);

	void setFillStyle(String color);

	void setFont(FormuleFont fmLog);

	void fillText(String string, float x, float y);

	FontMetrics measureText(String string);

	void arc(double x, double y, double radius, double startAngle, double endAngle, boolean anticlockwise);

}