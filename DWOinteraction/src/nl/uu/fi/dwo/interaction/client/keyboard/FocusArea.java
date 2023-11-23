package nl.uu.fi.dwo.interaction.client.keyboard;

import java.util.logging.Logger;

import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.TextArea;

@SuppressWarnings("deprecation")
class FocusArea extends TextArea {
	private static final Logger LOG = Logger.getLogger("FocusArea");
	private final FocusOnTouch handler;

	static native String getClipboardData(Event event) /*-{
		return event.clipboardData.getData('text/plain'); 
	}-*/;

	FocusArea(FocusOnTouch handler) {
		this.handler = handler;
		sinkEvents(Event.ONPASTE); 
		getElement().getStyle().setZIndex(-100);
		registerOnCutCopy(getElement());
	}

	@Override
	public void onBrowserEvent(Event event) {
		int type = event.getTypeInt();
		if (type == Event.ONPASTE) {
			
			String data = getClipboardData(event);
			handler.onPaste(data);
			event.preventDefault();
			return;
		}
		
		super.onBrowserEvent(event);
	}

	   private native void registerOnCutCopy(Element element)
	    /*-{
	        var that = this;
	        element.oncut = $entry(function(event)
	        {
	            that.@nl.uu.fi.dwo.interaction.client.keyboard.FocusArea::doCut(Lcom/google/gwt/user/client/Event;)(event);
	            return false;
	        });
	        element.oncopy = $entry(function(event)
	        {
	            that.@nl.uu.fi.dwo.interaction.client.keyboard.FocusArea::doCopy(Lcom/google/gwt/user/client/Event;)(event);
	            return false;
	        });
	        element.oninput = $entry(function(event)
	        {
	        	that.@nl.uu.fi.dwo.interaction.client.keyboard.FocusArea::doInput(Lcom/google/gwt/user/client/Event;)(event);
				return false;
	        });
	    }-*/;

	    @SuppressWarnings("unused")
	    private void doCut(Event event)
	    {
	        // substitute your own 'cut' function here
	        String clip = handler.doCut();
	        // and attempt to shove that text into the clipboard
	        setClipboardData(event, clip);
	    }
	    @SuppressWarnings("unused")
	    private void doCopy(Event event)
	    {
	        // substitute your own 'cut' function here
	        String clip = handler.doCopy();
	        // and attempt to shove that text into the clipboard
	        setClipboardData(event, clip);
	    }
	    
	    private void doInput(Event event) {
	    	String text = getData(event);
	    	String inputType = getInputType(event);
	    	LOG.info("do Input t=" + text + " i=" + inputType);
	    	if (text != null && !text.isEmpty())
	    	{	if ("insertText".equals(inputType))
	    			handler.doInput(text);
	    	    setText("");
	    	}
	    	
	    }
	    
	    private native static String getData(Event event) /*-{
	    	return event.data;
	    }-*/;

	    private native static String getInputType(Event event) /*-{
    		return event.inputType;
    	}-*/;

	    public static native void setClipboardData(Event event, String text)
	    /*-{
	        // Apple says this should work:
	        // http://devworld.apple.com/mac/library/documentation/AppleApplications/Conceptual/SafariJSProgTopics/Tasks/CopyAndPaste.html
	        // But it's broken...here's the bug report:
	        // https://bugs.webkit.org/show_bug.cgi?id=17645
	        if (event && event.clipboardData) // WebKit (Chrome/Safari)
	        {
	            try
	            {
	                event.clipboardData.setData("text/plain", text);
	                return;
	            }
	            catch (e)
	            {
	                // Hmm, that didn't work.
	            }
	        }

	        // http://msdn.microsoft.com/en-us/library/ms535220(VS.85).aspx
	        if ($wnd.clipboardData) // IE
	        {
	            try
	            {
	                $wnd.clipboardData.setData("Text", text);
	                return;
	            }
	            catch (e)
	            {
	                // Hmm, that didn't work.
	            }
	        }
	    }-*/;
}