package vpnrouter.web.validator;

import com.vaadin.flow.data.binder.ValidationResult;
import com.vaadin.flow.data.binder.Validator;
import com.vaadin.flow.data.binder.ValueContext;
import org.springframework.stereotype.Component;
import vpnrouter.api.client.ClientConstraints;

@Component
public class NameValidator implements Validator<String> {
    @Override
    public ValidationResult apply(String value, ValueContext context) {
        if (value == null) {
            return ValidationResult.ok();
        }
        if (value.length() < ClientConstraints.Name.MIN || value.length() > ClientConstraints.Name.MAX) {
            return ValidationResult.error(
                    "Name length should be from %d to %d"
                            .formatted(ClientConstraints.Name.MIN, ClientConstraints.Name.MAX)
            );
        }
        return ValidationResult.ok();
    }
}
