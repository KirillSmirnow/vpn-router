package vpnrouter.web;

import com.vaadin.flow.data.binder.ValidationResult;
import com.vaadin.flow.data.binder.Validator;
import com.vaadin.flow.data.binder.ValueContext;
import vpnrouter.api.client.ClientConstraints;

public class NameValidator implements Validator<String> {
    @Override
    public ValidationResult apply(String value, ValueContext context) {
        if (value == null || value.isEmpty()) {
            return ValidationResult.error("Name cannot be empty");
        }
        if (value.length() < ClientConstraints.Name.MIN || value.length() > ClientConstraints.Name.MAX) {
            return ValidationResult.error(
                    "The length of a name must be from %d to %d"
                            .formatted(ClientConstraints.Name.MIN, ClientConstraints.Name.MAX)
            );
        }
        return ValidationResult.ok();
    }
}
