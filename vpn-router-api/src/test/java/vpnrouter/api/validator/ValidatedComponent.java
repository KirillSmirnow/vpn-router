package vpnrouter.api.validator;

import jakarta.validation.Valid;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

@Validated
@Component
public class ValidatedComponent {

    public void validate(@Valid Object object) {
    }
}
