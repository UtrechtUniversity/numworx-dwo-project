package fi.beans.mainframe;

public interface AudioClip extends java.applet.AudioClip {

	@Deprecated
	public static AudioClip adapt(java.applet.AudioClip old) {
		if (old instanceof AudioClip || old == null) return (AudioClip) old;
		return new Bridge(old);
	}

	@Deprecated
	class Bridge implements AudioClip {
		final private java.applet.AudioClip delegate;

				
		public Bridge(java.applet.AudioClip delegate) {
			this.delegate = delegate;
		}

		/**
		 * 
		 * @see java.applet.AudioClip#play()
		 */
		public void play() {
			delegate.play();
		}

		/**
		 * 
		 * @see java.applet.AudioClip#loop()
		 */
		public void loop() {
			delegate.loop();
		}

		/**
		 * 
		 * @see java.applet.AudioClip#stop()
		 */
		public void stop() {
			delegate.stop();
		}
		
	}

	@Override
	default void play() {
	}

	@Override
	default void loop() {
	}

	@Override
	default void stop() {
	}
	
	
}
