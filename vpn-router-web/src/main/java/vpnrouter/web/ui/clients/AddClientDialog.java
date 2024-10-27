package vpnrouter.web.ui.clients;

import com.vaadin.flow.component.UI;
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
import com.vaadin.flow.spring.annotation.UIScope;
import org.springframework.stereotype.Component;
import vpnrouter.api.client.ClientService;
import vpnrouter.web.model.Client;
import vpnrouter.web.validator.IpAddressValidator;
import vpnrouter.web.validator.NameValidator;

@UIScope
@Component
@CssImport("./styles/styles.css")
public class AddClientDialog extends Dialog {
    private final ClientService clientService;
    private final IpAddressValidator ipAddressValidator;
    private final NameValidator nameValidator;

    private Binder<Client.Wrapper> binder;
    private final TextField ipAddressField;
    private final TextField nameField;
    private final Checkbox tunnelledCheckbox;

    public AddClientDialog(ClientService clientService) {
        this.clientService = clientService;
        this.ipAddressValidator = new IpAddressValidator();
        this.nameValidator = new NameValidator();
        binder = new Binder<>(Client.Wrapper.class);
        setHeaderTitle("Add Client");
        setDraggable(true);
        ipAddressField = new TextField("IP address", "192.168.0.123");
        ipAddressField.setRequired(true);
        nameField = new TextField("Name", "My Laptop");
        tunnelledCheckbox = new Checkbox("Tunnelled", true);
        binder = buildBinder();
        var addButton = new Button("Add");
        addButton.addClickListener(event -> validateAndAdd());
        addButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        var cancelButton = new Button("Cancel", event -> close());
        cancelButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
        add(new VerticalLayout(ipAddressField, nameField, tunnelledCheckbox));
        getFooter().add(addButton, cancelButton);
    }

    private Binder<Client.Wrapper> buildBinder() {
        binder.forField(ipAddressField)
                .withValidator(ipAddressValidator)
                .bind(Client.Wrapper::getIpAddress, Client.Wrapper::setIpAddress);
        binder.forField(nameField)
                .withValidator(nameValidator)
                .bind(Client.Wrapper::getName, Client.Wrapper::setName);
        binder.forField(tunnelledCheckbox)
                .bind(Client.Wrapper::isTunnelled, Client.Wrapper::setTunnelled);
        return binder;
    }

    private void validateAndAdd() {
        if (binder.validate().isOk()) {
            try {
                var clientWrapper = new Client.Wrapper();
                binder.writeBean(clientWrapper);
                var client = clientWrapper.build();
                clientService.add(client.toClientCreation());
                UI.getCurrent().access(() -> Notification.show("Client added"));
                close();
            } catch (ValidationException e) {
                UI.getCurrent().access(() -> Notification.show("Validation failed: " + e.getMessage()));
            }
        }
    }

    @Override
    public void open() {
        binder.removeBean();
        var clientWrapper = new Client.Wrapper();
        clientWrapper.setTunnelled(true);
        binder.readBean(clientWrapper);
        super.open();
    }

    @Override
    public void close() {
        ipAddressField.clear();
        nameField.clear();
        super.close();
    }

}
