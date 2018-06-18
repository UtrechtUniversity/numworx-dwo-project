package fi.dwo.gwt.lib.rest.util;

import com.google.gwt.core.client.JsArrayString;

/**
 * https://stackoverflow.com/questions/3126232/string-formatter-in-gwt/7127768
 * 
 * @author plas0006
 */
public abstract class StringFormatter {
    public static String format(final String format, final Object... args) {
        if (null == args || 0 == args.length)
            return format;
        JsArrayString array = newArray();
        for (Object arg : args) {
            array.push(String.valueOf(arg)); // TODO: smarter conversion?
        }
        return nativeFormat(format, array);
    }

    private static native JsArrayString newArray()/*-{
        return [];
    }-*/;

    private static native String nativeFormat(final String format, final JsArrayString args)/*-{
        return format.replace(/{(\d+)}/g, function(match, number) {
            return typeof args[number] != 'undefined' ? args[number] : match;
        });
    }-*/;
}