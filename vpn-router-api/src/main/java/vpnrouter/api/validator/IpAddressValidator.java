package vpnrouter.api.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import static java.lang.Integer.parseInt;

public class IpAddressValidator implements ConstraintValidator<IpAddress, String> {

    private static final String PATTERN = PatternBuilder.build();

    @Override
    public boolean isValid(String ipAddress, ConstraintValidatorContext context) {
        return ipAddress.matches(PATTERN) && areGroupsValid(ipAddress);
    }

    private boolean areGroupsValid(String ipAddress) {
        var groups = ipAddress.split(PatternBuilder.GROUP_SEPARATOR);
        for (var group : groups) {
            var number = parseInt(group);
            if (number > 255) {
                return false;
            }
        }
        return true;
    }

    private static class PatternBuilder {
        private static final String GROUP_PATTERN = "[0-9]{1,3}";
        private static final String GROUP_SEPARATOR = "\\.";
        private static final int GROUP_COUNT = 4;

        private static String build() {
            var pattern = new StringBuilder(GROUP_PATTERN);
            for (var group = 0; group < GROUP_COUNT - 1; group++) {
                pattern.append(GROUP_SEPARATOR).append(GROUP_PATTERN);
            }
            return pattern.toString();
        }
    }
}
