package vpnrouter.core.exception;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.ElementKind;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import vpnrouter.api.exception.Error;
import vpnrouter.api.exception.ThrowableToErrorsConverter;

import java.util.List;

import static java.util.Comparator.comparing;

@Slf4j
@Component
public class ThrowableToErrorsConverterImpl implements ThrowableToErrorsConverter {

    @Override
    public List<Error> convert(Throwable throwable) {
        if (throwable instanceof UserException userException) {
            return convert(userException);
        }
        if (throwable instanceof ConstraintViolationException constraintViolationException) {
            return convert(constraintViolationException);
        }
        return convertDefault(throwable);
    }

    private List<Error> convert(ConstraintViolationException constraintViolationException) {
        return constraintViolationException.getConstraintViolations().stream()
                .map(this::convert)
                .sorted(comparing(Error::getField).thenComparing(Error::getUserMessage))
                .toList();
    }

    private Error convert(ConstraintViolation<?> violation) {
        return Error.builder()
                .userMessage(violation.getMessage())
                .field(extractFieldName(violation))
                .build();
    }

    private String extractFieldName(ConstraintViolation<?> violation) {
        var fieldName = new StringBuilder();
        violation.getPropertyPath().forEach(node -> {
            if (node.getKind() == ElementKind.PROPERTY) {
                fieldName.append(node.getName()).append(".");
            }
        });
        return fieldName.deleteCharAt(fieldName.length() - 1).toString();
    }

    private List<Error> convert(UserException userException) {
        return List.of(
                Error.builder()
                        .userMessage(userException.getMessage())
                        .build()
        );
    }

    private List<Error> convertDefault(Throwable throwable) {
        log.warn("Unexpected error: {}", throwable.getMessage(), throwable);
        return List.of(
                Error.builder()
                        .userMessage("Unexpected error \uD83D\uDE32")
                        .developerMessage(throwable.getMessage())
                        .build()
        );
    }
}
