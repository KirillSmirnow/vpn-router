package vpnrouter.api.validator;

import jakarta.validation.ConstraintViolationException;
import lombok.Data;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = ValidationTestConfiguration.class)
public class IpAddressValidatorTest {

    @Autowired
    private ValidatedComponent validatedComponent;

    private static Stream<String> validIpAddresses() {
        return Stream.of(
                "192.168.0.1",
                "0.0.0.0",
                "255.255.255.255"
        );
    }

    private static Stream<String> invalidIpAddresses() {
        return Stream.of(
                "192.168.0.345",
                "12.12.1",
                "",
                "123.123,123.4",
                "12a.0.0.0",
                "-1.2.4.5",
                "1.2.3.4.5",
                " "
        );
    }

    @ParameterizedTest
    @MethodSource("validIpAddresses")
    public void givenValidIpAddress_whenValidate_thenSuccess(String validIpAddress) {
        var ipAddressObject = new IpAddressObject(validIpAddress);

        assertDoesNotThrow(() -> validatedComponent.validate(ipAddressObject));
    }

    @ParameterizedTest
    @MethodSource("invalidIpAddresses")
    public void givenInvalidIpAddress_whenValidate_thenFailure(String invalidIpAddress) {
        var ipAddressObject = new IpAddressObject(invalidIpAddress);

        assertThrows(
                ConstraintViolationException.class,
                () -> validatedComponent.validate(ipAddressObject)
        );
    }

    @Data
    private static class IpAddressObject {
        @IpAddress
        private final String ipAddress;
    }
}
