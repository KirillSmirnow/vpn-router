package vpnrouter.web.validator;

import com.vaadin.flow.data.binder.ValidationResult;
import com.vaadin.flow.data.binder.Validator;
import com.vaadin.flow.data.binder.ValueContext;
import jakarta.validation.ConstraintValidator;
import org.springframework.stereotype.Component;
import vpnrouter.api.validator.IpAddress;

@Component
public class IpAddressValidator implements Validator<String> {
    private final ConstraintValidator<IpAddress, String> ipAddressValidator;

    public IpAddressValidator() {
        this.ipAddressValidator = new vpnrouter.api.validator.IpAddressValidator();
    }

    @Override
    public ValidationResult apply(String value, ValueContext context) {
        boolean isValid = ipAddressValidator.isValid(value, null);
        if (isValid) {
            return ValidationResult.ok();
        } else {
            return ValidationResult.error("Invalid IP address");
        }
    }
}
