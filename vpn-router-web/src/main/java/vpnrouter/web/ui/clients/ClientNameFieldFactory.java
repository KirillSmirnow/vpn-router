package vpnrouter.web.ui.clients;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.textfield.TextField;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import vpnrouter.api.client.ClientService;
import vpnrouter.web.model.Client;

@Component
@RequiredArgsConstructor
public class ClientNameFieldFactory {

    private final ClientService clientService;

    public TextField build(Client client, Runnable onUpdatedListener) {
        var nameField = new TextField();
        nameField.setValue(client.getName() != null ? client.getName() : "");
        nameField.setWidthFull();
        nameField.addKeyDownListener(Key.ENTER, event -> updateName(client, nameField.getValue(), onUpdatedListener));
        nameField.addKeyDownListener(Key.ESCAPE, event -> onUpdatedListener.run());
        return nameField;
    }

    private void updateName(Client client, String newName, Runnable onUpdatedListener) {
        var clientUpdate = client.withName(
                newName.isBlank() ? null : newName.trim()
        ).toClientUpdate();
        clientService.update(client.getIpAddress(), clientUpdate);
        onUpdatedListener.run();
    }
}
