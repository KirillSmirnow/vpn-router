package vpnrouter.web.ui.clients.newclient;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import lombok.SneakyThrows;
import vpnrouter.api.client.ClientService;
import vpnrouter.web.model.Client;

@CssImport("./styles/styles.css")
public class AddClientDialog extends Dialog {
    private final ClientService clientService;
    private final AddClientDialogBinder binder;

    private final TextField ipAddressField;
    private final TextField nameField;
    private final Checkbox tunnelledCheckbox;

    public AddClientDialog(ClientService clientService, AddClientDialogBinder binder) {
        this.clientService = clientService;
        this.binder = binder;

        setStyle();

        ipAddressField = new TextField("IP address", "192.168.0.123");
        ipAddressField.setRequired(true);
        nameField = new TextField("Name", "My Laptop");
        tunnelledCheckbox = new Checkbox("Tunnelled", true);

        setBinder();

        var addButton = new Button("Add");
        addButton.addClickListener(event -> validateAndAdd());
        addButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        var cancelButton = new Button("Cancel", event -> close());
        cancelButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
        add(new VerticalLayout(ipAddressField, nameField, tunnelledCheckbox));
        HorizontalLayout buttonLayout = new HorizontalLayout(addButton, cancelButton);
        buttonLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        buttonLayout.setWidthFull();
        getFooter().add(buttonLayout);
    }

    private void setStyle() {
        setHeaderTitle("Add Client");
        setDraggable(true);
    }

    private void setBinder() {
        binder.bindIpAddressTextField(ipAddressField);
        binder.bindNameTextField(nameField);
        binder.bindTunnelledCheckbox(tunnelledCheckbox);
    }

    @SneakyThrows
    private void validateAndAdd() {
        if (binder.validate().isOk()) {
            var clientWrapper = new Client.Wrapper();
            binder.writeBean(clientWrapper);
            clientService.add(clientWrapper.build().toClientCreation());
            Notification.show("Client added");
            close();
        }
    }
}
