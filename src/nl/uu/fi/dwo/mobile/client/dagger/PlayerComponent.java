package nl.uu.fi.dwo.mobile.client.dagger;

import dagger.Component;
import nl.uu.fi.dwo.mobile.DWOplayer;

@Component(modules = { PlayerModule.class })
public interface PlayerComponent {
  void inject(DWOplayer dwo);
}
