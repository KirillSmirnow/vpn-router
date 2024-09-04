package vpnrouter.web;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.*;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.UIScope;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.springframework.stereotype.Component;
import vpnrouter.api.client.ClientCreation;
import vpnrouter.api.client.ClientService;

@UIScope
@Component
@PageTitle("Add Client")
@Route("/clients/add")
public class AddClientView extends VerticalLayout {

    private final ClientService clientService;

    private final TextField ipAddressField;
    private final TextField nameField;
    private final Checkbox tunnelledCheckbox;
    private final Button addButton;
    private final Button cancelButton;

    public AddClientView(ClientService clientService) {
        this.clientService = clientService;
        Binder<ClientCreation> binder = new Binder<>(ClientCreation.class);
        ipAddressField = new TextField("IP address", "192.168.0.123");
        ipAddressField.setRequired(true);
        binder.forField(ipAddressField)
                .withValidator(new IpAddressValidator())
                .bind(ClientCreation::getIpAddress, ClientCreation::setIpAddress);
        nameField = new TextField("Name", "My Laptop");
        binder.forField(nameField)
                .withValidator(new NameValidator())
//                .withValidator(this::validateName)
                .bind(ClientCreation::getName, ClientCreation::setName);
        nameField.setRequired(true);
        tunnelledCheckbox = new Checkbox("Tunnelled", true);
        addButton = new Button("Add");
        addButton.addClickListener(
                event -> {
                    var clientCreation = ClientCreation.builder()
                            .ipAddress(ipAddressField.getOptionalValue().orElse(null))
                            .name(nameField.getOptionalValue().orElse(null))
                            .tunnelled(tunnelledCheckbox.getValue())
                            .build();
                    try {
                        if (binder.validate().isOk()) {
                            binder.writeBean(clientCreation);
                            clientService.add(clientCreation);
                        }
                    } catch (ValidationException e) {
                        System.out.println(e.getMessage());
                    }
                }
        );
        cancelButton = new Button("Cancel", event -> getUI().ifPresent(ui -> ui.navigate("/clients")));
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

    private ValidationResult validateIpAddress(String ipAddress, ValueContext context) {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        ClientCreation clientCreation = ClientCreation.builder().ipAddress(ipAddress).build();
        if (validator.validate(clientCreation).isEmpty()) {
            return ValidationResult.ok();
        } else {
            return ValidationResult.error("Bad ip");
        }
    }

    private ValidationResult validateName(String name, ValueContext context) {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        ClientCreation clientCreation = ClientCreation.builder().name(name).build();
        if (validator.validate(clientCreation).isEmpty()) {
            return ValidationResult.ok();
        } else {
            return ValidationResult.error("Bad name");
        }
    }
}
