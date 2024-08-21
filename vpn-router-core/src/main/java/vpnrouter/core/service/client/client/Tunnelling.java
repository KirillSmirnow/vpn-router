package vpnrouter.core.service.client.client;

import java.util.Set;

public interface Tunnelling {

    void switchOnOnlyFor(Set<String> ipAddresses);
}
