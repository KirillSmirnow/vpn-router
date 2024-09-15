package vpnrouter.api.client;

public interface ClientDetectionService {

    void detectAndSave();

    boolean isInProgress();
}
