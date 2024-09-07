package vpnrouter.api.location;

import java.util.Optional;

public interface LocationService {

    Optional<String> getLocation(String ipAddress);
}
