package vpnrouter.web.ui.clients.newclient;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.icon.VaadinIcon;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import vpnrouter.api.client.ClientService;
import vpnrouter.web.validator.IpAddressValidator;
import vpnrouter.web.validator.NameValidator;

@Component
@RequiredArgsConstructor
public class AddClientButtonFactory {
    private final ClientService clientService;
    private final IpAddressValidator ipAddressValidator;
    private final NameValidator nameValidator;

    public Button build() {
        var button = new Button(VaadinIcon.PLUS.create());
        button.addClickListener(event -> {
            var dialog = new AddClientDialog(
                    clientService,
                    new AddClientDialogBinder(ipAddressValidator, nameValidator)
            );
            dialog.open();
        });
        return button;
    }
}
