package fi.beans.mainframe;

import java.util.function.BooleanSupplier;

import fi.dwo.eawt.EAWT;

public class Java9Adapter {
  private Java9Adapter() {}

  static class Quit implements BooleanSupplier {
      final MainFrame main;
      private Quit(MainFrame m) { main = m; }
      @Override
      public boolean getAsBoolean() {
        main.quit0();
        return true;
      }
      
      private boolean install() {
        try {
          EAWT eawt = (EAWT) Class.forName("fi.dwo.eawt.impl.EAWTImpl").newInstance();
          eawt.setQuit(this);
          return false;
        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
          return true;
        }
      }
  }
  
  
  public static boolean setQuitHandler(MainFrame mainFrame) {
    try {
      return new Quit(mainFrame).install();
    } catch(Throwable e) {
      return true;
    }
  }
  
  
}
