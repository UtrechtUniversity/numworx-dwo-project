package nl.uu.fi.dwo.mobile.client.sco;

import java.util.Map;

import com.google.gwt.core.client.GWT;

import nl.uu.fi.dwo.interaction.client.InteractionView;

public class CorrectieFacade {
    static CorrectieFacade instance = GWT.create(CorrectieFacade.class);
    
    public static CorrectieFacade get(Map<String,Object> h, InteractionView view, int maxScore) {
        return instance.create(h, view, maxScore);
    }

    protected CorrectieFacade create(Map<String, Object> h, InteractionView view, int maxScore) {
      return this;
    }
    
    public void correctie(Map<String,Object> state) {}
}
