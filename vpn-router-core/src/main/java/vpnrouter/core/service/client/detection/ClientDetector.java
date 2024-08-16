package vpnrouter.core.service.client.detection;

import vpnrouter.core.service.client.Client;

import java.util.List;

public interface ClientDetector {

    List<Client> detect();
}
