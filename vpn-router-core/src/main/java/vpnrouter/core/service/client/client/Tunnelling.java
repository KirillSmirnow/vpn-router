package vpnrouter.core.service.client.client;

public interface Tunnelling {

    void switchOnFor(String ipAddress);

    void switchOffFor(String ipAddress);
}
