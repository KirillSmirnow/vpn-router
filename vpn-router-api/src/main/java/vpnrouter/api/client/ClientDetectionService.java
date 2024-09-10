package vpnrouter.api.client;

public interface ClientDetectionService {

    void detectAndSave(StartListener startListener, CompletionListener completionListener);

    interface StartListener {
        void onStart();
    }

    interface CompletionListener {

        void onAlreadyRunning();

        void onNewClientsNotFound();

        void onNewClientsFound(int newClientsCount);

        void onFailure(Exception exception);
    }
}
