package vpnrouter.web.ui.clients.newclient;

import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import lombok.SneakyThrows;
import vpnrouter.api.client.ClientService;
import vpnrouter.web.model.Client;

@CssImport("./styles/styles.css")
public class AddClientDialog extends Dialog {
    private final ClientService clientService;
    private final AddClientDialogBinder binder;
    private final ButtonsLayoutBuilder buttonsLayoutBuilder;

    private final TextField ipAddressField;
    private final TextField nameField;
    private final Checkbox tunnelledCheckbox;

    public AddClientDialog(ClientService clientService, AddClientDialogBinder binder) {
        this.clientService = clientService;
        this.binder = binder;
        this.buttonsLayoutBuilder = new ButtonsLayoutBuilder(this::validateAndAdd, this::close);

        setStyle();

        ipAddressField = new TextField("IP address", "192.168.0.123");
        ipAddressField.setRequired(true);
        nameField = new TextField("Name", "My Laptop");
        tunnelledCheckbox = new Checkbox("Tunnelled", true);

        setBinder();

        add(new VerticalLayout(ipAddressField, nameField, tunnelledCheckbox));
    }

    private void setStyle() {
        setHeaderTitle("Add Client");
        setDraggable(true);
        getFooter().add(buttonsLayoutBuilder.build());
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
