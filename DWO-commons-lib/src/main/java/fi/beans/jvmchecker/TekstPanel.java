package fi.beans.jvmchecker;

import java.awt.*;

public class TekstPanel extends Panel {

    int aantalRegels;
    Label[] labels;
    int regelhoogte;

    public TekstPanel(int fontSize, int x, int y, int b, int h) {
        setLayout(null);
        setBackground(Color.white);
        setBounds(x, y, b, h);
        regelhoogte = fontSize + fontSize / 5;
        aantalRegels = h / regelhoogte;
        labels = new Label[aantalRegels];
        for (int i = 0; i < aantalRegels; i++) {
            labels[i] = new Label();
            labels[i].setBounds(0, i * regelhoogte, b, regelhoogte);
            labels[i].setFont(new Font("SansSerif", Font.PLAIN, fontSize));
            add(labels[i]);
        }
    }

    public void setText(int regelnummer, String s) {
        labels[regelnummer].setText(s);
    }

    public void setText(int regelnummer, int marge, String s) {
        labels[regelnummer].setLocation(marge, labels[regelnummer].getLocation().y);
        labels[regelnummer].setText(s);

    }
}
