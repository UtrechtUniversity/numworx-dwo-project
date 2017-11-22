package nl.uu.fi.dwo.formule.client.formuleobjects.vakken;

public interface PathBuilder {

	void setStrokeStyle(String color);

	void setLineWidth(double strokeWidth);

	void beginPath();

	void moveTo(float i, float d);

	void lineTo(float i, float j);

	void stroke();

}