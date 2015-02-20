package fi.beans.copyright;

import java.awt.*;
import java.awt.event.*;

public class FIButton extends Component
{   InfoFrame infoFrame;
    String titel;
    String[] text;
    public FIButton(String titel, String[] text)
    {   this.titel = titel;
        this.text = text;
        addMouseListener(new FIML());
    }

    public void paint(Graphics g)
    {   // logo
        int logoHeight = (getSize().height / 20) * 20;
        int logoWidth = 3 * (logoHeight / 5);
        int bx = (getSize().width - logoWidth) / 2;
        int by = (getSize().height - logoHeight) / 2;
        InfoFrame.drawFILogo(g, bx, by, logoHeight);
        // button outline
        /*g.setColor(Color.white);
        g.drawLine(0, 0, getSize().width - 1, 0);
        g.drawLine(0, 0, 0, getSize().height - 1);
        g.setColor(Color.black);
        g.drawLine(getSize().width - 1, 0,
                   getSize().width - 1, getSize().height - 1);
        g.drawLine(0, getSize().height - 1,
                   getSize().width - 1, getSize().height - 1);*/
    }
    // inner class
    class FIML extends MouseAdapter
    {   public void mousePressed(MouseEvent e)
        {   infoFrame = new InfoFrame(titel, text);
            infoFrame.setVisible(true);
        }
    }
} // class FIButton
