package vpnrouter.web.ui;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.UIScope;
import org.springframework.stereotype.Component;
import vpnrouter.api.client.ClientCreation;
import vpnrouter.api.client.ClientService;
import vpnrouter.web.ui.clients.ClientsPage;

@UIScope
@Component
@PageTitle("Add Client")
@Route("/clients/add")
public class AddClientPage extends VerticalLayout {

    private final ClientService clientService;

    private final TextField ipAddressField;
    private final TextField nameField;
    private final Checkbox tunnelledCheckbox;
    private final Button addButton;
    private final Button cancelButton;

    public AddClientPage(ClientService clientService) {
        this.clientService = clientService;
        ipAddressField = new TextField("IP address", "192.168.0.123");
        ipAddressField.setRequired(true);
        nameField = new TextField("Name", "My Laptop");
        tunnelledCheckbox = new Checkbox("Tunnelled", true);
        addButton = new Button("Add", $ -> {
            addClient();
            getUI().ifPresent(ui -> ui.navigate(ClientsPage.class));
            Notification.show("Client added");
        });
        cancelButton = new Button("Cancel", event -> getUI().ifPresent(ui -> ui.navigate(ClientsPage.class)));
        add(ipAddressField, nameField, tunnelledCheckbox, addButton, cancelButton);
    }

    private void addClient() {
        var clientCreation = ClientCreation.builder()
                .ipAddress(ipAddressField.getOptionalValue().orElse(null))
                .name(nameField.getOptionalValue().orElse(null))
                .tunnelled(tunnelledCheckbox.getValue())
                .build();
        clientService.add(clientCreation);
    }
}
