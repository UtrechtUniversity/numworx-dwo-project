package nl.numworx.uploadwidgetgwt.client;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

import nl.uu.fi.dwo.interaction.client.InteractionStub;
import nl.uu.fi.dwo.interaction.client.JSONUtilities;
import nl.uu.fi.dwo.interaction.client.OpdrNavIF;
import nl.uu.fi.dwo.interaction.client.Stub;
import nl.uu.fi.dwo.interaction.client.json.ObjectMap;

public class UploadWidgetGWT implements EntryPoint, InteractionStub {

	private OpdrNavIF comRoot;
	private int width, height, asHoogte;
	private final UploadPanel panel = new UploadPanel();
	private boolean volledigeBreedte;

	public UploadWidgetGWT(HashMap<String, Object> h, HashMap<String, Number> randomVarWaarden, int volleBreedte) {
		ObjectMap map = JSONUtilities.wrapMap(h);
		
		if(map != null)
		{
				width = map.getInt("breedte");
				height = map.getInt("hoogte");
				volledigeBreedte = map.getBoolean("volledigeBreedte", false);
		}
		
		if(volledigeBreedte)
			width = volleBreedte;
		Map<String,Object> launchState = Collections.emptyMap();
		if (h != null && h.get("interactiePanelLaunchState") != null)
			launchState = (HashMap<String, Object>) h.get("interactiePanelLaunchState");
				
		//alle gegevens uit launchData halen: 
		init(width, height, launchState, randomVarWaarden);

	}
	public UploadWidgetGWT() {}
		
	@Override
	public void onModuleLoad() {
		RootLayoutPanel.get().add(panel);
		Stub.publish(this);
	}

	@Override
	public HashMap<String, Object> getState() {
		HashMap<String,Object> state = new HashMap<>();
		return state;
	}

	@Override
	public void setState(HashMap<String, Object> h) {
	}

	@Override
	public int getScore() {
		return 0;
	}

	@Override
	public int[][] getScoreObjectives() {
		return null;
	}

	@Override
	public Boolean isCorrect() {
		return null;
	}

	@Override
	public void kijkNa() {
	}

	@Override
	public void zetNagekeken(boolean b) {
	}

	@Override
	public void setCommunicationRoot(OpdrNavIF comRoot) {
		this.comRoot = comRoot;
		panel.setComRoot(comRoot);
	}

	@Override
	public void zetVolledigeBreedte(int breedte) {
	}

	@Override
	public Widget asWidget() {
		return panel;
	}

	@Override
	public int getAsHoogte() {
		return asHoogte;
	}

	@Override
	public int getHeight() {
		return height;
	}

	@Override
	public int getWidth() {
		return width;
	}

	@Override
	public void setAsHoogte(int ashoogte) {
		this.asHoogte = ashoogte;
		
	}

	@Override
	public void init(int width, int height, Map<String, Object> launchData, Map<String, Number> values) {
		this.width = width;
		this.height = height;
	}

}
