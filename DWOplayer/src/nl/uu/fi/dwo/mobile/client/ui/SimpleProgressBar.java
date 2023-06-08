package nl.uu.fi.dwo.mobile.client.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;

public class SimpleProgressBar extends Composite  {

        public interface StatusCellSafeHTMLTemplate extends SafeHtmlTemplates {
                @Template("<div class='statusbar' ><div style=\"font-size:medium;height:1.2em;width:100%;cursor:default;border:thin #7ba5d5 solid;\">"
                                + "<div style=\"height:1.2em;width:{0}%; background:#8cb6e6;\">"
                                + "</div><div style=\"height:1.2em; margin:-1.2em;font-weight:bold;color:#4e7fba;\">"
                                + "<center>{0}%</center></div></div></div>")
                                SafeHtml status(int percentage);
                @Template("<div><div style=\"font-size:medium;height:1.2em;width:100%;cursor:default;border:thin #7ba5d5 solid;\">"
                        + "<div style=\"height:1.2em;width:{0}%; background:#8cb6e6;\">"
                        + "</div><div style=\"height:1.2em; margin:-1.2em;font-weight:bold;color:#4e7fba;\">"
                        + "<center>\u00A0</center></div></div></div>")
                        SafeHtml nostatus(int percentage);
        }

        final static StatusCellSafeHTMLTemplate statusCellSafeHTMLTemplate = (StatusCellSafeHTMLTemplate) GWT
        .create(StatusCellSafeHTMLTemplate.class);

        final private HTML widget = new HTML();

        private int progress;
        //private boolean status;

        public SimpleProgressBar(int i) {
                initWidget(widget);
                setProgress(i);
        }

//        public SimpleProgressBar(int i, boolean status)
//        {
//        		initWidget(widget);
//        		this.status = status;
//        		setProgress(i);
//        }
        
        public int getProgress() {
                return progress;
        }

        public void setProgress(final int progress) {
                this.progress = progress;
                widget.setHTML(
//                		status ? 
//                			statusCellSafeHTMLTemplate.status(progress) :
                			statusCellSafeHTMLTemplate.nostatus(progress));
        }

}