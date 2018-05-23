package nl.uu.fi.dwo.mobile.client.sco;

import java.util.Map;

import com.google.gwt.core.client.GWT;

import nl.uu.fi.dwo.interaction.client.InteractionView;

public class CorrectieFacade {
    private static CorrectieFacade instance = GWT.create(CorrectieFacade.class);
    private static CorrectieFacade NULL = new CorrectieFacade();
    
    public static CorrectieFacade get(Map<String,Object> h, InteractionView view, int maxScore) {
        return instance.create(h, view, maxScore);
    }

    protected CorrectieFacade create(Map<String, Object> h, InteractionView view, int maxScore) {
      return NULL;
    }
    
    public void correctie(Map<String,Object> state) {}
}
