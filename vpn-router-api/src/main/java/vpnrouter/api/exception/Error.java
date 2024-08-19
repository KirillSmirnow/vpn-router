package vpnrouter.api.exception;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Error {
    private final String userMessage;
    private final String developerMessage;
    private final String field;
}
