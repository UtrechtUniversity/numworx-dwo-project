package fi.beans.copyright;

import java.awt.*;
import java.awt.event.*;

/*

klassen voor copyright statement:

InfoFrame: bevat de copyright statement en het FI-logo

FIButton: een light weight button met het FI-logo om het InfoFrame op te roepen

InfoLabel: een light weight label gebruikt in het InfoFrame

De constructors van InfoFrame en FIButton hebben als argumenten:

String titel en String[] text

titel is de applet-titel (deze wordt in een groter font weergegeven)
String[] text bevat (steeds een regel):
versie-info (b.v de datum)
auteur(s): xxxx
programmeur(s) xxxx
Freudenthal Instituut
www.fisme.science.uu.nl

Bij een applet in een Frame kan het InfoFrame via een menu worden opgeroepen,
de menu-aktie moet dan zijn: creeer het InfoFrame en maak het zichtbaar

Bij een applet zonder Frame wordt de FIButton gebruikt, deze creert zelf het InfoFrame

Het InfoFrame wordt vernietigd door het te sluiten of ergens anders op het applet te klikken
(de akties zijn WindowClosing en WindowDeactivated).

Op de FIButton wordt het FI-logo getekend, het logo is minimaal 12 x 20 pixels groot,
het wordt gecentreerd als de FIButton groter is, en het wordt vergroot zodra dit mogelijk is.

*/

// class representing an information frame
public class InfoFrame extends Frame
{
    // for addNotify, see below
    boolean fComponentsAdjusted = false;
    String[] textLines;
    FontMetrics fm;
    Font fo;
    int frameWidth = 250;
    int spacing = 10;
    public InfoFrame(String titel, String[] text)
    {   super(titel);
        setLayout(null);
        textLines = text;
        // vertical layout counter
        int currentY = spacing;
        // first text line in bigger font
        fo = new Font("Dialog", Font.BOLD, 16);
        fm = getFontMetrics(fo);
        InfoLabel l = new InfoLabel(textLines[0]);
        l.setFont(fo);
        l.setBounds(0, currentY, frameWidth, fm.getHeight());
        add(l);
        currentY += spacing + fm.getHeight();
        // change to smaller font
        fo = new Font("Dialog", Font.BOLD, 12);
        fm = getFontMetrics(fo);
        for (int i = 1; i < textLines.length; i++)
        {   l = new InfoLabel(textLines[i]);
            l.setFont(fo);
            l.setBounds(0, currentY, frameWidth, fm.getHeight());
            add(l);
            currentY += spacing + fm.getHeight();
        }
        setSize(frameWidth, currentY);
        setResizable(false);
        // add window listener
        addWindowListener(new WL());

        setBackground(new Color(230, 230, 230));
    }

    // overridden addNotify to correct for frame borders
  public void addNotify()
  {   // Record the size of the window prior to calling parent addNotify
      Dimension d = getSize();
    super.addNotify();
    if (fComponentsAdjusted)
      return;
    // adjust components according to the insets
    setSize(insets().left + insets().right + d.width, insets().top + insets().bottom + d.height);
    Component components[] = getComponents();
    for (int i = 0; i < components.length; i++) {
      Point p = components[i].getLocation();
      p.translate(insets().left, insets().top);
      components[i].setLocation(p);
    }
    fComponentsAdjusted = true;
  }

  public void paint(Graphics g)
  {
      int height = 3 * spacing + fm.getHeight();
      drawFILogo(g, insets().left + 10,
                 getSize().height - insets().bottom - height - 10,
                 height);
      super.paint(g);
  }

  public static void drawFILogo(Graphics g, int x, int y, int height)
  {   int unit = height / 20;
      Polygon gray1 = new Polygon(); // OK
      gray1.addPoint(x + 3 * unit, y + 4 * unit);
      gray1.addPoint(x + 6 * unit, y + 3 * unit);
      gray1.addPoint(x + 9 * unit, y + 4 * unit);
      gray1.addPoint(x + 6 * unit, y + 5 * unit);
      Polygon gray2 = new Polygon(); // OK
      gray2.addPoint(x + 3 * unit, y + 10 * unit);
      gray2.addPoint(x + 6 * unit, y + 9 * unit);
      gray2.addPoint(x + 9 * unit, y + 10 * unit);
      gray2.addPoint(x + 6 * unit, y + 11 * unit);
      Polygon gray3 = new Polygon();
      gray3.addPoint(x, y + 19 * unit);
      gray3.addPoint(x + 3 * unit, y + 18 * unit);
      gray3.addPoint(x + 6 * unit, y + 19 * unit);
      gray3.addPoint(x + 3 * unit, y + 20 * unit);
      Polygon gray4 = new Polygon();
      gray4.addPoint(x + 6 * unit, y + 19 * unit);
      gray4.addPoint(x + 9 * unit, y + 18 * unit);
      gray4.addPoint(x + 12 * unit, y + 19 * unit);
      gray4.addPoint(x + 9 * unit, y + 20 * unit);

      Polygon red1 = new Polygon();
      red1.addPoint(x + 6 * unit, y + 5 * unit);
      red1.addPoint(x + 9 * unit, y + 4 * unit);
      red1.addPoint(x + 9 * unit, y + 7 * unit);
      red1.addPoint(x + 6 * unit, y + 6 * unit);
      Polygon red2 = new Polygon(); // OK
      red2.addPoint(x, y + 2 * unit);
      red2.addPoint(x + 6 * unit, y);
      red2.addPoint(x + 6 * unit, y + 3 * unit);
      red2.addPoint(x, y + 5 * unit);
      Polygon red3 = new Polygon();
      red3.addPoint(x, y + 5 * unit);
      red3.addPoint(x + 3 * unit, y + 4 * unit);
      red3.addPoint(x + 3 * unit, y + 7 * unit);
      red3.addPoint(x, y + 8 * unit);
      Polygon red4 = new Polygon();
      red4.addPoint(x, y + 8 * unit);
      red4.addPoint(x + 6 * unit, y + 6 * unit);
      red4.addPoint(x + 6 * unit, y + 9 * unit);
      red4.addPoint(x, y + 11 * unit);
      Polygon red5 = new Polygon();
      red5.addPoint(x, y + 11 * unit);
      red5.addPoint(x + 3 * unit, y + 10 * unit);
      red5.addPoint(x + 3 * unit, y + 18 * unit);
      red5.addPoint(x, y + 19 * unit);
      Polygon red6 = new Polygon();
      red6.addPoint(x + 6 * unit, y + 11 * unit);
      red6.addPoint(x + 9 * unit, y + 10 * unit);
      red6.addPoint(x + 9 * unit, y + 18 * unit);
      red6.addPoint(x + 6 * unit, y + 19 * unit);

      Polygon white1 = new Polygon();
      white1.addPoint(x + 6 * unit, y);
      white1.addPoint(x + 12 * unit, y + 2 * unit);
      white1.addPoint(x + 12 * unit, y + 5 * unit);
      white1.addPoint(x + 6 * unit, y + 3 * unit);
      Polygon white2 = new Polygon();
      white2.addPoint(x + 3 * unit, y + 4 * unit);
      white2.addPoint(x + 6 * unit, y + 5 * unit);
      white2.addPoint(x + 6 * unit, y + 6 * unit);
      white2.addPoint(x + 3 * unit, y + 7 * unit);
      Polygon white3 = new Polygon();
      white3.addPoint(x + 3 * unit, y + 10 * unit);
      white3.addPoint(x + 6 * unit, y + 11 * unit);
      white3.addPoint(x + 6 * unit, y + 19 * unit);
      white3.addPoint(x + 3 * unit, y + 18 * unit);
      Polygon white4 = new Polygon();
      white4.addPoint(x + 9 * unit, y + 4 * unit);
      white4.addPoint(x + 12 * unit, y + 5 * unit);
      white4.addPoint(x + 12 * unit, y + 8 * unit);
      white4.addPoint(x + 9 * unit, y + 7 * unit);
      Polygon white5 = new Polygon();
      white5.addPoint(x + 6 * unit, y + 6 * unit);
      white5.addPoint(x + 12 * unit, y + 8 * unit);
      white5.addPoint(x + 12 * unit, y + 11 * unit);
      white5.addPoint(x + 6 * unit, y + 9 * unit);
      Polygon white6 = new Polygon();
      white6.addPoint(x + 9 * unit, y + 10 * unit);
      white6.addPoint(x + 12 * unit, y + 11 * unit);
      white6.addPoint(x + 12 * unit, y + 19 * unit);
      white6.addPoint(x + 9 * unit, y + 18 * unit);

      g.setColor(Color.gray);
      g.fillPolygon(gray1);
      g.fillPolygon(gray2);
      g.fillPolygon(gray3);
      g.fillPolygon(gray4);

      g.setColor(Color.red);
      g.fillPolygon(red1);
      g.fillPolygon(red2);
      g.fillPolygon(red3);
      g.fillPolygon(red4);
      g.fillPolygon(red5);
      g.fillPolygon(red6);

      g.setColor(Color.white);
      g.fillPolygon(white1);
      g.fillPolygon(white2);
      g.fillPolygon(white3);
      g.fillPolygon(white4);
      g.fillPolygon(white5);
      g.fillPolygon(white6);
  }
  // listener class for button and window events
    class WL extends WindowAdapter
    {   public void windowClosing(WindowEvent e)
        {   dispose();
        }
        public void windowDeactivated(WindowEvent e)
        {   dispose();
        }

    } // class WAL
} // class InfoFrame

class InfoLabel extends Component
{   String text;
    public InfoLabel(String t)
    {   text = t;
    }
    public void paint(Graphics g)
    {   g.setColor(Color.black);
        g.setFont(getFont());
        FontMetrics fm = getFontMetrics(g.getFont());
        int by = fm.getHeight() - fm.getDescent();
        drawCenteredString(g, text, by);
    }
    public void drawCenteredString(Graphics g, String s, int by)
    {   FontMetrics fm = getFontMetrics(g.getFont());
        int bx = 0;
        int horSpace = getSize().width - fm.stringWidth(s);
        if (horSpace > 0)
            bx = horSpace / 2;
        g.drawString(s, bx, by);
    }
} // class InfoLabel
