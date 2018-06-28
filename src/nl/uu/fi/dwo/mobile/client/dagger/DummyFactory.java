package nl.uu.fi.dwo.mobile.client.dagger;

import dagger.Binds;
import dagger.Module;
import nl.uu.fi.dwo.mobile.client.ui.ClientFactory;
import nl.uu.fi.dwo.mobile.client.ui.DummyClientFactory;
import nl.uu.fi.dwo.mobile.client.ui.DummyRPCHandler;
import nl.uu.fi.dwo.mobile.client.ui.RPCHandler;

@Module
public abstract class DummyFactory {
  @Binds abstract ClientFactory clientFactory(DummyClientFactory dummy);
  @Binds abstract RPCHandler  rpcHandler(DummyRPCHandler dummy);
}
