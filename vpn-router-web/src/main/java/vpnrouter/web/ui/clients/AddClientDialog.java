package vpnrouter.web.ui.clients;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.binder.ValidationResult;
import org.springframework.stereotype.Component;
import vpnrouter.api.client.ClientService;
import vpnrouter.web.model.Client;
import vpnrouter.web.validator.IpAddressValidator;
import vpnrouter.web.validator.NameValidator;

@Component
@CssImport("./styles/styles.css")
public class AddClientDialog extends Dialog {
    private final ClientService clientService;
    private final IpAddressValidator ipAddressValidator;
    private final NameValidator nameValidator;

    private final TextField ipAddressField;
    private final TextField nameField;
    private final Checkbox tunnelledCheckbox;
    private final Button addButton;
    private final Button cancelButton;

    public AddClientDialog(ClientService clientService) {
        this.clientService = clientService;
        this.ipAddressValidator = new IpAddressValidator();
        this.nameValidator = new NameValidator();

        setHeaderTitle("Add Client");
        setDraggable(true);
        ipAddressField = new TextField("IP address", "192.168.0.123");
        ipAddressField.setRequired(true);
        nameField = new TextField("Name", "My Laptop");
        tunnelledCheckbox = new Checkbox("Tunnelled", true);
        var binder = buildBinder();
        addButton = new Button("Add", event -> validateAndAdd(binder));
        addButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        cancelButton = new Button("Cancel", event -> close());
        cancelButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
        add(new VerticalLayout(ipAddressField, nameField, tunnelledCheckbox));
        getFooter().add(addButton, cancelButton);
    }

    private Binder<Client.Wrapper> buildBinder() {
        var binder = new Binder<>(Client.Wrapper.class);
        binder.forField(ipAddressField)
                .withValidator(ipAddressValidator)
                .bind(Client.Wrapper::getIpAddress, Client.Wrapper::setIpAddress);
        binder.forField(nameField)
                .withValidator(nameValidator)
                .bind(Client.Wrapper::getName, Client.Wrapper::setName);
        return binder;
    }

    private void validateAndAdd(Binder<Client.Wrapper> binder) {
        var clientWrapper = new Client.Wrapper();
        try {
            if (binder.validate().isOk()) {
                binder.writeBean(clientWrapper);
                var client = clientWrapper.build();
                clientService.add(client.toClientCreation());
                Notification.show("Client added");
                close();
            }
        } catch (ValidationException e) {
            Notification.show("Validation failed: " + e.getMessage());
        }
    }

    @Override
    public void close() {
        super.close();
        ipAddressField.clear();
        nameField.clear();
        tunnelledCheckbox.setValue(true);
    }

}
