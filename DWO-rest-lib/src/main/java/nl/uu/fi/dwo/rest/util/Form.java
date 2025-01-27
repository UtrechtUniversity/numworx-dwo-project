package nl.uu.fi.dwo.rest.util;

public enum Form { phone, tablet, desktop;
	
	
	static public Form getFormFactor(String userAgent) {
		if (userAgent == null) return Form.desktop;
		userAgent = userAgent.toLowerCase();
		Form form = Form.desktop;
    	if(userAgent.contains("iphone")|| userAgent.contains("ipod")) form = Form.phone;
    	else if (userAgent.contains("ipad")) form = Form.tablet;
    	else if (userAgent.contains("android")) {
    		if (userAgent.contains("mobile"))
    			form = Form.phone;
    		else 
    			form = Form.tablet;
    	}
		return form;
	}

}