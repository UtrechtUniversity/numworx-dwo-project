package nl.uu.fi.dwo.keyboard.client;

public interface KeyboardFactory {

  AbstractKeyboard getKeyboard();

  void setPremium(boolean premium);
  
  default void setCombinedState(CombinedState state) { } 

}
