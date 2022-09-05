/*
 * Copyright (c) 2009 Johann Prieur
 *
 * Permission is hereby granted, free of charge, to any person
 * obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without
 * restriction, including without limitation the rights to use,
 * copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following
 * conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 */
package com.stanziq.strophe.client;

import com.google.gwt.core.client.JavaScriptObject;

public class Logging {

	public enum Level {
		DEBUG,
		INFO,
		WARN,
		ERROR,
		FATAL;
	}

	public abstract static class Logger {
		public abstract void log(Level level, String message);

		@SuppressWarnings("unused")
		private void log(int code, String message) {
			Level level = null;
			for(Level l : Level.values())
				if(l.ordinal() == code)
					level = l;
			log(level, message);
		}

		native JavaScriptObject wrapper() /*-{
			var logger = this;
			return function(code, message) {
				logger.@com.stanziq.strophe.client.Logging.Logger::log(ILjava/lang/String;)(code, message);
			}
		}-*/;
	}

	public native static void setLogger(Logger logger) /*-{
		var l = logger.@com.stanziq.strophe.client.Logging.Logger::wrapper()();
		$wnd.Strophe.log = l;
	}-*/;

	public static void log(Level level, String message) {
		log(level.ordinal(), message);
	}

	private native static void log(int level, String message) /*-{
		$wnd.Strophe.log(level, message);
	}-*/;

	public native static void debug(String message) /*-{
		$wnd.Strophe.debug(message);
	}-*/;

	public native static void info(String message) /*-{
		$wnd.Strophe.info(message);
	}-*/;

	public native static void warn(String message) /*-{
		$wnd.Strophe.debug(message);
	}-*/;

	public native static void error(String message) /*-{
		$wnd.Strophe.debug(message);
	}-*/;

	public native static void fatal(String message) /*-{
		$wnd.Strophe.debug(message);
	}-*/;

}
