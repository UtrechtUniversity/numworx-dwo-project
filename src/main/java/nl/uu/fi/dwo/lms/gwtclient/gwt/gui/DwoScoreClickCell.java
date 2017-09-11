package nl.uu.fi.dwo.lms.gwtclient.gwt.gui;

import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;

/**
 *
 * @author G.A.J. van der Plas
 */
public class DwoScoreClickCell extends DwoCell {

    SelectedCellHandler handler;
    String style;

    public DwoScoreClickCell() {
        super();
        style = null;
    }

    public DwoScoreClickCell(String style) {

    }

    @Override
    public void render(com.google.gwt.cell.client.Cell.Context context, String value, SafeHtmlBuilder sb) {
        if (value != null) {
            if (style != null) {
                sb.appendHtmlConstant(coloredScore(value));
            } else {
                sb.appendHtmlConstant(coloredScore(value));
            }
        }
    }

    @Override
    public void onBrowserEvent(com.google.gwt.cell.client.Cell.Context context, Element parent, String value, NativeEvent event, ValueUpdater<String> valueUpdater) {
        if (value == null) {
            return;
        }
        super.onBrowserEvent(context, parent, value, event, valueUpdater);
        if ("click".equals(event.getType())) {
            if (handler != null) {
                handler.onSelectedCell(context, value);
            }
        }
    }

    public void addSelectedCellHandler(SelectedCellHandler aHandler) {
        handler = aHandler;
    }

    private String coloredScore(String value) {
        SafeHtmlBuilder sb = new SafeHtmlBuilder();
        
        try {
            double score = Integer.parseInt(value);
            String color = "red";
            if (score > 10.0 && score < 60.0) {
                color = "orange";
            } else if (score >= 60) {
                color = "green";
            }
            String prefix;
            if (score > 0) {
                int r, g, b;
                b = 0;
                g = (int) (255 * (score / 50));
                r = (int) (255 * (1 - (score - 50) / 50));
                prefix = "<div style=\"text-align: right; padding: 2px; background:rgb(" + r + "," + g + "," + b + ");\">";
            } else {
                prefix = "<div style=\"text-align: right; padding: 2px; overflow auto;\">"; // use default of style
            }
            long iScore = Math.round(score);
            StringBuilder builder = new StringBuilder(prefix);
            builder.append(value);
            builder.append("</div>");
            return builder.toString();
        } catch (Exception e) {
            return value;
        }
    }
}
