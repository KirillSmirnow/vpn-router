package vpnrouter.api.client;

public interface ClientDetectionService {
    boolean isInProgress();

    void detectAndSave();
}
