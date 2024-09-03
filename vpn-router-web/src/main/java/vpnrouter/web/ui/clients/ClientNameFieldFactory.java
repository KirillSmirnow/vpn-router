package vpnrouter.web.ui.clients;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import vpnrouter.api.client.ClientService;
import vpnrouter.api.client.ClientUpdate;
import vpnrouter.web.model.ClientWebView;

@Component
@RequiredArgsConstructor
public class ClientNameFieldFactory {

    private final ClientService clientService;

    public TextField build(ClientWebView client, Binder<ClientWebView> binder, Runnable onUpdatedListener) {
        var nameField = new TextField();
        nameField.setWidthFull();
        binder.forField(nameField).bind(ClientWebView::getName, ClientWebView::setName);
        nameField.addKeyDownListener(Key.ENTER, event -> updateName(client, nameField.getValue(), onUpdatedListener));
        nameField.addKeyDownListener(Key.ESCAPE, event -> onUpdatedListener.run());
        return nameField;
    }

    private void updateName(ClientWebView client, String newName, Runnable onUpdatedListener) {
        var clientUpdate = ClientUpdate.builder()
                .tunnelled(client.isTunnelled())
                .name(newName)
                .build();
        clientService.update(client.getIpAddress(), clientUpdate);
        onUpdatedListener.run();
    }
}
