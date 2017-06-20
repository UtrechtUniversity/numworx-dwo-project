package fi.dwo.server.persistence;

import fi.beans.scorm.ScormAdapter;

public class CmiConvert extends ScormAdapter {

    public CmiConvert() {
        super(true);
    }

    @Override
    public String GetValue(String cmiElement) {
        return null;
    }

    @Override
    public String SetValue(String key, String value) {
        return null;
    }

    public long from1_2Timex(String str) {
        return super.from1_2Time(str);
    }

    public String to1_2Timex(long time) {
        return super.to1_2Time(time);
    }

    public String to2004Timex(long time) {
        return super.to2004Time(time);
    }

	@Override
	public long from2004Time(String str) {
		return super.from2004Time(str);
	}
    
}