package vpnrouter.web.ui.clients.newclient;

import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import lombok.RequiredArgsConstructor;
import vpnrouter.web.model.Client;
import vpnrouter.web.validator.IpAddressValidator;
import vpnrouter.web.validator.NameValidator;

@RequiredArgsConstructor
public class AddClientDialogBinder extends Binder<Client.Wrapper> {
    private final IpAddressValidator ipAddressValidator;
    private final NameValidator nameValidator;

    public void bindIpAddressTextField(TextField ipAddressTextField) {
        this.forField(ipAddressTextField)
                .withValidator(ipAddressValidator)
                .bind(Client.Wrapper::getIpAddress, Client.Wrapper::setIpAddress);
    }

    public void bindNameTextField(TextField nameTextField) {
        this.forField(nameTextField)
                .withNullRepresentation("")
                .withValidator(nameValidator)
                .bind(Client.Wrapper::getName, Client.Wrapper::setName);
    }

    public void bindTunnelledCheckbox(Checkbox tunnelledCheckbox) {
        this.forField(tunnelledCheckbox)
                .bind(Client.Wrapper::isTunnelled, Client.Wrapper::setTunnelled);
    }
}
