package vpnrouter.api.exception;

import java.util.List;

public interface ThrowableToErrorsConverter {

    List<Error> convert(Throwable throwable);
}
