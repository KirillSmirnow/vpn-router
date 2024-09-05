package vpnrouter.web.ui;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.UIScope;
import org.springframework.stereotype.Component;
import vpnrouter.api.client.ClientService;
import vpnrouter.web.model.Client;
import vpnrouter.web.validator.IpAddressValidator;
import vpnrouter.web.validator.NameValidator;

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
        Binder<Client.ClientWrapper> binder = buildBinder();
        addButton = new Button("Add", event -> validateAndAdd(binder));
        cancelButton = new Button("Cancel", event -> getUI().ifPresent(ui -> ui.navigate("")));
        add(ipAddressField, nameField, tunnelledCheckbox, addButton, cancelButton);
    }

    private Binder<Client.ClientWrapper> buildBinder() {
        Binder<Client.ClientWrapper> binder = new Binder<>(Client.ClientWrapper.class);
        binder.forField(ipAddressField)
                .withValidator(new IpAddressValidator())
                .bind(Client.ClientWrapper::getIpAddress, Client.ClientWrapper::setIpAddress);
        binder.forField(nameField)
                .withValidator(new NameValidator())
                .bind(Client.ClientWrapper::getName, Client.ClientWrapper::setName);
        return binder;
    }

    private void validateAndAdd(Binder<Client.ClientWrapper> binder) {
        Client.ClientWrapper clientBuilder = new Client.ClientWrapper();
        try {
            if (binder.validate().isOk()) {
                binder.writeBean(clientBuilder);
                Client client = clientBuilder.build();
                clientService.add(client.toClientCreation());
                getUI().ifPresent(ui -> ui.navigate(""));
                Notification.show("Client added");
            }
        } catch (ValidationException e) {
            Notification.show("Validation failed: " + e.getMessage());
        }
    }
}
