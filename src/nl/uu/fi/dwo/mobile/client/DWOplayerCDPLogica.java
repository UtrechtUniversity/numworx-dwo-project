package nl.uu.fi.dwo.mobile.client;

public class DWOplayerCDPLogica extends DWOplayerNoordhoff {
	@Override
	public String getResource(String resource) {
		//return "http://www.fisme.science.uu.nl/dwo/apps/noordhoff/" + resource;
		return "http://cdplogica.toegang.nu/noordhoff/vo/fi/dwo/2014_v1_0/" + resource;
	}

}
