package fi.dwo.dwojapplet.gui.domainmodel;

import java.awt.Color;
import java.awt.Component;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Polygon;

import javax.swing.Icon;

class ScoreIcon implements PIcon {
	static final float UNSURE = 0.5f;

	float green = 0.64f;
	float red = 0.24f;
	float score = 0.5f;
	float score1 = 0.5f;

	ScoreIcon(double green, double red, FontMetrics fm) {
		this.fm = fm;
		if (Double.isNaN(green))
			green = UNSURE;
		if (Double.isNaN(red))
			red = UNSURE;
		this.green = this.score = (float) green;
		this.red = this.score1 = (float) red;
	}

	@Deprecated
	ScoreIcon(double score, long count, double part, int size, FontMetrics fm) {
		this.fm = fm;
		if (count == 0L || size == 0) {
			this.score = UNSURE;
			this.score1 = UNSURE;
			red = UNSURE;
			green = UNSURE;
		} else {
			this.score = red = green = (float) (((float) score / count * part + (size - part) * UNSURE) / (float) size);
			this.score1 = this.score;
			if (green <= 0.49f) {
				green = 0.5f;
			} else if (green >= 0.51f) {
				red = 0.5f;
			} else {
				green += 0.01f;
				red -= 0.01f;
			}
		}
	}

	@Override
	public void paintIcon(Component c, Graphics g, int x, int y) {
		g.setColor(c.getBackground());
		g.fillRect(x, y, getIconWidth(), getIconHeight());
		x += 2;
		y += 2;
//		int w = getIconWidth() - 3;
//		g.setColor(Color.white);
//		g.fillRect(x, y, w - 1, getIconHeight() - 2 - 3);
//		g.setColor(LeerdomeinResultsPanel2.RED);
//		g.fillRect(x + Math.round(red * w), y, Math.round((0.5f - red) * w), getIconHeight() - 2 - 3);
//
//		g.setColor(LeerdomeinResultsPanel2.GREEN);
//		g.fillRect(x + Math.round(w / 2.0f), y, Math.round(w * (green - 0.5f)), getIconHeight() - 2 - 3);
//
//		g.setColor(LeerdomeinResultsPanel2.COLOR14); g.drawRect(x, y, getIconWidth()-2, getIconHeight()-2-4);
	
		int w = getIconWidth() - 10;
		g.setColor(new Color(230,220,220));
		g.fillRect(x+5, y+1, Math.round(w / 2.0f), getIconHeight() - 7);
		g.setColor(new Color(220,230,220));
		g.fillRect(x+5 + Math.round(w / 2.0f), y+1, w/2 - 1, getIconHeight() - 7);
		
		//g.fillRect(x, y, w - 1, getIconHeight() - 2 - 3);
		g.setColor(LeerdomeinResultsPanel2.RED);
		g.fillRect(x+5 + Math.round(red * w), y+1, Math.round((0.5f - red) * w), getIconHeight() - 14);

		g.setColor(LeerdomeinResultsPanel2.GREEN);
		g.fillRect(x+5 + Math.round(w / 2.0f), y+1, Math.round(w * (green - 0.5f)), getIconHeight() - 14);
		
		Polygon p = new Polygon();
		if(green>0.5) {
			p.addPoint(7+Math.round(w * green), y+getIconHeight()-12);
			p.addPoint(7+Math.round(w * green)+5, y+getIconHeight()-6);
			p.addPoint(7+Math.round(w * green)-5, y+getIconHeight()-6);
		}
		else if(red<0.5){
			p.addPoint(5+Math.round(w * (0.5f - red)), y+getIconHeight()-12);
			p.addPoint(5+Math.round(w * (0.5f - red))+5, y+getIconHeight()-6);
			p.addPoint(5+Math.round(w * (0.5f - red))-5, y+getIconHeight()-6);
		}
		else{
			p.addPoint(7+Math.round(w * (0.5f)), y+getIconHeight()-12);
			p.addPoint(7+Math.round(w * (0.5f))+5, y+getIconHeight()-6);
			p.addPoint(7+Math.round(w * (0.5f))-5, y+getIconHeight()-6);
		}
		g.setColor(LeerdomeinResultsPanel2.colorBlue1);
		g.fillPolygon(p);
		//g.setColor(LeerdomeinResultsPanel2.COLOR14); g.drawRect(x, y, getIconWidth()-2, getIconHeight()-2-4);
		
		
	}

	@Override
	public int getIconWidth() {
		return 150;
	}

	final FontMetrics fm;

	@Override
	public int getIconHeight() {
		return fm.getHeight() + 4 + 3;
	}

	public String getGreenPercentage() {
		return Math.round(score * 200 - 100) + "%";
	}

	public String getRedPercentage() {
		return -Math.round(score1 * 200 - 100) + "%";
	}

}