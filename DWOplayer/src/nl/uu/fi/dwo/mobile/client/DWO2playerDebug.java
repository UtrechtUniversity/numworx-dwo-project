package nl.uu.fi.dwo.mobile.client;

import javax.inject.Inject;
import javax.inject.Provider;

import nl.uu.fi.dwo.account.client.DwoGlobalVars;
import nl.uu.fi.dwo.mobile.client.ui.IdleDetect;
import nl.uu.fi.dwo.mobile.client.ui.RPCHandler;

public class DWO2playerDebug extends DWO2playerDefaults {

	@Inject DWO2playerDebug(IdleDetect idle) {
		super(idle);
	}

	@Override
	public String getStubView() {
		return "";
	}

}
