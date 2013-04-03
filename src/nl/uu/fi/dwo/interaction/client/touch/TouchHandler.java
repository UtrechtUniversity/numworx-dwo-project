package nl.uu.fi.dwo.interaction.client.touch;

public interface TouchHandler extends TouchStartHandler {

	void onTouchMove(TouchMoveEvent event);

	void onTouchEnd(TouchEndEvent event);

	void onTouchCanceled(TouchCancelEvent event);

}
