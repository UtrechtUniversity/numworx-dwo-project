package nl.uu.fi.dwo.lms.gwtclient.gwt.studentmodel;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import com.google.gwt.editor.client.LeafValueEditor;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class FilterSettings extends Composite implements LeafValueEditor<Map<String, Map<String, Set<Integer>>>>{

	private static FilterSettingsUiBinder uiBinder = GWT.create(FilterSettingsUiBinder.class);

	interface FilterSettingsUiBinder extends UiBinder<Widget, FilterSettings> {
	}

	@Inject public FilterSettings() {
		initWidget(uiBinder.createAndBindUi(this));
		gr1s = new CheckBox[] { gr1, gr11, gr12, gr13, gr14, gr15, gr16,gr17, gr18, gr19};
		gr2s = new CheckBox[] { gr2, gr21, gr22, gr23, gr24, gr25, gr26,gr27, gr28};
		gr3s = new CheckBox[] { gr3, gr31, gr32, gr33, gr34, gr35, gr36,gr37, gr38, gr39};

		mw1s = new CheckBox[] { mw1, mw11, mw12, mw13, mw14, mw15, mw16,mw17, mw18, mw19, mw110, mw111, mw112, mw113};
		mw2s = new CheckBox[] { mw2, mw21, mw22, mw23, mw24, mw25, mw26,mw27, mw28, mw29, mw210, mw211, mw212};
		mw3s = new CheckBox[] { mw3, mw31, mw32, mw33, mw34, mw35, mw36,mw37, mw38, mw39, mw310, mw311, mw312, mw313, mw314};

	
	}

	@UiField CheckBox gr, 
		gr1, gr11, gr12, gr13, gr14, gr15, gr16,gr17, gr18, gr19,
		gr2, gr21, gr22, gr23, gr24, gr25, gr26,gr27, gr28, 
		gr3, gr31, gr32, gr33, gr34, gr35, gr36,gr37, gr38, gr39;
	
	CheckBox gr1s[], gr2s[], gr3s[];
	
	@UiHandler("gr") 
	void grChange(ValueChangeEvent<Boolean> e) {
		boolean value = e.getValue();
		setValue(value, gr1s);
		setValue(value, gr2s);
		setValue(value, gr3s);
	}

	@UiHandler("gr1") 
	void gr1Change(ValueChangeEvent<Boolean> e) {
		boolean value = e.getValue();
		setValue(value, gr1s);
	}

	@UiHandler("gr2") 
	void gr2Change(ValueChangeEvent<Boolean> e) {
		boolean value = e.getValue();
		setValue(value, gr2s);
	}
	@UiHandler("gr3") 
	void gr3Change(ValueChangeEvent<Boolean> e) {
		boolean value = e.getValue();
		setValue(value, gr3s);
	}

	@UiField CheckBox mw, 
	mw1, mw11, mw12, mw13, mw14, mw15, mw16,mw17, mw18, mw19, mw110, mw111, mw112, mw113,
	mw2, mw21, mw22, mw23, mw24, mw25, mw26,mw27, mw28, mw29, mw210, mw211, mw212,
	mw3, mw31, mw32, mw33, mw34, mw35, mw36,mw37, mw38, mw39, mw310, mw311, mw312, mw313, mw314;

	CheckBox mw1s[], mw2s[], mw3s[];
	
	@UiField CheckBox rest;

	@UiHandler("mw") 
	void mwChange(ValueChangeEvent<Boolean> e) {
		boolean value = e.getValue();
		setValue(value, mw1s);
		setValue(value, mw2s);
		setValue(value, mw3s);
	}

	@UiHandler("mw1") 
	void mw1Change(ValueChangeEvent<Boolean> e) {
		boolean value = e.getValue();
		setValue(value, mw1s);
	}

	@UiHandler("mw2") 
	void mw2Change(ValueChangeEvent<Boolean> e) {
		boolean value = e.getValue();
		setValue(value, mw2s);
	}
	@UiHandler("mw3") 
	void mw3Change(ValueChangeEvent<Boolean> e) {
		boolean value = e.getValue();
		setValue(value, mw3s);
	}

	
	private void setValue(boolean value, CheckBox[] cbs) {
		for(CheckBox cb: cbs) { cb.setValue(value); }		
	}

	@Override
	public void setValue(Map<String, Map<String, Set<Integer>>> value) {
		rest.setValue(value.containsKey("")||value.containsKey(null));
		Map<String, Set<Integer>> GenR = value.getOrDefault("Getal&Ruimte", Collections.emptyMap());
		gr.setValue(false, true);
		setValue(GenR, "1HV", gr1s);
		setValue(GenR, "2HV", gr2s);
		setValue(GenR, "3V", gr3s);
		gr.setValue(gr1.isChecked() && gr2.isChecked() && gr3.isChecked());

		Map<String, Set<Integer>> MW = value.getOrDefault("Moderne Wiskunde", Collections.emptyMap());
		mw.setValue(false, true);
		setValue(MW, "1HV", mw1s);
		setValue(MW, "2HV", mw2s);
		setValue(MW, "3V", mw3s);
		mw.setValue(mw1.isChecked() && mw2.isChecked() && mw3.isChecked());
	}

	private void setValue(Map<String, Set<Integer>> GenR, String hfst, CheckBox[] g) {
		Set<Integer> h1 = GenR.get(hfst);
		if (h1 == null) return;
		if (h1.isEmpty()) {
			setValue(true,g);
			return;
		}
		h1.forEach(item -> g[item.intValue()].setValue(true));
		g[0].setValue(h1.size() == g.length-1);
	}
	
	private Set<Integer> getValue(CheckBox[] g) {
		Set<Integer> set = new TreeSet<>();
		for (int i = 1; i < g.length; i++) {
			if (g[i].isChecked()) set.add(i);
		}
		return set;
	}

	@Override
	public Map<String, Map<String, Set<Integer>>> getValue() {
		Map<String, Map<String,Set<Integer>>> result = new HashMap<>();
		Map<String, Set<Integer>> GenR = new HashMap<>();
		Set<Integer> h;
		h = getValue(gr1s);if (!h.isEmpty()) {
			if (h.size() == gr1s.length-1) h = Collections.emptySet();
			GenR.put("1HV", h);
		}
		h = getValue(gr2s);if (!h.isEmpty()) {
			if (h.size() == gr2s.length-1) h = Collections.emptySet();
			GenR.put("2HV", h);
		}
		h = getValue(gr3s);if (!h.isEmpty()) {
			if (h.size() == gr3s.length-1) h = Collections.emptySet();
			GenR.put("3V", h);
		}
		if (!GenR.isEmpty()) result.put("Getal&Ruimte", GenR);
		Map<String, Set<Integer>> MW = new HashMap<>();
		h = getValue(mw1s);if (!h.isEmpty()) {
			if (h.size() == mw1s.length-1) h = Collections.emptySet();
			MW.put("1HV", h);
		}
		h = getValue(mw2s);if (!h.isEmpty()) {
			if (h.size() == mw2s.length-1) h = Collections.emptySet();
			MW.put("2HV", h);
		}
		h = getValue(mw3s);if (!h.isEmpty()) {
			if (h.size() == mw3s.length-1) h = Collections.emptySet();
			MW.put("3V", h);
		}
		if (!MW.isEmpty()) result.put("Moderne Wiskunde", MW);
		
		if (rest.isChecked()) result.put("", Collections.emptyMap());
		
		return result;
	}
}
