package vpnrouter.api.validator;

import org.springframework.boot.autoconfigure.validation.ValidationAutoConfiguration;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Import;

@TestConfiguration
@Import({ValidatedComponent.class, ValidationAutoConfiguration.class})
public class ValidationTestConfiguration {
}
