package vpnrouter.api.client;

public interface ClientDetectionService {

    void detectAndSave(CompletionListener completionListener);

    interface CompletionListener {

        void onAlreadyRunning();

        void onNewClientsNotFound();

        void onNewClientsFound(int newClientsCount);

        void onFailure(Exception exception);
    }
}
